import model.{PlayerContext, Move}
import model.CanBeEmpty.CanBeEmptyOps

import scala.annotation.tailrec

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

      @tailrec
      def iteratePlayerContext(playerContextOpt: Option[PlayerContext]): Unit = playerContextOpt match {
        case None =>
        case Some(playerContext) =>
          val playerHockeyists = playerContext.hockeyists

          if (playerHockeyists.length == teamSize) {
            val moves = List.fill(teamSize) { new Move() }
            playerHockeyists.zip(moves).foreach {
              case (Some(hockeyist), move) if playerContext.world.isDefined &&
                                              playerContext.world.puck.isDefined =>
                strategies(hockeyist.teammateIndex).move(hockeyist, playerContext.world, game.orNull, move)
              case _ =>
            }
            remoteProcessClient.writeMoves(moves)
          }
          iteratePlayerContext(remoteProcessClient.readPlayerContext())
      }
      iteratePlayerContext(remoteProcessClient.readPlayerContext())
    } finally {
      remoteProcessClient.close()
    }
  }
}

object Runner {
  def main(args: Array[String]): Unit = {
    val params =
      if (args.length == 3) { args }
      else { Array("127.0.0.1", "31001", "0000000000000000") }
    new Runner(params).run()
  }
}
