import java.io.{ByteArrayOutputStream, Closeable, IOException, UnsupportedEncodingException}
import java.net.Socket
import java.nio.{ByteBuffer, ByteOrder}

import model.{ActionType, Game, Hockeyist, HockeyistState, HockeyistType, Move, Player, PlayerContext, Puck, World}
import RemoteProcessClient.{BufferSizeBytes, IntegerSizeBytes, LongSizeBytes, MessageType, ProtocolByteOrder,
                            actionTypeFromByte, actionTypeToByte, ensureMessageType,
                            hockeyistStateFromByte, hockeyistTypeFromByte, messageTypeFromByte, messageTypeToByte}

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

  def writeToken(token: String): Unit = {
    writeEnum(MessageType.AuthenticationToken, messageTypeToByte)
    writeString(token)
    flush()
  }

  def readTeamSize(): Int = {
    ensureMessageType(readEnum(messageTypeFromByte), MessageType.TeamSize)
    readInt()
  }

  def writeProtocolVersion(): Unit = {
    writeEnum(MessageType.ProtocolVersion, messageTypeToByte)
    writeInt(1)
    flush()
  }

  def readGameContext(): Game = {
    ensureMessageType(readEnum(messageTypeFromByte), MessageType.GameContext)

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

  def readPlayerContext(): Option[PlayerContext] = {
    readEnum(messageTypeFromByte) match {
      case MessageType.GameOver => None
      case MessageType.PlayerContext =>
        if (readBoolean()) { Some(new PlayerContext(readHockeyists(), readWorld())) }
        else { None }
      case msgType: Any => throw new IllegalArgumentException(s"Received wrong message: $msgType.")
    }
  }

  def writeMoves(moves: List[Move]): Unit = {
    writeEnum(MessageType.Moves, messageTypeToByte)

    if (moves.isEmpty) {
      writeInt(-1)
    } else {
      writeInt(moves.length)

      moves.foreach { move =>
        writeBoolean(value = true)
        writeDouble(move.speedUp)
        writeDouble(move.turn)
        writeEnum(move.action, actionTypeToByte)
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

  private def readPlayers(): Vector[Option[Player]] = {
    val playerCount: Int = readInt()

    Vector.fill(playerCount) {
      if (readBoolean()) {
        Some(new Player(readLong(), readBoolean(), readString(), readInt(), readBoolean(),
          readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
          readBoolean(), readBoolean()))
      } else {
        None
      }
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
        readBoolean(), readEnum(hockeyistTypeFromByte), readInt(), readInt(), readInt(), readInt(),
        readDouble(), readEnum(hockeyistStateFromByte), readInt(), readInt(), readInt(), readInt(),
        readEnum(actionTypeFromByte), if (readBoolean()) Some(readInt()) else None)
    } else { Hockeyist.empty }
  }

  private def longToId(v: Long): Option[Long] = if (-1 == v) None else Some(v)

  private def readPuck(): Puck = {
    if (readBoolean()) {
      Puck(readLong(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), longToId(readLong()), longToId(readLong()))
    } else { Puck.empty }
  }

  private def readEnum[E](fromByte: Byte => E): E = fromByte(readBytes(1)(0))

  private def writeEnum[E](value: E, toByte: E => Byte): Unit = writeBytes(Array(toByte(value)))

  private def readString(): Option[String] = {
    try {
      val length: Int = readInt()
      if (length == -1) {
        None
      } else {
        Some(new String(readBytes(length), "UTF-8"))
      }
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

  private def readBoolean(): Boolean = readBytes(1)(0) != 0

  private def readBooleanArray(count: Int): Array[Boolean] = {
    val bytes: Array[Byte] = readBytes(count)
    bytes.map(0 != _)
  }

  private def writeBoolean(value: Boolean): Unit = {
    writeBytes(Array[Byte](if (value) 1 else 0))
  }

  private def readInt(): Int = {
    ByteBuffer.wrap(readBytes(IntegerSizeBytes)).order(ProtocolByteOrder).getInt
  }

  private def writeInt(value: Int): Unit = {
    writeBytes(ByteBuffer.allocate(IntegerSizeBytes).order(ProtocolByteOrder).putInt(value).array)
  }

  private def readLong(): Long = {
    ByteBuffer.wrap(readBytes(LongSizeBytes)).order(ProtocolByteOrder).getLong
  }

  private def writeLong(value: Long): Unit = {
    writeBytes(ByteBuffer.allocate(LongSizeBytes).order(ProtocolByteOrder).putLong(value).array)
  }

  private def readDouble(): Double = {
    java.lang.Double.longBitsToDouble(readLong())
  }

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

  private def writeBytes(bytes: Array[Byte]): Unit = {
    outputStreamBuffer.write(bytes)
  }

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

  private[RemoteProcessClient] def ensureMessageType(actualType: MessageType, expectedType: MessageType): Boolean =
    if (actualType != expectedType) {
      throw new IllegalArgumentException(s"Received wrong message [actual=$actualType, expected=$expectedType].")
    } else {
      true
    }

  // scalastyle:off magic.number
  def messageTypeToByte(value: MessageType): Byte = value match {
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
      case _ => throw new IllegalArgumentException(s"value: $value")
    }
  // scalastyle:on magic.number

  // scalastyle:off magic.number
  private[RemoteProcessClient] def hockeyistTypeFromByte(value: Byte): Option[HockeyistType] = (value: @switch) match {
    case 0 => Some(HockeyistType.Goalie)
    case 1 => Some(HockeyistType.Versatile)
    case 2 => Some(HockeyistType.Forward)
    case 3 => Some(HockeyistType.Defenceman)
    case 4 => Some(HockeyistType.Random)
    case _ => None
  }

  private[RemoteProcessClient] def hockeyistStateFromByte(value: Byte): Option[HockeyistState] = (value: @switch) match {
    case 0 => Some(HockeyistState.Active)
    case 1 => Some(HockeyistState.Swinging)
    case 2 => Some(HockeyistState.KnockedDown)
    case 3 => Some(HockeyistState.Resting)
    case _ => None
  }

  private[RemoteProcessClient] def actionTypeFromByte(value: Byte): Option[ActionType] = (value: @switch) match {
    case 0 => Some(ActionType.None)
    case 1 => Some(ActionType.TakePuck)
    case 2 => Some(ActionType.Swing)
    case 3 => Some(ActionType.Strike)
    case 4 => Some(ActionType.CancelStrike)
    case 5 => Some(ActionType.Pass)
    case 6 => Some(ActionType.Substitute)
    case _ => None
  }

  private[RemoteProcessClient] def actionTypeToByte(value: ActionType): Byte = value match {
    case ActionType.None => 0
    case ActionType.TakePuck => 1
    case ActionType.Swing => 2
    case ActionType.Strike => 3
    case ActionType.CancelStrike => 4
    case ActionType.Pass => 5
    case ActionType.Substitute => 6
  }
  // scalastyle:on magic.number
}
