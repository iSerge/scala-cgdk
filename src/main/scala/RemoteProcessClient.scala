import java.io.{ByteArrayOutputStream, Closeable, IOException, UnsupportedEncodingException}
import java.net.Socket
import java.nio.{ByteBuffer, ByteOrder}

import model.{ActionType, Game, Hockeyist, HockeyistState, HockeyistType, Move, Player, PlayerContext, Puck, World}
import RemoteProcessClient.{BufferSizeBytes, IntegerSizeBytes, LongSizeBytes, MessageType, ProtocolByteOrder,
                            actionTypeFromByte, actionTypeToInt, ensureMessageType,
                            hockeyistStateFromByte, hockeyistTypeFromByte, messageTypeFromByte, messageTypeToInt}

import scala.annotation.{tailrec, switch}

final class RemoteProcessClient(host: String, port: Int) extends Closeable {
  private val (socket, inputStream, outputStream) = {
    val socket = {
      val skt = new Socket(host, port)
      skt.setSendBufferSize(BufferSizeBytes)
      skt.setReceiveBufferSize(BufferSizeBytes)
      skt.setTcpNoDelay(true)
      skt
    }
    (socket, socket.getInputStream, socket.getOutputStream)
  }
  private val outputStreamBuffer = new ByteArrayOutputStream(BufferSizeBytes)

  @scala.inline
  def writeToken(token: String): Unit = {
    writeByte(messageTypeToInt(MessageType.AuthenticationToken))
    writeString(token)
    flush()
  }

  @scala.inline
  def readTeamSize(): Int = {
    ensureMessageType(messageTypeFromByte(readByte()), MessageType.TeamSize)
    readInt()
  }

  def writeProtocolVersion(): Unit = {
    writeByte(messageTypeToInt(MessageType.ProtocolVersion))
    writeInt(1)
    flush()
  }

  def readGameContext(): Game = {
    ensureMessageType(messageTypeFromByte(readByte()), MessageType.GameContext)

    if (readBoolean()) {
      new Game(readLong(), readInt(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readInt(), readInt(), readInt(), readInt(),
        readInt(), readInt(), readDouble(), readDouble(), readDouble(), readInt(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readInt(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readInt(), readInt(),
        readInt(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(),
        readInt(), readInt(), readDouble(), readDouble())
    } else { Game.empty }
  }

  def readPlayerContext(): PlayerContext = {
    messageTypeFromByte(readByte()) match {
      case MessageType.GameOver => PlayerContext.empty
      case MessageType.PlayerContext =>
        if (readBoolean()) { new PlayerContext(readHockeyists(), readWorld()) }
        else { PlayerContext.empty }
      case msgType: Any => throw new IllegalArgumentException(s"Received wrong message: $msgType.")
    }
  }

  def writeMoves(moves: List[Move]): Unit = {
    writeByte(messageTypeToInt(MessageType.Moves))

    if (moves.isEmpty) {
      writeInt(-1)
    } else {
      writeInt(moves.length)

      moves.foreach { move =>
        writeBoolean(value = true)
        writeDouble(move.speedUp)
        writeDouble(move.turn)
        writeByte(actionTypeToInt(move.action))
        move.action match {
          case ActionType.Pass =>
            writeDouble(move.passPower)
            writeDouble(move.passAngle)
          case ActionType.Substitute =>
            writeInt(move.teammateIndex.getOrElse(-1))
          case _ =>
        }
      }
    }
    flush()
  }

  def close(): Unit = socket.close()

  private def readWorld(): World = {
    if (readBoolean()) {
      new World(readInt(), readInt(), readDouble(), readDouble(), readPlayers(), readHockeyists(), readPuck())
    } else { World.empty }
  }

  private def readPlayers(): Vector[Player] = {
    val playerCount: Int = readInt()

    Vector.fill(playerCount) {
      if (readBoolean()) {
        new Player(readLong(), readBoolean(), readString(), readInt(), readBoolean(),
          readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
          readBoolean(), readBoolean())
      } else { Player.empty }
    }
  }

  private def readHockeyists(): Vector[Hockeyist] = {
    val hockeyistCount: Int = readInt()
    Vector.fill(hockeyistCount) { readHockeyist() }
  }

  private def readHockeyist(): Hockeyist = {
    if (readBoolean()) {
      new Hockeyist(readLong(), readLong(), readInt(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readBoolean(), hockeyistTypeFromByte(readByte()), readInt(), readInt(), readInt(), readInt(),
        readDouble(), hockeyistStateFromByte(readByte()), readInt(), readInt(), readInt(), readInt(),
        actionTypeFromByte(readByte()), if (readBoolean()) Some(readInt()) else None)
    } else { Hockeyist.empty }
  }

  @scala.inline
  private def longToId(v: Long): Option[Long] = if (-1 == v) None else Some(v)

  private def readPuck(): Puck = {
    if (readBoolean()) {
      Puck(readLong(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), longToId(readLong()), longToId(readLong()))
    } else { Puck.empty }
  }

  private def readString(): String = {
    try {
      val length: Int = readInt()
      if (length == -1) { "" }
      else { new String(readBytes(length), "UTF-8") }
    } catch {
      case e: UnsupportedEncodingException =>
        throw new IllegalArgumentException("UTF-8 is unsupported.", e)
    }
  }

  private def writeString(value: String): Unit = {
    try {
      val bytes = value.getBytes("UTF-8")
      writeInt(bytes.length)
      writeBytes(bytes)
    } catch {
      case e: UnsupportedEncodingException =>
        throw new IllegalArgumentException("UTF-8 is unsupported.", e)
    }
  }

  @scala.inline
  private def readBoolean(): Boolean = readByte() != 0

  @scala.inline
  private def readBooleanArray(count: Int): Array[Boolean] = {
    val bytes: Array[Byte] = readBytes(count)
    bytes.map(0 != _)
  }

  @scala.inline
  private def writeBoolean(value: Boolean): Unit = {
    writeBytes(Array[Byte](if (value) 1 else 0))
  }

  @scala.inline
  private def readInt(): Int = {
    ByteBuffer.wrap(readBytes(IntegerSizeBytes)).order(ProtocolByteOrder).getInt
  }

  @scala.inline
  private def writeInt(value: Int): Unit = {
    writeBytes(ByteBuffer.allocate(IntegerSizeBytes).order(ProtocolByteOrder).putInt(value).array)
  }

  @scala.inline
  private def readLong(): Long = {
    ByteBuffer.wrap(readBytes(LongSizeBytes)).order(ProtocolByteOrder).getLong
  }

  @scala.inline
  private def writeLong(value: Long): Unit = {
    writeBytes(ByteBuffer.allocate(LongSizeBytes).order(ProtocolByteOrder).putLong(value).array)
  }

  @scala.inline
  private def readDouble(): Double = {
    java.lang.Double.longBitsToDouble(readLong())
  }

  @scala.inline
  private def writeDouble(value: Double): Unit = {
    writeLong(java.lang.Double.doubleToLongBits(value))
  }

  private def readBytes(byteCount: Int): Array[Byte] = {
    def result(bytes: Array[Byte], offset: Int): Array[Byte] = {
      if (offset == byteCount) { bytes }
      else { throw new IOException(s"Can't read $byteCount bytes from input stream.") }
    }

    @tailrec
    def rb(offset: Int, bytes: Array[Byte]): Array[Byte] = {
      if (offset < byteCount) {
        val readByteCount = inputStream.read(bytes, offset, byteCount - offset)
        if (readByteCount != -1) {
          rb(offset + readByteCount, bytes)
        } else { result(bytes, offset) }
      } else { result(bytes, offset) }
    }
    rb(0, new Array[Byte](byteCount))
  }

  private def readByte(): Byte = {
    val byte = inputStream.read()
    if (byte == -1) { throw new IOException(s"Can't read 1 bytes from input stream.") }
    else { byte.asInstanceOf[Byte] }
  }

  @scala.inline
  private def writeBytes(bytes: Array[Byte]): Unit = outputStreamBuffer.write(bytes)

  @scala.inline
  private def writeByte(byte: Int): Unit = outputStreamBuffer.write(byte)

  private def flush(): Unit = {
    outputStream.write(outputStreamBuffer.toByteArray)
    outputStreamBuffer.reset()
    outputStream.flush()
  }
}

object RemoteProcessClient {
  private[RemoteProcessClient] val BufferSizeBytes: Int = 1 << 20
  private[RemoteProcessClient] val ProtocolByteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN
  private[RemoteProcessClient] val IntegerSizeBytes: Int = Integer.SIZE / java.lang.Byte.SIZE
  private[RemoteProcessClient] val LongSizeBytes: Int = java.lang.Long.SIZE / java.lang.Byte.SIZE

  sealed trait MessageType

  object MessageType {
    case object Unknown extends MessageType
    case object GameOver extends MessageType
    case object AuthenticationToken extends MessageType
    case object TeamSize extends MessageType
    case object ProtocolVersion extends MessageType
    case object GameContext extends MessageType
    case object PlayerContext extends MessageType
    case object Moves extends MessageType
  }

  @scala.inline
  private[RemoteProcessClient] def ensureMessageType(actualType: MessageType, expectedType: MessageType): Boolean =
    if (actualType != expectedType) {
      throw new IllegalArgumentException(s"Received wrong message [actual=$actualType, expected=$expectedType].")
    } else {
      true
    }

  // scalastyle:off magic.number
  def messageTypeToInt(value: MessageType): Int = value match {
    case MessageType.Unknown             => 0
    case MessageType.GameOver            => 1
    case MessageType.AuthenticationToken => 2
    case MessageType.TeamSize            => 3
    case MessageType.ProtocolVersion     => 4
    case MessageType.GameContext         => 5
    case MessageType.PlayerContext       => 6
    case MessageType.Moves               => 7
    case _                               => -1
  }

  def messageTypeFromByte(value: Byte): MessageType = (value: @switch) match {
      case 0 => MessageType.Unknown
      case 1 => MessageType.GameOver
      case 2 => MessageType.AuthenticationToken
      case 3 => MessageType.TeamSize
      case 4 => MessageType.ProtocolVersion
      case 5 => MessageType.GameContext
      case 6 => MessageType.PlayerContext
      case 7 => MessageType.Moves
      case _ => throw new IllegalArgumentException("messageTypeFromByte: " + value)
    }
  // scalastyle:on magic.number

  // scalastyle:off magic.number
  private[RemoteProcessClient] def hockeyistTypeFromByte(value: Byte): HockeyistType = (value: @switch) match {
    case 0 => HockeyistType.Goalie
    case 1 => HockeyistType.Versatile
    case 2 => HockeyistType.Forward
    case 3 => HockeyistType.Defenceman
    case 4 => HockeyistType.Random
    case _ => throw new IllegalArgumentException("hockeyistTypeFromByte: " + value)
  }

  private[RemoteProcessClient] def hockeyistStateFromByte(value: Byte): HockeyistState = (value: @switch) match {
    case 0 => HockeyistState.Active
    case 1 => HockeyistState.Swinging
    case 2 => HockeyistState.KnockedDown
    case 3 => HockeyistState.Resting
    case _ => throw new IllegalArgumentException("hockeyistStateFromByte:" + value)
  }

  private[RemoteProcessClient] def actionTypeFromByte(value: Byte): ActionType = (value: @switch) match {
    case 0 => ActionType.None
    case 1 => ActionType.TakePuck
    case 2 => ActionType.Swing
    case 3 => ActionType.Strike
    case 4 => ActionType.CancelStrike
    case 5 => ActionType.Pass
    case 6 => ActionType.Substitute
    case _ => ActionType.Unknown
  }

  private[RemoteProcessClient] def actionTypeToInt(value: ActionType): Int = value match {
    case ActionType.None         => 0
    case ActionType.TakePuck     => 1
    case ActionType.Swing        => 2
    case ActionType.Strike       => 3
    case ActionType.CancelStrike => 4
    case ActionType.Pass         => 5
    case ActionType.Substitute   => 6
    case ActionType.Unknown      => -1
  }
  // scalastyle:on magic.number
}
