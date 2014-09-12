package model

/**
 * Содержит данные о текущем состоянии игрока.
 */
class Player(id: Long, me: Boolean, name: Option[String], goalCount: Int, strategyCrashed: Boolean,
             netTop: Double, netLeft: Double, netBottom: Double, netRight: Double,
             netFront: Double, netBack: Double, justScoredGoal: Boolean, justMissedGoal: Boolean)
{

  /**
   * @return Возвращает уникальный идентификатор игрока.
   */
  def getId: Long = id

  /**
   * @return Возвращает `true` в том и только в том случае, если этот игрок ваш.
   */
  def isMe: Boolean = me

  /**
   * @return Возвращает имя игрока.
   */
  def getName: Option[String] = name

  /**
   * @return Возвращает количество шайб, заброшенных хоккеистами данного игрока в сетку противника.
   *         Шайбы, заброшенные во время состояния вне игры, не влияют на этот счётчик.
   */
  def getGoalCount: Int = goalCount

  /**
   * @return Возвращает специальный флаг --- показатель того, что стратегия игрока ''упала''.
   *         Более подробную информацию можно найти в документации к игре.
   */
  def isStrategyCrashed: Boolean = strategyCrashed

  /**
   * @return Возвращает ординату верхней штанги ворот.
   */
  def getNetTop: Double = netTop

  /**
   * @return Возвращает абсциссу левой границы ворот.
   */
  def getNetLeft: Double = netLeft

  /**
   * @return Возвращает ординату нижней штанги ворот.
   */
  def getNetBottom: Double = netBottom

  /**
   * @return Возвращает абсциссу правой границы ворот.
   */
  def getNetRight: Double = netRight

  /**
   * @return Возвращает абсциссу ближайшей к вратарю вертикальной границы ворот.
   *         Соответствует одному из значений [[model.Player#getNetLeft]] или [[model.Player#getNetRight]].
   */
  def getNetFront: Double = netFront

  /**
   * @return Возвращает абсциссу дальней от вратаря вертикальной границы ворот.
   *         Соответствует одному из значений [[model.Player#getNetLeft]] или [[model.Player#getNetRight]].
   */
  def getNetBack: Double = netBack

  /**
   * @return Возвращает `true` в том и только в том случае, если игрок только что забил гол.
   *         <p/>
   *         Вместе с установленным флагом { @code justMissedGoal} другого игрока означает,
   *         что сейчас состояние вне игры и новые голы не будут засчитаны.
   *         Длительность состояния вне игры составляет [[model.Game#getAfterGoalStateTickCount]] тиков.
   */
  def isJustScoredGoal: Boolean = justScoredGoal

  /**
   * @return Возвращает `true` в том и только в том случае, если игрок только что пропустил гол.
   *         <p/>
   *         Вместе с установленным флагом { @code justScoredGoal} другого игрока означает,
   *         что сейчас состояние вне игры и новые голы не будут засчитаны.
   *         Длительность состояния вне игры составляет [[model.Game#getAfterGoalStateTickCount]] тиков.
   */
  def isJustMissedGoal: Boolean = justMissedGoal
}

