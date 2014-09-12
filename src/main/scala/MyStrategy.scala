import model.{World, Hockeyist, Game, Move, ActionType}

final class MyStrategy extends Strategy {
  override def move(self: Hockeyist, world: World, game: Game, move: Move) {
    move.setSpeedUp(-1.0D)
    move.setTurn(math.Pi)
    move.setAction(ActionType.Strike)
  }
}
