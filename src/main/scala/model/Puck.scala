package model

/**
 * Класс, определяющий хоккейную шайбу. Содержит также все свойства юнита.
 */
class Puck(id: Long, mass: Double, radius: Double,
           x: Double, y: Double,
           speedX: Double, speedY: Double,
           ownerHockeyistId: Long, ownerPlayerId: Long)
  extends Unit(id, mass, radius, x, y, speedX, speedY, 0.0D, 0.0D)
{

  /**
   * @return Возвращает идентификатор хоккеиста, контролирующего шайбу, или `-1`.
   */
  def getOwnerHockeyistId: Long = ownerHockeyistId

  /**
   * @return Возвращает идентификатор игрока, контролирующего шайбу, или `-1`.
   */
  def getOwnerPlayerId: Long = ownerPlayerId
}

