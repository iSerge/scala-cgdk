package model

/**
 * Класс, определяющий хоккейную шайбу. Содержит также все свойства юнита.
 * @param id Возвращает уникальный идентификатор объекта.
 * @param mass Возвращает массу объекта в единицах массы.
 * @param radius Возвращает радиус объекта.
 * @param x Возвращает X-координату центра объекта. Ось абсцисс направлена слева направо.
 * @param y Возвращает Y-координату центра объекта. Ось ординат направлена свеху вниз.
 * @param speedX Возвращает X-составляющую скорости объекта. Ось абсцисс направлена слева направо.
 * @param speedY Возвращает Y-составляющую скорости объекта. Ось ординат направлена свеху вниз.
 * @param ownerHockeyistId Возвращает идентификатор хоккеиста, контролирующего шайбу, или `-1`.
 * @param ownerPlayerId Возвращает идентификатор игрока, контролирующего шайбу, или `-1`.
 */
class Puck(id: Long,
           mass: Double,
           radius: Double,
           x: Double,
           y: Double,
           speedX: Double,
           speedY: Double,
           val ownerHockeyistId: Long,
           val ownerPlayerId: Long)
  extends Unit(id, mass, radius, x, y, speedX, speedY, 0.0D, 0.0D)
