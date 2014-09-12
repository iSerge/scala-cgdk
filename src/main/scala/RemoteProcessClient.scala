import java.io.{IOException, UnsupportedEncodingException, ByteArrayOutputStream, Closeable}
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

import model.{ActionType, Move, PlayerContext, Game, World, Player, HockeyistType, HockeyistState, Hockeyist, Puck}

import scala.annotation.switch

final class RemoteProcessClient(host: String, port: Int) extends Closeable {
  private val BufferSizeBytes: Int = 1 << 20
  private val ProtocolByteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN
  private val IntegerSizeBytes: Int = Integer.SIZE / java.lang.Byte.SIZE
  private val LongSizeBytes: Int = java.lang.Long.SIZE / java.lang.Byte.SIZE

  private val socket = {
    val socket = new Socket(host, port)
    socket.setSendBufferSize(BufferSizeBytes)
    socket.setReceiveBufferSize(BufferSizeBytes)
    socket.setTcpNoDelay(true)
    socket
  }

  private val inputStream = socket.getInputStream
  private val outputStream = socket.getOutputStream
  private val outputStreamBuffer = new ByteArrayOutputStream(BufferSizeBytes)

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

    // scalastyle:off magic.number
    def toByte(value: MessageType):Byte = value match {
      case Unknown              => 0
      case GameOver             => 1
      case AuthenticationToken  => 2
      case TeamSize             => 3
      case ProtocolVersion      => 4
      case GameContext          => 5
      case PlayerContext        => 6
      case Moves                => 7
    }

    def fromByte(value: Byte): Option[MessageType] = (value: @switch) match {
      case 0 => Some(Unknown)
      case 1 => Some(GameOver)
      case 2 => Some(AuthenticationToken)
      case 3 => Some(TeamSize)
      case 4 => Some(ProtocolVersion)
      case 5 => Some(GameContext)
      case 6 => Some(PlayerContext)
      case 7 => Some(Moves)
      case _ => None
    }
    // scalastyle:on magic.number
  }


  def writeToken(token: String): Unit = {
    writeEnum(MessageType.AuthenticationToken, MessageType.toByte)
    writeString(token)
    flush()
  }

  def readTeamSize: Int = {
    ensureMessageType(readEnum(MessageType.fromByte), MessageType.TeamSize)
    readInt
  }

  def writeProtocolVersion(): Unit = {
    writeEnum(MessageType.ProtocolVersion, MessageType.toByte)
    writeInt(1)
    flush()
  }

  def readGameContext: Option[Game] = {
    ensureMessageType(readEnum(MessageType.fromByte), MessageType.GameContext)

    if (readBoolean) Some(new Game(readLong, readInt, readDouble, readDouble, readDouble, readDouble,
      readDouble, readDouble, readDouble, readDouble, readDouble, readInt, readInt, readInt, readInt,
      readInt, readInt, readDouble, readDouble, readDouble, readInt, readDouble, readDouble, readDouble,
      readDouble, readDouble, readDouble, readInt, readDouble, readDouble, readDouble, readDouble,
      readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readDouble,
      readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readDouble,
      readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readInt, readInt,
      readInt, readInt, readInt, readInt, readInt, readInt, readInt, readInt, readInt, readInt,
      readInt, readInt, readDouble, readDouble))
    else None
  }

  def readPlayerContext: Option[PlayerContext] = {
    readEnum(MessageType.fromByte) match {
      case Some(MessageType.GameOver) => None
      case Some(MessageType.PlayerContext) =>
        if (readBoolean) Some(new PlayerContext(readHockeyists, readWorld))
        else None
      case msgType: Any => throw new IllegalArgumentException(s"Received wrong message: $msgType.")
    }
  }

  def writeMoves(moves: List[Move]): Unit = {
    writeEnum(MessageType.Moves, MessageType.toByte)

    if (moves.isEmpty) writeInt(-1)
    else {
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

  def close(): Unit = socket.close()

  private def readWorld: Option[World] = {
    if (readBoolean) Some(new World(readInt, readInt, readDouble, readDouble, readPlayers, readHockeyists, readPuck))
    else None
  }

  private def readPlayers: Vector[Option[Player]] = {
    val playerCount: Int = readInt

    Vector.fill(playerCount) {
      if (readBoolean) Some(new Player(readLong, readBoolean, readString, readInt, readBoolean,
        readDouble, readDouble, readDouble, readDouble, readDouble, readDouble,
        readBoolean, readBoolean))
      else None
    }
  }

  private def readHockeyists: Vector[Option[Hockeyist]] = {
    val hockeyistCount: Int = readInt
    Vector.fill(hockeyistCount) { readHockeyist }
  }

  // scalastyle:off magic.number
  private def hockeyistTypeFromByte(value: Byte): Option[HockeyistType] = (value: @switch) match {
    case 0 => Some(HockeyistType.Goalie)
    case 1 => Some(HockeyistType.Versatile)
    case 2 => Some(HockeyistType.Forward)
    case 3 => Some(HockeyistType.Defenceman)
    case 4 => Some(HockeyistType.Random)
    case _ => None
  }

  private def hockeyistStateFromByte(value: Byte): Option[HockeyistState] = (value: @switch) match {
    case 0 => Some(HockeyistState.Active)
    case 1 => Some(HockeyistState.Swinging)
    case 2 => Some(HockeyistState.KnockedDown)
    case 3 => Some(HockeyistState.Resting)
    case _ => None
  }

  private def actionTypeFromByte(value: Byte): Option[ActionType] = (value: @switch) match {
    case 0 => Some(ActionType.None)
    case 1 => Some(ActionType.TakePuck)
    case 2 => Some(ActionType.Swing)
    case 3 => Some(ActionType.Strike)
    case 4 => Some(ActionType.CancelStrike)
    case 5 => Some(ActionType.Pass)
    case 6 => Some(ActionType.Substitute)
    case _ => None
  }

  private def actionTypeToByte(value: ActionType): Byte = value match {
    case ActionType.None          => 0
    case ActionType.TakePuck      => 1
    case ActionType.Swing         => 2
    case ActionType.Strike        => 3
    case ActionType.CancelStrike  => 4
    case ActionType.Pass          => 5
    case ActionType.Substitute    => 6
  }
  // scalastyle:on magic.number

  private def readHockeyist: Option[Hockeyist] = {
    if (readBoolean) Some(new Hockeyist(readLong, readLong, readInt, readDouble,
      readDouble, readDouble, readDouble, readDouble, readDouble, readDouble, readDouble,
      readBoolean, readEnum(hockeyistTypeFromByte), readInt, readInt, readInt, readInt,
      readDouble, readEnum(hockeyistStateFromByte), readInt, readInt, readInt, readInt,
      readEnum(actionTypeFromByte), if (readBoolean) Some(readInt) else None))
    else None
  }

  private def readPuck: Option[Puck] = {
    if (readBoolean) Some(new Puck(readLong, readDouble, readDouble, readDouble, readDouble,
      readDouble, readDouble, readLong, readLong))
    else None
  }

  private def readEnum[E](fromByte: Byte => Option[E]) = fromByte(readBytes(1)(0))

  private def writeEnum[E](value: E, toByte: E => Byte) = writeBytes(Array(toByte(value)))

  private def readString: Option[String] = {
    try {
      val length: Int = readInt
      if (length == -1) None
      else Some(new String(readBytes(length), "UTF-8"))
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

  private def readBoolean: Boolean = readBytes(1)(0) != 0

  private def readBooleanArray(count: Int): Array[Boolean] = {
    val bytes: Array[Byte] = readBytes(count)
    bytes.map(0 != _)
  }

  private def writeBoolean(value: Boolean): Unit = {
    writeBytes(Array[Byte](if (value) 1.asInstanceOf[Byte] else 0.asInstanceOf[Byte]))
  }

  private def readInt: Int = {
    ByteBuffer.wrap(readBytes(IntegerSizeBytes)).order(ProtocolByteOrder).getInt
  }

  private def writeInt(value: Int): Unit = {
    writeBytes(ByteBuffer.allocate(IntegerSizeBytes).order(ProtocolByteOrder).putInt(value).array)
  }

  private def readLong: Long = {
    ByteBuffer.wrap(readBytes(LongSizeBytes)).order(ProtocolByteOrder).getLong
  }

  private def writeLong(value: Long): Unit = {
    writeBytes(ByteBuffer.allocate(LongSizeBytes).order(ProtocolByteOrder).putLong(value).array)
  }

  private def readDouble: Double = {
    java.lang.Double.longBitsToDouble(readLong)
  }

  private def writeDouble(value: Double): Unit = {
    writeLong(java.lang.Double.doubleToLongBits(value))
  }

  private def readBytes(byteCount: Int): Array[Byte] = {
    val bytes = Stream.continually(inputStream.read).takeWhile(-1 != _).take(byteCount).map(_.toByte).toArray

    if (bytes.length != byteCount) {
      throw new IOException(s"Can't read $byteCount bytes from input stream.")
    }

    bytes
  }

  private def writeBytes(bytes: Array[Byte]): Unit = {
    outputStreamBuffer.write(bytes)
  }

  private def flush(): Unit = {
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
