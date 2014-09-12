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

object MyStrategy {
  private def getNearestOpponent(x: Double, y: Double, world: World): Option[Hockeyist] = {
    val hockeists = world.getHockeyists.collect({
      case Some(hockeyist) if !hockeyist.isTeammate && hockeyist.getType != HockeyistType.Goalie
        && hockeyist.getState != HockeyistState.KnockedDown && hockeyist.getState != HockeyistState.Resting
        => hockeyist
    })

    hockeists match {
      case Nil => None
      case _   => Some(hockeists.minBy { hockeyist => math.hypot(x - hockeyist.getX, y - hockeyist.getY)})
    }
  }

  private[MyStrategy] final val STRIKE_ANGLE = 1.0D * math.Pi / 180.0D
}

class MyStrategy extends Strategy {

  import MyStrategy.{STRIKE_ANGLE, getNearestOpponent}

  def move(self: Hockeyist, world: World, game: Game, move: Move) = {
    (self.getState, world.getPuck) match {
      case (HockeyistState.Swinging, _) => move.setAction(ActionType.Strike)
      case (_, Some(puck)) =>
        if (puck.getOwnerPlayerId == self.getPlayerId) {
          if (puck.getOwnerHockeyistId == self.getId) {
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
    for (nearestOpponent <- getNearestOpponent(self.getX, self.getY, world)) {
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
      opponentPlayer <- world.getOpponentPlayer
      netX = 0.5D * (opponentPlayer.getNetBack + opponentPlayer.getNetFront)
      netY = {
        val ny = 0.5D * (opponentPlayer.getNetBottom + opponentPlayer.getNetTop)
        (if (self.getY < ny) 0.5D else -0.5D) * game.getGoalNetHeight
      }
    } yield (netX, netY)

    val angleToNet = self.getAngleTo(netX, netY)
    move.setTurn(angleToNet)
    if (math.abs(angleToNet) < STRIKE_ANGLE) {
      move.setAction(ActionType.Swing)
    }
  }
}
```
