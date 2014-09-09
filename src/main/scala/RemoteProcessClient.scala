import java.io.{IOException, UnsupportedEncodingException, ByteArrayOutputStream, Closeable}
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

import model.{ActionType, Move, PlayerContext, Game, World, Player, HockeyistType, HockeyistState, Hockeyist, Puck}

final class RemoteProcessClient(host: String, port: Int) extends Closeable {
  val BUFFER_SIZE_BYTES: Int = 1 << 20
  val PROTOCOL_BYTE_ORDER: ByteOrder = ByteOrder.LITTLE_ENDIAN
  val INTEGER_SIZE_BYTES: Int = Integer.SIZE / java.lang.Byte.SIZE
  val LONG_SIZE_BYTES: Int = java.lang.Long.SIZE / java.lang.Byte.SIZE

  private val socket = new Socket(host, port)
  socket.setSendBufferSize(BUFFER_SIZE_BYTES)
  socket.setReceiveBufferSize(BUFFER_SIZE_BYTES)
  socket.setTcpNoDelay(true)

  private val inputStream = socket.getInputStream
  private val outputStream = socket.getOutputStream
  private val outputStreamBuffer = new ByteArrayOutputStream(BUFFER_SIZE_BYTES)

  sealed trait MessageType

  object MessageType{
    case object UNKNOWN extends MessageType
    case object GAME_OVER extends MessageType
    case object AUTHENTICATION_TOKEN extends MessageType
    case object TEAM_SIZE extends MessageType
    case object PROTOCOL_VERSION extends MessageType
    case object GAME_CONTEXT extends MessageType
    case object PLAYER_CONTEXT extends MessageType
    case object MOVES extends MessageType

    // scalastyle:off magic.number
    def toByte(value: MessageType):Byte = value match {
      case UNKNOWN              => 0
      case GAME_OVER            => 1
      case AUTHENTICATION_TOKEN => 2
      case TEAM_SIZE            => 3
      case PROTOCOL_VERSION     => 4
      case GAME_CONTEXT         => 5
      case PLAYER_CONTEXT       => 6
      case MOVES                => 7
      case _                    => -1
    }

    def fromByte(value: Byte): Option[MessageType] = value match {
      case 0 => Some(UNKNOWN)
      case 1 => Some(GAME_OVER)
      case 2 => Some(AUTHENTICATION_TOKEN)
      case 3 => Some(TEAM_SIZE)
      case 4 => Some(PROTOCOL_VERSION)
      case 5 => Some(GAME_CONTEXT)
      case 6 => Some(PLAYER_CONTEXT)
      case 7 => Some(MOVES)
      case _ => None
    }
    // scalastyle:on magic.number
  }


  def writeToken(token: String) {
    writeEnum(MessageType.AUTHENTICATION_TOKEN, MessageType.toByte)
    writeString(token)
    flush()
  }

  def readTeamSize: Int = {
    ensureMessageType(readEnum(MessageType.fromByte), MessageType.TEAM_SIZE)
    readInt
  }

  def writeProtocolVersion() {
    writeEnum(MessageType.PROTOCOL_VERSION, MessageType.toByte)
    writeInt(1)
    flush()
  }

  def readGameContext: Option[Game] = {
    ensureMessageType(readEnum(MessageType.fromByte), MessageType.GAME_CONTEXT)

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
      case Some(MessageType.GAME_OVER) => None
      case Some(MessageType.PLAYER_CONTEXT) =>
        if (readBoolean) Some(new PlayerContext(readHockeyists, readWorld))
        else None
      case msgType: Any => throw new IllegalArgumentException(s"Received wrong message: $msgType.")
    }
  }

  def writeMoves(moves: List[Move]) {
    writeEnum(MessageType.MOVES, MessageType.toByte)

    if (moves.isEmpty) writeInt(-1)
    else {
      writeInt(moves.length)

      moves.foreach { move =>
        writeBoolean(value = true)
        writeDouble(move.getSpeedUp)
        writeDouble(move.getTurn)
        writeEnum(move.getAction, actionTypeToByte)
        move.getAction match {
          case ActionType.PASS =>
            writeDouble(move.getPassPower)
            writeDouble(move.getPassAngle)
          case ActionType.SUBSTITUTE =>
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

  private def readWorld: Option[World] = {
    if (readBoolean) Some(new World(readInt, readInt, readDouble, readDouble, readPlayers, readHockeyists, readPuck))
    else None
  }

  private def readPlayers: List[Option[Player]] = {
    val playerCount: Int = readInt

    if (playerCount < 0) List.empty
    else List.range(0, playerCount).map(_ =>
      if (readBoolean) Some(new Player(readLong, readBoolean, readString, readInt, readBoolean,
        readDouble, readDouble, readDouble, readDouble, readDouble, readDouble,
        readBoolean, readBoolean))
      else None
    )
  }

  private def readHockeyists: List[Option[Hockeyist]] = {
    val hockeyistCount: Int = readInt

    if (hockeyistCount < 0) List.empty
    else List.range(0, hockeyistCount).map(_ => readHockeyist)
  }

  // scalastyle:off magic.number
  private def hockeyistTypeFromByte(value: Byte): Option[HockeyistType] = value match {
    case 0 => Some(HockeyistType.GOALIE)
    case 1 => Some(HockeyistType.VERSATILE)
    case 2 => Some(HockeyistType.FORWARD)
    case 3 => Some(HockeyistType.DEFENCEMAN)
    case 4 => Some(HockeyistType.RANDOM)
    case _ => None
  }

  private def hockeyistStateFromByte(value: Byte): Option[HockeyistState] = value match {
    case 0 => Some(HockeyistState.ACTIVE)
    case 1 => Some(HockeyistState.SWINGING)
    case 2 => Some(HockeyistState.KNOCKED_DOWN)
    case 3 => Some(HockeyistState.RESTING)
    case _ => None
  }

  private def actionTypeFromByte(value: Byte): Option[ActionType] = value match {
    case 0 => Some(ActionType.NONE)
    case 1 => Some(ActionType.TAKE_PUCK)
    case 2 => Some(ActionType.SWING)
    case 3 => Some(ActionType.STRIKE)
    case 4 => Some(ActionType.CANCEL_STRIKE)
    case 5 => Some(ActionType.PASS)
    case 6 => Some(ActionType.SUBSTITUTE)
    case _ => None
  }

  private def actionTypeToByte(value: ActionType): Byte = value match {
    case ActionType.NONE          => 0
    case ActionType.TAKE_PUCK     => 1
    case ActionType.SWING         => 2
    case ActionType.STRIKE        => 3
    case ActionType.CANCEL_STRIKE => 4
    case ActionType.PASS          => 5
    case ActionType.SUBSTITUTE    => 6
    case _                        => -1
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

  private def readBoolean: Boolean = readBytes(1)(0) != 0

  private def readBooleanArray(count: Int): Array[Boolean] = {
    val bytes: Array[Byte] = readBytes(count)
    bytes.map(0 != _)
  }

  private def writeBoolean(value: Boolean) {
    writeBytes(Array[Byte](if (value) 1.asInstanceOf[Byte] else 0.asInstanceOf[Byte]))
  }

  private def readInt: Int = {
    ByteBuffer.wrap(readBytes(INTEGER_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getInt
  }

  private def writeInt(value: Int) {
    writeBytes(ByteBuffer.allocate(INTEGER_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putInt(value).array)
  }

  private def readLong: Long = {
    ByteBuffer.wrap(readBytes(LONG_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getLong
  }

  private def writeLong(value: Long) {
    writeBytes(ByteBuffer.allocate(LONG_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putLong(value).array)
  }

  private def readDouble: Double = {
    java.lang.Double.longBitsToDouble(readLong)
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
