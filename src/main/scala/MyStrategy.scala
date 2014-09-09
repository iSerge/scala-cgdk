import model._
import java.lang.StrictMath.PI

final class MyStrategy extends Strategy {
  def move(self: Hockeyist, world: Option[World], game: Option[Game], move: Move) {
    move.setSpeedUp(-1.0D)
    move.setTurn(PI)
    move.setAction(ActionType.STRIKE)
  }
}

