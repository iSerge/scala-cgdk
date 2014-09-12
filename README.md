How to generate IDE projects
-----------------------------------------

For Intellij Idea run sbt command gen-idea:
```shell
sbt gen-idea
```

For Eclipse run sbt comand ecpilse:
```shell
sbt eclipse
```

[Quick-start simple strategy](http://russianaicup.ru/p/quick) ported to Scala
-----------------------------------------------------------------------------

```scala
import model.{Hockeyist, World, Game, Move, ActionType, HockeyistType, HockeyistState, Puck}
import MyStrategy.{StrikeAngle, getNearestOpponent}

object MyStrategy {
  private def getNearestOpponent(x: Double, y: Double, world: World): Option[Hockeyist] = {
    val hockeists = world.hockeyists.collect({
      case Some(hockeyist) if !hockeyist.teammate && hockeyist.hokeyistType.orNull != HockeyistType.Goalie
        && hockeyist.state.orNull != HockeyistState.KnockedDown && hockeyist.state.orNull != HockeyistState.Resting
      => hockeyist
    })

    if (hockeists.isEmpty) {
      None
    } else {
      Some(hockeists.minBy { hockeyist => math.hypot(x - hockeyist.x, y - hockeyist.y)})
    }
  }

  private[MyStrategy] final val StrikeAngle = 1.0D * math.Pi / 180.0D
}

class MyStrategy extends Strategy {

  def move(self: Hockeyist, world: World, game: Game, move: Move): Unit = {
    (self.state, world.puck) match {
      case (Some(HockeyistState.Swinging), _) => move.setAction(ActionType.Strike)
      case (_, Some(puck)) =>
        if (puck.ownerPlayerId == self.playerId) {
          if (puck.ownerHockeyistId == self.id) {
            drivePuck(self, world, game, move)
          } else {
            strikeNearestOpponent(self, world, game, move)
          }
        } else {
          moveToPuck(self, puck, move)
        }
      case _ =>
    }
  }

  private def strikeNearestOpponent(self: Hockeyist, world: World, game: Game, move: Move) {
    for (nearestOpponent <- getNearestOpponent(self.x, self.y, world)) {
      if (self.getDistanceTo(nearestOpponent) > game.getStickLength) {
        move.setSpeedUp(1.0D)
        move.setTurn(self.getAngleTo(nearestOpponent))
      }
      if (math.abs(self.getAngleTo(nearestOpponent)) < 0.5D * game.getStickSector) {
        move.setAction(ActionType.Strike)
      }
    }
  }

  private def moveToPuck(self: Hockeyist, puck: Puck, move: Move) {
    move.setSpeedUp(1.0D)
    move.setTurn(self.getAngleTo(puck))
    move.setAction(ActionType.TakePuck)
  }

  private def drivePuck(self: Hockeyist, world: World, game: Game, move: Move) {
    val Some((netX, netY)) = for {
      opponentPlayer <- world.opponentPlayer
      netX = 0.5D * (opponentPlayer.getNetBack + opponentPlayer.getNetFront)
      netY = {
        val ny = 0.5D * (opponentPlayer.getNetBottom + opponentPlayer.getNetTop)
        (if (self.y < ny) 0.5D else -0.5D) * game.getGoalNetHeight
      }
    } yield (netX, netY)

    val angleToNet = self.getAngleTo(netX, netY)
    move.setTurn(angleToNet)
    if (math.abs(angleToNet) < StrikeAngle) {
      move.setAction(ActionType.Swing)
    }
  }
}
```
