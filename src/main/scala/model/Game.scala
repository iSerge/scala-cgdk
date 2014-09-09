package model

/**
 * Предоставляет доступ к различным игровым константам.
 */
class Game(randomSeed: Long, tickCount: Int, worldWidth: Double, worldHeight: Double,
           goalNetTop: Double, goalNetWidth: Double, goalNetHeight: Double,
           rinkTop: Double, rinkLeft: Double, rinkBottom: Double, rinkRight: Double,
           afterGoalStateTickCount: Int, overtimeTickCount: Int, defaultActionCooldownTicks: Int,
           swingActionCooldownTicks: Int, cancelStrikeActionCooldownTicks: Int,
           actionCooldownTicksAfterLosingPuck: Int, stickLength: Double, stickSector: Double,
           passSector: Double, hockeyistAttributeBaseValue: Int, minActionChance: Double,
           maxActionChance: Double, strikeAngleDeviation: Double, passAngleDeviation: Double,
           pickUpPuckBaseChance: Double, takePuckAwayBaseChance: Double, maxEffectiveSwingTicks: Int,
           strikePowerBaseFactor: Double, strikePowerGrowthFactor: Double, strikePuckBaseChance: Double,
           knockdownChanceFactor: Double, knockdownTicksFactor: Double, maxSpeedToAllowSubstitute: Double,
           substitutionAreaHeight: Double, passPowerFactor: Double, hockeyistMaxStamina: Double,
           activeHockeyistStaminaGrowthPerTick: Double, restingHockeyistStaminaGrowthPerTick: Double,
           zeroStaminaHockeyistEffectivenessFactor: Double, speedUpStaminaCostFactor: Double,
           turnStaminaCostFactor: Double, takePuckStaminaCost: Double, swingStaminaCost: Double,
           strikeStaminaBaseCost: Double, strikeStaminaCostGrowthFactor: Double,
           cancelStrikeStaminaCost: Double, passStaminaCost: Double, goalieMaxSpeed: Double,
           hockeyistMaxSpeed: Double, struckHockeyistInitialSpeedFactor: Double,
           hockeyistSpeedUpFactor: Double, hockeyistSpeedDownFactor: Double,
           hockeyistTurnAngleFactor: Double, versatileHockeyistStrength: Int,
           versatileHockeyistEndurance: Int, versatileHockeyistDexterity: Int,
           versatileHockeyistAgility: Int, forwardHockeyistStrength: Int, forwardHockeyistEndurance: Int,
           forwardHockeyistDexterity: Int, forwardHockeyistAgility: Int, defencemanHockeyistStrength: Int,
           defencemanHockeyistEndurance: Int, defencemanHockeyistDexterity: Int,
           defencemanHockeyistAgility: Int, minRandomHockeyistParameter: Int,
           maxRandomHockeyistParameter: Int, struckPuckInitialSpeedFactor: Double,
           puckBindingRange: Double)
{
  /**
   * @return Возвращает некоторое число, которое ваша стратегия может использовать для инициализации генератора
   *         случайных чисел. Данное значение имеет рекомендательный характер, однако позволит более точно
   *         воспроизводить прошедшие игры.
   */
  def getRandomSeed: Long = randomSeed

  /**
   * @return Возвращает длительность игры в тиках.
   */
  def getTickCount: Int = tickCount

  /**
   * @return Возвращает ширину игрового мира.
   */
  def getWorldWidth: Double = worldWidth

  /**
   * @return Возвращает высоту игрового мира.
   */
  def getWorldHeight: Double = worldHeight

  /**
   * @return Возвращает ординату верхней штанги ворот.
   */
  def getGoalNetTop: Double = goalNetTop

  /**
   * @return Возвращает ширину ворот.
   */
  def getGoalNetWidth: Double = goalNetWidth

  /**
   * @return Возвращает высоту ворот.
   */
  def getGoalNetHeight: Double = goalNetHeight

  /**
   * @return Возвращает ординату верхней границы игрового поля.
   */
  def getRinkTop: Double = rinkTop

  /**
   * @return Возвращает абсциссу левой границы игрового поля.
   */
  def getRinkLeft: Double = rinkLeft

  /**
   * @return Возвращает ординату нижней границы игрового поля.
   */
  def getRinkBottom: Double = rinkBottom

  /**
   * @return Возвращает абсциссу правой границы игрового поля.
   */
  def getRinkRight: Double = rinkRight

  /**
   * @return Возвращает длительность состояния вне игры после гола.
   *         В течение этого времени новые забитые голы игнорируются,
   *         а действия не требуют затрат выносливости.
   */
  def getAfterGoalStateTickCount: Int = afterGoalStateTickCount

  /**
   * @return Возвращает длительность дополнительного времени.
   *         Дополнительное время наступает в случае ничейного счёта на момент окончания основного времени.
   *         Если за основное время не было забито ни одного гола, вратари обоих игроков убираются с поля.
   */
  def getOvertimeTickCount: Int = overtimeTickCount

  /**
   * @return Возвращает длительность задержки, применяемой к хоккеисту
   *         после совершения им большинства действий ({ @code move.action}).
   *         В течение этого времени хоккеист не может совершать новые действия.
   */
  def getDefaultActionCooldownTicks: Int = defaultActionCooldownTicks

  /**
   * @return Возвращает длительность задержки, применяемой к хоккеисту
   *         после совершения им действия замах ({ @code ActionType.SWING}).
   *         В течение этого времени хоккеист не может совершать новые действия.
   */
  def getSwingActionCooldownTicks: Int = swingActionCooldownTicks

  /**
   * @return Возвращает длительность задержки, применяемой к хоккеисту
   *         после отмены им удара ({ @code ActionType.CANCEL_STRIKE}).
   *         В течение этого времени хоккеист не может совершать новые действия.
   */
  def getCancelStrikeActionCooldownTicks: Int = cancelStrikeActionCooldownTicks

  /**
   * @return Возвращает длительность задержки, применяемой к хоккеисту
   *         в случае потери шайбы вследствие воздействия других хоккеистов.
   *         В течение этого времени хоккеист не может совершать действия.
   */
  def getActionCooldownTicksAfterLosingPuck: Int = actionCooldownTicksAfterLosingPuck

  /**
   * @return Возвращает длину клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
   *         если и только если расстояние от центра хоккеиста до центра объекта не превышает эту величину.
   */
  def getStickLength: Double = stickLength

  /**
   * @return Возвращает сектор клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
   *         если и только если угол между направлением хоккеиста и вектором из центра хоккеиста в центр объекта
   *         не превышает половину этой величины.
   */
  def getStickSector: Double = stickSector

  /**
   * @return Возвращает сектор, ограничивающий направление паса.
   */
  def getPassSector: Double = passSector

  /**
   * @return Возвращает базовое значение атрибута хоккеиста.
   *         Данная величина используется как коэффициент в различных игровых формулах.
   */
  def getHockeyistAttributeBaseValue: Int = hockeyistAttributeBaseValue

  /**
   * @return Возвращает минимальный шанс на совершение любого вероятностного действия.
   */
  def getMinActionChance: Double = minActionChance

  /**
   * @return Возвращает максимальный шанс на совершение любого вероятностного действия.
   */
  def getMaxActionChance: Double = maxActionChance

  /**
   * @return Возвращает стандартное отклонение распределения Гаусса для угла удара ({ @code ActionType.STRIKE})
   *         хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
   *         тем точнее его удар.
   */
  def getStrikeAngleDeviation: Double = strikeAngleDeviation

  /**
   * @return Возвращает стандартное отклонение распределения Гаусса для угла паса ({ @code ActionType.PASS})
   *         хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
   *         тем точнее его пас.
   */
  def getPassAngleDeviation: Double = passAngleDeviation

  /**
   * @return Возвращает базовый шанс подобрать шайбу, не контролируемую другим хоккеистом.
   *         Максимальный из атрибутов ловкость и подвижность хоккеиста увеличивает шанс на захват.
   *         Скорость шайбы уменьшает шанс на захват.
   */
  def getPickUpPuckBaseChance: Double = pickUpPuckBaseChance

  /**
   * @return Возвращает базовый шанс отнять шайбу у другого хоккеиста.
   *         Максимальный из атрибутов сила и ловкость хоккеиста, отнимающего шайбу, увеличивает шанс на захват.
   *         Максимальный из атрибутов стойкость и подвижность текущего владельца шайбы уменьшает шанс на её потерю.
   */
  def getTakePuckAwayBaseChance: Double = takePuckAwayBaseChance

  /**
   * @return Возвращает длительность замаха, после достижения которой сила удара не увеличивается.
   */
  def getMaxEffectiveSwingTicks: Int = maxEffectiveSwingTicks

  /**
   * @return Возвращает коэффициент силы удара без замаха.
   */
  def getStrikePowerBaseFactor: Double = strikePowerBaseFactor

  /**
   * @return Возвращает увеличение коэффициента силы удара за каждый тик замаха.
   *         Максимальное количество учитываемых тиков ограничено значением { @code maxEffectiveSwingTicks}.
   */
  def getStrikePowerGrowthFactor: Double = strikePowerGrowthFactor

  /**
   * @return Возвращает базовый шанс ударить шайбу. Базовый шанс не зависит от того,
   *         контролирует шайбу другой хоккеист или нет, однако на результирующий шанс удара
   *         по свободной и контролируемой шайбе влияют разные атрибуты хоккеиста
   *         (смотрите документацию к { @code pickUpPuckBaseChance} и { @code takePuckAwayBaseChance}).
   *         Если хоккеист, совершающий удар, контролирует шайбу, то вероятность удара всегда будет 100%.
   */
  def getStrikePuckBaseChance: Double = strikePuckBaseChance

  /**
   * @return Возвращает шанс ударом ({ @code ActionType.STRIKE}) сбить с ног другого хоккеиста при максимальной
   *         длительности замаха. Среднее значение атрибутов сила и ловкость хоккеиста, совершающего удар,
   *         увеличивает шанс сбить с ног. Значение атрибута стойкость атакуемого хоккеиста уменьшает шанс на падение.
   */
  def getKnockdownChanceFactor: Double = knockdownChanceFactor

  /**
   * @return Возвращает количество тиков, по прошествии которого хоккеист восстановится после падения при базовом
   *         значении атрибута подвижность. Чем выше подвижность, тем быстрее восстановление.
   */
  def getKnockdownTicksFactor: Double = knockdownTicksFactor

  /**
   * @return Возвращает максимальную допустимую скорость для выполнения замены хоккеиста.
   */
  def getMaxSpeedToAllowSubstitute: Double = maxSpeedToAllowSubstitute

  /**
   * @return Возвращает высоту зоны, в которой может быть выполнена замена хоккеиста. Зона расположена вдоль верхней
   *         границы игровой площадки. Замена может быть выполнена только на своей половине поля.
   */
  def getSubstitutionAreaHeight: Double = substitutionAreaHeight

  /**
   * @return Возвращает коэффициент силы паса. Умножается на устанавливаемое стратегией в интервале
   *         [{ @code 0.0}, { @code 1.0}] значение силы паса ({ @code move.passPower}).
   */
  def getPassPowerFactor: Double = passPowerFactor

  /**
   * @return Возвращает максимальное значение выносливости хоккеиста. Выносливость тратится на перемещение
   *         и совершение хоккеистом различных действий. Каждый тик может восстановиться небольшое количество
   *         выносливости в зависимости от состояния хоккеиста ({ @code hockeyist.state}). По мере расходования
   *         выносливости все атрибуты (соответственно, и эффективность всех действий) хоккеиста равномерно
   *         уменьшаются и достигают значения { @code zeroStaminaHockeyistEffectivenessFactor} (от начальных
   *         показателей) при падении выносливости до нуля. Хоккеист не восстанавливает выносливость в состояниях
   *         { @code HockeyistState.SWINGING} и { @code HockeyistState.KNOCKED_DOWN}.
   */
  def getHockeyistMaxStamina: Double = hockeyistMaxStamina

  /**
   * @return Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
   *         { @code HockeyistType.ACTIVE}.
   */
  def getActiveHockeyistStaminaGrowthPerTick: Double = activeHockeyistStaminaGrowthPerTick

  /**
   * @return Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
   *         { @code HockeyistType.RESTING}.
   */
  def getRestingHockeyistStaminaGrowthPerTick: Double = restingHockeyistStaminaGrowthPerTick

  /**
   * @return Возвращает коэффициент эффективности действий хоккеиста при падении его выносливости до нуля.
   */
  def getZeroStaminaHockeyistEffectivenessFactor: Double = zeroStaminaHockeyistEffectivenessFactor

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на максимальное по модулю
   *         ускорение/замедление хоккеиста ({ @code move.speedUp}) за 1 тик. Для меньших значений ускорения затраты
   *         выносливости пропорционально падают.
   */
  def getSpeedUpStaminaCostFactor: Double = speedUpStaminaCostFactor

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на максимальный по модулю
   *         угол поворота хоккеиста ({ @code move.turn}) за 1 тик. Для меньших значений угла поворота затраты
   *         выносливости пропорционально падают.
   */
  def getTurnStaminaCostFactor: Double = turnStaminaCostFactor

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
   *         { @code ActionType.TAKE_PUCK}.
   */
  def getTakePuckStaminaCost: Double = takePuckStaminaCost

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
   *         { @code ActionType.SWING}.
   */
  def getSwingStaminaCost: Double = swingStaminaCost

  /**
   * @return Возвращает базовое количество выносливости, которое необходимо затратить на совершение действия
   *         { @code ActionType.STRIKE}.
   */
  def getStrikeStaminaBaseCost: Double = strikeStaminaBaseCost

  /**
   * @return Возвращает увеличение затрат выносливости на удар ({ @code ActionType.STRIKE}) за каждый тик замаха.
   *         Максимальное количество учитываемых тиков ограничено значением { @code maxEffectiveSwingTicks}.
   */
  def getStrikeStaminaCostGrowthFactor: Double = strikeStaminaCostGrowthFactor

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
   *         { @code ActionType.CANCEL_STRIKE}.
   */
  def getCancelStrikeStaminaCost: Double = cancelStrikeStaminaCost

  /**
   * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
   *         { @code ActionType.PASS}.
   */
  def getPassStaminaCost: Double = passStaminaCost

  /**
   * @return Возвращает максимальную скорость перемещения вратаря.
   */
  def getGoalieMaxSpeed: Double = goalieMaxSpeed

  /**
   * @return Возвращает максимальную скорость перемещения полевого хоккеиста.
   */
  def getHockeyistMaxSpeed: Double = hockeyistMaxSpeed

  /**
   * @return Возвращает модуль скорости, добавляемой хоккеисту, попавшему под удар силы 1.0.
   */
  def getStruckHockeyistInitialSpeedFactor: Double = struckHockeyistInitialSpeedFactor

  /**
   * @return Возвращает модуль ускорения, приобретаемого хоккеистом, при { @code move.speedUp} равном 1.0,
   *         базовом значении атрибута подвижность и максимальном запасе выносливости.
   *         Направление ускорения совпадает с направлением хоккеиста.
   *         В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
   *         игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
   *         Чем больше скорость, тем на большую величину она уменьшается.
   */
  def getHockeyistSpeedUpFactor: Double = hockeyistSpeedUpFactor

  /**
   * @return Возвращает модуль ускорения, приобретаемого хоккеистом, при { @code move.speedUp} равном -1.0,
   *         базовом значении атрибута подвижность и максимальном запасе выносливости.
   *         Направление ускорения противоположно направлению хоккеиста.
   *         В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
   *         игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
   *         Чем больше скорость, тем на большую величину она уменьшается.
   */
  def getHockeyistSpeedDownFactor: Double = hockeyistSpeedDownFactor

  /**
   * @return Возвращает максимальный модуль угла поворота хоккеиста за тик при базовом значении атрибута подвижность
   *         и максимальном запасе выносливости.
   */
  def getHockeyistTurnAngleFactor: Double = hockeyistTurnAngleFactor

  /**
   * @return Возвращает значение атрибута сила для хоккеиста-универсала.
   */
  def getVersatileHockeyistStrength: Int = versatileHockeyistStrength

  /**
   * @return Возвращает значение атрибута стойкость для хоккеиста-универсала.
   */
  def getVersatileHockeyistEndurance: Int = versatileHockeyistEndurance

  /**
   * @return Возвращает значение атрибута ловкость для хоккеиста-универсала.
   */
  def getVersatileHockeyistDexterity: Int = versatileHockeyistDexterity

  /**
   * @return Возвращает значение атрибута подвижность для хоккеиста-универсала.
   */
  def getVersatileHockeyistAgility: Int = versatileHockeyistAgility

  /**
   * @return Возвращает значение атрибута сила для нападающего.
   */
  def getForwardHockeyistStrength: Int = forwardHockeyistStrength

  /**
   * @return Возвращает значение атрибута стойкость для нападающего.
   */
  def getForwardHockeyistEndurance: Int = forwardHockeyistEndurance

  /**
   * @return Возвращает значение атрибута ловкость для нападающего.
   */
  def getForwardHockeyistDexterity: Int = forwardHockeyistDexterity

  /**
   * @return Возвращает значение атрибута подвижность для нападающего.
   */
  def getForwardHockeyistAgility: Int = forwardHockeyistAgility

  /**
   * @return Возвращает значение атрибута сила для защитника.
   */
  def getDefencemanHockeyistStrength: Int = defencemanHockeyistStrength

  /**
   * @return Возвращает значение атрибута стойкость для защитника.
   */
  def getDefencemanHockeyistEndurance: Int = defencemanHockeyistEndurance

  /**
   * @return Возвращает значение атрибута ловкость для защитника.
   */
  def getDefencemanHockeyistDexterity: Int = defencemanHockeyistDexterity

  /**
   * @return Возвращает значение атрибута подвижность для защитника.
   */
  def getDefencemanHockeyistAgility: Int = defencemanHockeyistAgility

  /**
   * @return Возвращает минимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
   */
  def getMinRandomHockeyistParameter: Int = minRandomHockeyistParameter

  /**
   * @return Возвращает максимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
   */
  def getMaxRandomHockeyistParameter: Int = maxRandomHockeyistParameter

  /**
   * @return Возвращает модуль скорости, устанавливаемой шайбе, попавшей под удар силы 1.0.
   */
  def getStruckPuckInitialSpeedFactor: Double = struckPuckInitialSpeedFactor

  /**
   * @return Возвращает расстояние от центра хоккеиста, контролирующего шайбу, до центра шайбы.
   */
  def getPuckBindingRange: Double = puckBindingRange
}

