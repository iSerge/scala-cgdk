package model

final class PlayerContext(val hockeyists: Vector[Hockeyist],
                          val world: World)

object PlayerContext extends CanBeEmpty[PlayerContext]
