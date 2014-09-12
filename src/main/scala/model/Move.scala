package model

/**
 * Стратегия игрока может управлять хоккеистом посредством установки свойств объекта данного класса.
 */
class Move {
  private var speedUp: Double = .0
  private var turn: Double = .0
  private var action: ActionType = ActionType.None
  private var passPower: Double = 1.0D
  private var passAngle: Double = .0
  private var teammateIndex: Int = -1

  /**
   * @return Возвращает текущее ускорение хоккеиста.
   */
  def getSpeedUp: Double = speedUp

  /**
   * Устанавливает ускорение хоккеиста.
   * <p/>
   * Ускорение является относительным и должно лежать в интервале от `[-1.0, 1.0]`.
   * Значения, выходящие за указанный интервал, будут приведены к ближайшей его границе.
   */
  def setSpeedUp(speedUp: Double) {
    this.speedUp = speedUp
  }

  /**
   * @return Возвращает текущий угол поворота хоккеиста.
   */
  def getTurn: Double = turn

  /**
   * Устанавливает угол поворота хоккеиста.
   * <p/>
   * Угол поворота задаётся в радианах относительно текущего направления хоккеиста и для хоккеиста
   * с базовым значением атрибута подвижность и максимальным запасом выносливости ограничен
   * интервалом от -[[model.Game#getHockeyistTurnAngleFactor]] до [[model.Game#getHockeyistTurnAngleFactor]].
   * Значения, выходящие за указанный интервал, будут приведены к ближайшей его границе.
   * Положительные значения соответствуют повороту по часовой стрелке.
   */
  def setTurn(turn: Double) {
    this.turn = turn
  }

  /**
   * @return Возвращает текущее действие хоккеиста.
   */
  def getAction: ActionType = action

  /**
   * Устанавливает действие хоккеиста.
   */
  def setAction(action: ActionType) {
    this.action = action
  }

  /**
   * @return Возвращает текущую силу паса.
   */
  def getPassPower: Double = passPower

  /**
   * Устанавливает силу паса ([[model.ActionType.Pass]]).
   * <p/>
   * Сила паса является относительной величиной и должна лежать в интервале от `0.0` до `1.0`.
   * Значения, выходящие за указанный интервал, будут приведены к ближайшей его границе.
   * К значению реальной силы паса применяется также поправочный коэффициент [[model.Game#getPassPowerFactor]].
   */
  def setPassPower(passPower: Double) {
    this.passPower = passPower
  }

  /**
   * @return Возвращает текущее направление паса.
   */
  def getPassAngle: Double = passAngle

  /**
   * Устанавливает направление паса ([[model.ActionType.Pass]]).
   * <p/>
   * Направление паса задаётся в радианах относительно текущего направления хоккеиста
   * и должно лежать в интервале от `-0.5 * [[model.Game#getPassSector]]`
   * до `0.5 * [[model.Game#getPassSector]]`.
   * Значения, выходящие за указанный интервал, будут приведены к ближайшей его границе.
   */
  def setPassAngle(passAngle: Double) {
    this.passAngle = passAngle
  }

  /**
   * @return Возвращает текущий индекс хоккеиста, на которого будет произведена замена,
   *         или `-1`, если хоккеист не был указан.
   */
  def getTeammateIndex: Int = teammateIndex

  /**
   * Устанавливает индекс хоккеиста для выполнения замены ([[model.ActionType.Substitute]]).
   * <p/>
   * Индексация начинается с нуля. Значением по умолчанию является `-1`.
   * Если в команде игрока не существует хоккеиста с указанным индексом, то замена произведена не будет.
   */
  def setTeammateIndex(teammateIndex: Int) {
    this.teammateIndex = teammateIndex
  }
}

