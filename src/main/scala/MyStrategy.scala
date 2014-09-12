import model.{World, Hockeyist, Game, Move, ActionType}

final class MyStrategy extends Strategy {
  override def move(self: Hockeyist, world: World, game: Game, move: Move) {
    move.speedUp = -1.0D
    move.turn = math.Pi
    move.action = ActionType.Strike
  }
}
