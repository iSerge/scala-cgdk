package model

/**
 * Состояние хоккеиста.
 */
sealed trait HockeyistState

object HockeyistState{
  /**
   * Хоккеист находится на игровом поле.
   */
  case object Active extends HockeyistState

  /**
   * Хоккеист находится на игровом поле и делает замах клюшкой.
   * <p/>
   * Во время замаха стратегия не может управлять движением хоккеиста, а из действий доступны только
   * {@code ActionType.STRIKE} и {@code ActionType.CANCEL_STRIKE}.
   */
  case object Swinging extends HockeyistState

  /**
   * Хоккеист находится на игровом поле, но сбит с ног.
   * Стратегия игрока не может им управлять.
   */
  case object KnockedDown extends HockeyistState

  /**
   * Хоккеист отдыхает вне игрового поля.
   * Стратегия игрока не может им управлять.
   */
  case object Resting extends HockeyistState
}
