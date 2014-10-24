import model.{World, Hockeyist, Game, Move, ActionType}

final class MyStrategy extends Strategy {
  override def move(self: Hockeyist, world: World, game: Game): Move = {
    Move(speedUp = -1.0D, turn = Math.PI, action = ActionType.Strike)
  }
}
