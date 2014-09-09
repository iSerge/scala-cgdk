import model.Move

final class Runner(args: Array[String]) {
  private val remoteProcessClient= new RemoteProcessClient(args(0), Integer.parseInt(args(1)))
  private val token: String = args(2)

  def run() {
    try {
      remoteProcessClient.writeToken(token)
      val teamSize = remoteProcessClient.readTeamSize
      remoteProcessClient.writeProtocolVersion()
      val game = remoteProcessClient.readGameContext
      val strategies = Array.fill(teamSize) {new MyStrategy()}

      var playerContext = remoteProcessClient.readPlayerContext
      while (None != playerContext) {
        val playerHockeyists = playerContext.get.getHockeyists

        if (playerHockeyists.length == teamSize) {
          val moves = List.fill(teamSize){new Move()}
          playerHockeyists.zip(moves).foreach( {
            case (Some(hockeyist), move) =>
              val world = playerContext.flatMap(_.getWorld)
              strategies(hockeyist.getTeammateIndex).move(hockeyist, world, game, move)
            case _ =>
          })
          remoteProcessClient.writeMoves(moves)
        }
        playerContext = remoteProcessClient.readPlayerContext
      }
    }
    finally {
      remoteProcessClient.close()
    }
  }
}

object Runner {
  def main(args: Array[String]) {
    if (args.length == 3) {
      new Runner(args).run()
    }
    else {
      new Runner(Array[String]("127.0.0.1", "31001", "0000000000000000")).run()
    }
  }
}


