package model

import java.lang.StrictMath.{PI, atan2, hypot}

/**
 * Базовый класс для определения объектов (''юнитов'') на игровом поле.
 * @param id Возвращает уникальный идентификатор объекта.
 * @param mass Возвращает массу объекта в единицах массы.
 * @param radius Возвращает радиус объекта.
 * @param x Возвращает X-координату центра объекта. Ось абсцисс направлена слева направо.
 * @param y Возвращает Y-координату центра объекта. Ось ординат направлена свеху вниз.
 * @param speedX Возвращает X-составляющую скорости объекта. Ось абсцисс направлена слева направо.
 * @param speedY Возвращает Y-составляющую скорости объекта. Ось ординат направлена свеху вниз.
 * @param angle Возвращает угол поворота объекта в радианах. Нулевой угол соответствует направлению оси абсцисс.
 *              Положительные значения соответствуют повороту по часовой стрелке.
 * @param angularSpeed Возвращает скорость вращения объекта.
 *                     Положительные значения соответствуют вращению по часовой стрелке.
 */
abstract class Unit (val id: Long,
                     val mass: Double,
                     val radius: Double,
                     val x: Double,
                     val y: Double,
                     val speedX: Double,
                     val speedY: Double,
                     val angle: Double,
                     val angularSpeed: Double)
{
  /**
   * @param x X-координата точки.
   * @param y Y-координата точки.
   * @return Возвращает ориентированный угол `[-PI, PI]` между направлением
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
   * @return Возвращает ориентированный угол `[-PI, PI]` между направлением
   *         данного объекта и вектором из центра данного объекта к центру указанного объекта.
   */
  def getAngleTo(unit: Unit): Double = getAngleTo(unit.x, unit.y)

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
  def getDistanceTo(unit: Unit): Double = getDistanceTo(unit.x, unit.y)
}

