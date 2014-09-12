import model.Move

final class Runner(args: Array[String]) {
  private val remoteProcessClient = new RemoteProcessClient(args(0), Integer.parseInt(args(1)))
  private val token: String = args(2)

  def run(): Unit = {
    try {
      remoteProcessClient.writeToken(token)
      val teamSize = remoteProcessClient.readTeamSize()
      remoteProcessClient.writeProtocolVersion()
      val game = remoteProcessClient.readGameContext()
      val strategies = Array.fill(teamSize) {new MyStrategy()}

      var playerContext = remoteProcessClient.readPlayerContext()
      while (None != playerContext) {
        val playerHockeyists = playerContext.get.hockeyists

        if (playerHockeyists.length == teamSize) {
          val moves = List.fill(teamSize){new Move()}
          playerHockeyists.zip(moves).foreach( {
            case (Some(hockeyist), move) =>
              val world = playerContext.flatMap(_.world)
              strategies(hockeyist.teammateIndex).move(hockeyist, world.orNull, game.orNull, move)
            case _ =>
          })
          remoteProcessClient.writeMoves(moves)
        }
        playerContext = remoteProcessClient.readPlayerContext()
      }
    } finally {
      remoteProcessClient.close()
    }
  }
}

object Runner {
  def main(args: Array[String]): Unit = {
    val params =
      if (args.length == 3) {
        args
      } else {
        Array("127.0.0.1", "31001", "0000000000000000")
      }
    new Runner(params).run()
  }
}
