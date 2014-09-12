package model

final class PlayerContext(hockeyists: Vector[Option[Hockeyist]], world: Option[World])
{
  def getHockeyists: Vector[Option[Hockeyist]] = hockeyists

  def getWorld: Option[World] = world
}

