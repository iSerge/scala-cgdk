import java.io.{IOException, UnsupportedEncodingException, ByteArrayOutputStream, Closeable}
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

import model.{ActionType, Move, PlayerContext, Game, World, Player, HockeyistType, HockeyistState, Hockeyist, Puck}

final class RemoteProcessClient(host: String, port: Int) extends Closeable {
  import RemoteProcessClient._

  private val socket = new Socket(host, port)
  socket.setSendBufferSize(BUFFER_SIZE_BYTES)
  socket.setReceiveBufferSize(BUFFER_SIZE_BYTES)
  socket.setTcpNoDelay(true)

  private val inputStream = socket.getInputStream
  private val outputStream = socket.getOutputStream
  private val outputStreamBuffer = new ByteArrayOutputStream(BUFFER_SIZE_BYTES)

  def writeToken(token: String) {
    writeEnum(MessageType.AuthenticationToken, messageTypeToByte)
    writeString(token)
    flush()
  }

  def readTeamSize(): Int = {
    ensureMessageType(readEnum(messageTypeFromByte), MessageType.TeamSize)
    readInt()
  }

  def writeProtocolVersion() {
    writeEnum(MessageType.ProtocolVersion, messageTypeToByte)
    writeInt(1)
    flush()
  }

  def readGameContext(): Option[Game] = {
    ensureMessageType(readEnum(messageTypeFromByte), MessageType.GameContext)

    if (readBoolean())
    {
      Some(new Game(readLong(), readInt(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(),
        readDouble(), readDouble(), readDouble(), readInt(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readInt(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
        readDouble(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(),
        readInt(), readInt(), readInt(), readInt(), readInt(), readInt(), readInt(),
        readInt(), readDouble(), readDouble())
      )
    }
    else None
  }

  def readPlayerContext(): Option[PlayerContext] = {
    readEnum(messageTypeFromByte) match {
      case MessageType.GameOver => None
      case MessageType.PlayerContext =>
        if (readBoolean()) Some(new PlayerContext(readHockeyists(), readWorld()))
        else None
      case msgType: Any => throw new IllegalArgumentException(s"Received wrong message: $msgType.")
    }
  }

  def writeMoves(moves: List[Move]) {
    writeEnum(MessageType.Moves, messageTypeToByte)

    if (moves.isEmpty) {
      writeInt(-1)
    } else {
      writeInt(moves.length)

      moves.foreach { move =>
        writeBoolean(value = true)
        writeDouble(move.getSpeedUp)
        writeDouble(move.getTurn)
        writeEnum(move.getAction, actionTypeToByte)
        move.getAction match {
          case ActionType.Pass =>
            writeDouble(move.getPassPower)
            writeDouble(move.getPassAngle)
          case ActionType.Substitute =>
            writeInt(move.getTeammateIndex)
          case _ =>
        }
      }
    }
    flush()
  }

  def close() {
    socket.close()
  }

  private def readWorld(): Option[World] = {
    if (readBoolean())
    {
      Some(new World(readInt(), readInt(), readDouble(), readDouble(),
        readPlayers(), readHockeyists(), readPuck()))
    }
    else None
  }

  private def readPlayers(): List[Player] = {
    val playerCount: Int = readInt()

    if (playerCount < 0) List.empty
    else {
      List.range(0, playerCount).map(_ =>
        if (readBoolean()) Some(new Player(readLong(), readBoolean(), readString(), readInt(), readBoolean(),
          readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
          readBoolean(), readBoolean()))
        else None
      ).flatten
    }
  }

  private def readHockeyists(): List[Option[Hockeyist]] = {
    val hockeyistCount: Int = readInt()

    if (hockeyistCount < 0) List.empty
    else List.range(0, hockeyistCount).map(_ => readHockeyist())
  }

  private def readHockeyist(): Option[Hockeyist] = {
    if (readBoolean()) Some(new Hockeyist(readLong(), readLong(), readInt(), readDouble(),
      readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(), readDouble(),
      readBoolean(), readEnum(hockeyistTypeFromByte), readInt(), readInt(), readInt(), readInt(),
      readDouble(), readEnum(hockeyistStateFromByte), readInt(), readInt(), readInt(), readInt(),
      readEnum(actionTypeFromByte), if (readBoolean()) Some(readInt()) else None))
    else None
  }

  private def readPuck(): Option[Puck] = {
    if (readBoolean()) Some(new Puck(readLong(), readDouble(), readDouble(), readDouble(), readDouble(),
      readDouble(), readDouble(), readLong(), readLong()))
    else None
  }

  private def readEnum[E](fromByte: Byte => E): E = fromByte(readBytes(1)(0))

  private def writeEnum[E](value: E, toByte: E => Byte) = writeBytes(Array(toByte(value)))

  private def readString(): Option[String] = {
    try {
      val length: Int = readInt()

      if (length == -1) None
      else Some(new String(readBytes(length), "UTF-8"))
    }
    catch {
      case e: UnsupportedEncodingException =>
        throw new IllegalArgumentException("UTF-8 is unsupported.", e)
    }
  }

  private def writeString(value: String) {
    try {
      val bytes = value.getBytes("UTF-8")
      writeInt(bytes.length)
      writeBytes(bytes)
    }
    catch {
      case e: UnsupportedEncodingException =>
        throw new IllegalArgumentException("UTF-8 is unsupported.", e)
    }
  }

  private def readBoolean(): Boolean = readBytes(1)(0) != 0

  private def readBooleanArray(count: Int): Array[Boolean] = {
    val bytes: Array[Byte] = readBytes(count)
    bytes.map(0 != _)
  }

  private def writeBoolean(value: Boolean) {
    writeBytes(Array[Byte](if (value) 1.asInstanceOf[Byte] else 0.asInstanceOf[Byte]))
  }

  private def readInt(): Int = {
    ByteBuffer.wrap(readBytes(INTEGER_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getInt
  }

  private def writeInt(value: Int) {
    writeBytes(ByteBuffer.allocate(INTEGER_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putInt(value).array)
  }

  private def readLong(): Long = {
    ByteBuffer.wrap(readBytes(LONG_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getLong
  }

  private def writeLong(value: Long) {
    writeBytes(ByteBuffer.allocate(LONG_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putLong(value).array)
  }

  private def readDouble(): Double = {
    java.lang.Double.longBitsToDouble(readLong())
  }

  private def writeDouble(value: Double) {
    writeLong(java.lang.Double.doubleToLongBits(value))
  }

  private def readBytes(byteCount: Int): Array[Byte] = {
    val bytes = Stream.continually(inputStream.read).takeWhile(-1 != _).take(byteCount).map(_.toByte).toArray

    if (bytes.length != byteCount) {
      throw new IOException(s"Can't read $byteCount bytes from input stream.")
    }

    bytes
  }

  private def writeBytes(bytes: Array[Byte]) {
    outputStreamBuffer.write(bytes)
  }

  private def flush() {
    outputStream.write(outputStreamBuffer.toByteArray)
    outputStreamBuffer.reset()
    outputStream.flush()
  }

  private def ensureMessageType(actualType: Option[MessageType], expectedType: MessageType): Boolean =
    ensureMessageType(actualType.orNull, expectedType)

  private def ensureMessageType(actualType: MessageType, expectedType: MessageType): Boolean =
    if (actualType != expectedType)
      throw new IllegalArgumentException(s"Received wrong message [actual=$actualType, expected=$expectedType].")
    else true
}

object RemoteProcessClient{
  val BUFFER_SIZE_BYTES: Int = 1 << 20
  val PROTOCOL_BYTE_ORDER: ByteOrder = ByteOrder.LITTLE_ENDIAN
  val INTEGER_SIZE_BYTES: Int = Integer.SIZE / java.lang.Byte.SIZE
  val LONG_SIZE_BYTES: Int = java.lang.Long.SIZE / java.lang.Byte.SIZE

  sealed trait MessageType

  object MessageType{
    case object Unknown extends MessageType
    case object GameOver extends MessageType
    case object AuthenticationToken extends MessageType
    case object TeamSize extends MessageType
    case object ProtocolVersion extends MessageType
    case object GameContext extends MessageType
    case object PlayerContext extends MessageType
    case object Moves extends MessageType

  }

  // scalastyle:off magic.number
  import MessageType._
  def messageTypeToByte(value: MessageType):Byte = value match {
    case Unknown             => 0
    case GameOver            => 1
    case AuthenticationToken => 2
    case TeamSize            => 3
    case ProtocolVersion     => 4
    case GameContext         => 5
    case PlayerContext       => 6
    case Moves               => 7
    case _                   => -1
  }

  def messageTypeFromByte(value: Byte): MessageType = value match {
    case 0 => Unknown
    case 1 => GameOver
    case 2 => AuthenticationToken
    case 3 => TeamSize
    case 4 => ProtocolVersion
    case 5 => GameContext
    case 6 => PlayerContext
    case 7 => Moves
    case _ => null
  }

  import HockeyistType._

  private def hockeyistTypeFromByte(value: Byte): HockeyistType = value match {
    case 0 => Goalie
    case 1 => Versatile
    case 2 => Forward
    case 3 => Defenceman
    case 4 => Random
    case _ => null
  }

  import HockeyistState._

  private def hockeyistStateFromByte(value: Byte): HockeyistState = value match {
    case 0 => Active
    case 1 => Swinging
    case 2 => KnockedDown
    case 3 => Resting
    case _ => null
  }

  import ActionType._

  private def actionTypeFromByte(value: Byte): ActionType = value match {
    case 0 => None
    case 1 => TakePuck
    case 2 => Swing
    case 3 => Strike
    case 4 => CancelStrike
    case 5 => Pass
    case 6 => Substitute
    case _ => null
  }

  private def actionTypeToByte(value: ActionType): Byte = value match {
    case None         => 0
    case TakePuck     => 1
    case Swing        => 2
    case Strike       => 3
    case CancelStrike => 4
    case Pass         => 5
    case Substitute   => 6
    case _            => -1
  }
  // scalastyle:on magic.number
}
