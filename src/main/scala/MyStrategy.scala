import model.{World, Hockeyist, Game, Move, ActionType}

final class MyStrategy extends Strategy {
  override def move(self: Hockeyist, world: Option[World], game: Option[Game], move: Move): Unit = {
    move.setSpeedUp(-1.0D)
    move.setTurn(math.Pi)
    move.setAction(ActionType.Strike)
  }
}
