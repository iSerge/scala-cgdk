package model

final class PlayerContext(hockeyists: List[Option[Hockeyist]], world: Option[World])
{
  def getHockeyists: List[Option[Hockeyist]] = hockeyists

  def getWorld: Option[World] = world
}

