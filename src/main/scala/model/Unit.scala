package model

import java.lang.StrictMath._

/**
 * Базовый класс для определения объектов (<<юнитов>>) на игровом поле.
 */
abstract class Unit (id: Long, mass: Double, radius: Double,
                     x: Double, y: Double, speedX: Double, speedY: Double,
                     angle: Double, angularSpeed: Double)
{
  /**
   * @return Возвращает уникальный идентификатор объекта.
   */
  def getId: Long = id

  /**
   * @return Возвращает массу объекта в единицах массы.
   */
  def getMass: Double = mass

  /**
   * @return Возвращает радиус объекта.
   */
  def getRadius: Double = radius

  /**
   * @return Возвращает X-координату центра объекта. Ось абсцисс направлена слева направо.
   */
  final def getX: Double = x

  /**
   * @return Возвращает Y-координату центра объекта. Ось ординат направлена свеху вниз.
   */
  final def getY: Double = y

  /**
   * @return Возвращает X-составляющую скорости объекта. Ось абсцисс направлена слева направо.
   */
  final def getSpeedX: Double = speedX

  /**
   * @return Возвращает Y-составляющую скорости объекта. Ось ординат направлена свеху вниз.
   */
  final def getSpeedY: Double = speedY

  /**
   * @return Возвращает угол поворота объекта в радианах. Нулевой угол соответствует направлению оси абсцисс.
   *         Положительные значения соответствуют повороту по часовой стрелке.
   */
  final def getAngle: Double = angle

  /**
   * @return Возвращает скорость вращения объекта.
   *         Положительные значения соответствуют вращению по часовой стрелке.
   */
  def getAngularSpeed: Double = angularSpeed

  /**
   * @param x X-координата точки.
   * @param y Y-координата точки.
   * @return Возвращает ориентированный угол [{ @code -PI}, { @code PI}] между направлением
   *         данного объекта и вектором из центра данного объекта к указанной точке.
   */
  def getAngleTo(x: Double, y: Double): Double = {
    val absoluteAngleTo: Double = atan2(y - this.y, x - this.x)
    var relativeAngleTo: Double = absoluteAngleTo - angle

    while (relativeAngleTo > PI) {
      relativeAngleTo -= 2.0D * PI
    }

    while (relativeAngleTo < -PI) {
      relativeAngleTo += 2.0D * PI
    }

    relativeAngleTo
  }

  /**
   * @param unit Объект, к центру которого необходимо определить угол.
   * @return Возвращает ориентированный угол [{ @code -PI}, { @code PI}] между направлением
   *         данного объекта и вектором из центра данного объекта к центру указанного объекта.
   */
  def getAngleTo(unit: Unit): Double = getAngleTo(unit.getX, unit.getY)

  /**
   * @param x X-координата точки.
   * @param y Y-координата точки.
   * @return Возвращает расстояние до точки от центра данного объекта.
   */
  def getDistanceTo(x: Double, y: Double): Double = hypot(x - this.x, y - this.y)

  /**
   * @param unit Объект, до центра которого необходимо определить расстояние.
   * @return Возвращает расстояние от центра данного объекта до центра указанного объекта.
   */
  def getDistanceTo(unit: Unit): Double = getDistanceTo(unit.getX, unit.getY)
}

