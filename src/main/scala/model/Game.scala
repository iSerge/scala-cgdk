package model

/**
 * Предоставляет доступ к различным игровым константам.
 * @param randomSeed Возвращает некоторое число, которое ваша стратегия может использовать для инициализации генератора
 *                   случайных чисел. Данное значение имеет рекомендательный характер, однако позволит более точно
 *                   воспроизводить прошедшие игры.
 * @param tickCount Возвращает длительность игры в тиках.
 * @param worldWidth Возвращает ширину игрового мира.
 * @param worldHeight Возвращает высоту игрового мира.
 * @param goalNetTop Возвращает ординату верхней штанги ворот.
 * @param goalNetWidth Возвращает ширину ворот.
 * @param goalNetHeight Возвращает высоту ворот.
 * @param rinkTop Возвращает ординату верхней границы игрового поля.
 * @param rinkLeft Возвращает абсциссу левой границы игрового поля.
 * @param rinkBottom Возвращает ординату нижней границы игрового поля.
 * @param rinkRight Возвращает абсциссу правой границы игрового поля.
 * @param afterGoalStateTickCount Возвращает длительность состояния вне игры после гола.
 *                                В течение этого времени новые забитые голы игнорируются,
 *                                а действия не требуют затрат выносливости.
 * @param overtimeTickCount Возвращает длительность дополнительного времени.
 *                          Дополнительное время наступает в случае ничейного счёта на момент окончания основного времени.
 *                          Если за основное время не было забито ни одного гола, вратари обоих игроков убираются с поля.
 * @param defaultActionCooldownTicks Возвращает длительность задержки, применяемой к хоккеисту
 *                                   после совершения им большинства действий ([[model.Move.action]]).
 *                                   В течение этого времени хоккеист не может совершать новые действия.
 * @param swingActionCooldownTicks Возвращает длительность задержки, применяемой к хоккеисту
 *                                 после совершения им действия замах ([[model.ActionType.Swing]]).
 *                                 В течение этого времени хоккеист не может совершать новые действия.
 * @param cancelStrikeActionCooldownTicks Возвращает длительность задержки, применяемой к хоккеисту
 *                                        после отмены им удара ([[model.ActionType.CancelStrike]]).
 *                                        В течение этого времени хоккеист не может совершать новые действия.
 * @param actionCooldownTicksAfterLosingPuck Возвращает длительность задержки, применяемой к хоккеисту
 *                                           в случае потери шайбы вследствие воздействия других хоккеистов.
 *                                           В течение этого времени хоккеист не может совершать действия.
 * @param stickLength Возвращает длину клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
 *                    если и только если расстояние от центра хоккеиста до центра объекта не превышает эту величину.
 * @param stickSector Возвращает сектор клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
 *                    если и только если угол между направлением хоккеиста и вектором из центра хоккеиста в центр объекта
 *                    не превышает половину этой величины.
 * @param passSector Возвращает сектор, ограничивающий направление паса.
 * @param hockeyistAttributeBaseValue Возвращает базовое значение атрибута хоккеиста.
 *                                    Данная величина используется как коэффициент в различных игровых формулах.
 * @param minActionChance Возвращает минимальный шанс на совершение любого вероятностного действия.
 * @param maxActionChance Возвращает максимальный шанс на совершение любого вероятностного действия.
 * @param strikeAngleDeviation Возвращает стандартное отклонение распределения Гаусса для угла удара ([[model.ActionType.Strike]])
 *                             хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
 *                             тем точнее его удар.
 * @param passAngleDeviation Возвращает стандартное отклонение распределения Гаусса для угла паса ([[model.ActionType.Pass]])
 *                           хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
 *                           тем точнее его пас.
 * @param pickUpPuckBaseChance Возвращает базовый шанс подобрать шайбу, не контролируемую другим хоккеистом.
 *                             Максимальный из атрибутов ловкость и подвижность хоккеиста увеличивает шанс на захват.
 *                             Скорость шайбы уменьшает шанс на захват.
 * @param takePuckAwayBaseChance Возвращает базовый шанс отнять шайбу у другого хоккеиста.
 *                               Максимальный из атрибутов сила и ловкость хоккеиста, отнимающего шайбу, увеличивает шанс на захват.
 *                               Максимальный из атрибутов стойкость и подвижность текущего владельца шайбы уменьшает шанс на её потерю.
 * @param maxEffectiveSwingTicks Возвращает длительность замаха, после достижения которой сила удара не увеличивается.
 * @param strikePowerBaseFactor Возвращает коэффициент силы удара без замаха.
 * @param strikePowerGrowthFactor Возвращает увеличение коэффициента силы удара за каждый тик замаха.
 *                                Максимальное количество учитываемых тиков ограничено значением [[model.Game.maxEffectiveSwingTicks]].
 * @param strikePuckBaseChance Возвращает базовый шанс ударить шайбу. Базовый шанс не зависит от того,
 *                             контролирует шайбу другой хоккеист или нет, однако на результирующий шанс удара
 *                             по свободной и контролируемой шайбе влияют разные атрибуты хоккеиста
 *                             (смотрите документацию к [[model.Game.pickUpPuckBaseChance]] и [[model.Game.takePuckAwayBaseChance]]).
 *                             Если хоккеист, совершающий удар, контролирует шайбу, то вероятность удара всегда будет 100%.
 * @param knockdownChanceFactor Возвращает шанс ударом ([[model.ActionType.Strike]]) сбить с ног другого хоккеиста при максимальной
 *                              длительности замаха. Среднее значение атрибутов сила и ловкость хоккеиста, совершающего удар,
 *                              увеличивает шанс сбить с ног. Значение атрибута стойкость атакуемого хоккеиста уменьшает шанс на падение.
 * @param knockdownTicksFactor Возвращает количество тиков, по прошествии которого хоккеист восстановится после падения при базовом
 *                             значении атрибута подвижность. Чем выше подвижность, тем быстрее восстановление.
 * @param maxSpeedToAllowSubstitute Возвращает максимальную допустимую скорость для выполнения замены хоккеиста.
 * @param substitutionAreaHeight Возвращает высоту зоны, в которой может быть выполнена замена хоккеиста. Зона расположена вдоль верхней
 *                               границы игровой площадки. Замена может быть выполнена только на своей половине поля.
 * @param passPowerFactor Возвращает коэффициент силы паса. Умножается на устанавливаемое стратегией в интервале
 *                        `[0.0, 1.0]` значение силы паса ([[model.Move.passPower]]).
 * @param hockeyistMaxStamina Возвращает максимальное значение выносливости хоккеиста. Выносливость тратится на перемещение
 *                            и совершение хоккеистом различных действий. Каждый тик может восстановиться небольшое количество
 *                            выносливости в зависимости от состояния хоккеиста ([[model.Hockeyist.state]]). По мере расходования
 *                            выносливости все атрибуты (соответственно, и эффективность всех действий) хоккеиста равномерно
 *                            уменьшаются и достигают значения [[model.Game.zeroStaminaHockeyistEffectivenessFactor]] (от начальных
 *                            показателей) при падении выносливости до нуля. Хоккеист не восстанавливает выносливость в состояниях
 *                            [[model.HockeyistState.Swinging]] и [[model.HockeyistState.KnockedDown]].
 * @param activeHockeyistStaminaGrowthPerTick Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
 *                                            [[model.HockeyistState.Active]].
 * @param restingHockeyistStaminaGrowthPerTick Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
 *                                             [[model.HockeyistState.Resting]].
 * @param zeroStaminaHockeyistEffectivenessFactor Возвращает коэффициент эффективности действий хоккеиста при падении его выносливости до нуля.
 * @param speedUpStaminaCostFactor Возвращает количество выносливости, которое необходимо затратить на максимальное по модулю
 *                                 ускорение/замедление хоккеиста ([[model.Move.speedUp]]) за 1 тик. Для меньших значений ускорения затраты
 *                                 выносливости пропорционально падают.
 * @param turnStaminaCostFactor Возвращает количество выносливости, которое необходимо затратить на максимальный по модулю
 *                              угол поворота хоккеиста ([[model.Move.turn]]) за 1 тик. Для меньших значений угла поворота затраты
 *                              выносливости пропорционально падают.
 * @param takePuckStaminaCost Возвращает количество выносливости, которое необходимо затратить на совершение действия
 *                            [[model.ActionType.TakePuck]].
 * @param swingStaminaCost Возвращает количество выносливости, которое необходимо затратить на совершение действия
 *                         [[model.ActionType.Swing]].
 * @param strikeStaminaBaseCost Возвращает базовое количество выносливости, которое необходимо затратить на совершение действия
 *                              [[model.ActionType.Strike]].
 * @param strikeStaminaCostGrowthFactor Возвращает увеличение затрат выносливости на удар ([[model.ActionType.Strike]]) за каждый тик замаха.
 *                                      Максимальное количество учитываемых тиков ограничено значением [[model.Game.maxEffectiveSwingTicks]].
 * @param cancelStrikeStaminaCost Возвращает количество выносливости, которое необходимо затратить на совершение действия
 *                                [[model.ActionType.CancelStrike]].
 * @param passStaminaCost Возвращает количество выносливости, которое необходимо затратить на совершение действия
 *                        [[model.ActionType.Pass]].
 * @param goalieMaxSpeed Возвращает максимальную скорость перемещения вратаря.
 * @param hockeyistMaxSpeed Возвращает максимальную скорость перемещения полевого хоккеиста.
 * @param struckHockeyistInitialSpeedFactor Возвращает модуль скорости, добавляемой хоккеисту, попавшему под удар силы 1.0.
 * @param hockeyistSpeedUpFactor Возвращает модуль ускорения, приобретаемого хоккеистом, при [[model.Move.speedUp]] равном 1.0,
 *                               базовом значении атрибута подвижность и максимальном запасе выносливости.
 *                               Направление ускорения совпадает с направлением хоккеиста.
 *                               В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
 *                               игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
 *                               Чем больше скорость, тем на большую величину она уменьшается.
 * @param hockeyistSpeedDownFactor Возвращает модуль ускорения, приобретаемого хоккеистом, при [[model.Move.speedUp]] равном -1.0,
 *                                 базовом значении атрибута подвижность и максимальном запасе выносливости.
 *                                 Направление ускорения противоположно направлению хоккеиста.
 *                                 В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
 *                                 игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
 *                                 Чем больше скорость, тем на большую величину она уменьшается.
 * @param hockeyistTurnAngleFactor Возвращает максимальный модуль угла поворота хоккеиста за тик при базовом значении атрибута подвижность
 *                                 и максимальном запасе выносливости.
 * @param versatileHockeyistStrength Возвращает значение атрибута сила для хоккеиста-универсала.
 * @param versatileHockeyistEndurance Возвращает значение атрибута стойкость для хоккеиста-универсала.
 * @param versatileHockeyistDexterity Возвращает значение атрибута ловкость для хоккеиста-универсала.
 * @param versatileHockeyistAgility Возвращает значение атрибута подвижность для хоккеиста-универсала.
 * @param forwardHockeyistStrength Возвращает значение атрибута сила для нападающего.
 * @param forwardHockeyistEndurance Возвращает значение атрибута стойкость для нападающего.
 * @param forwardHockeyistDexterity Возвращает значение атрибута ловкость для нападающего.
 * @param forwardHockeyistAgility Возвращает значение атрибута подвижность для нападающего.
 * @param defencemanHockeyistStrength Возвращает значение атрибута сила для защитника.
 * @param defencemanHockeyistEndurance Возвращает значение атрибута стойкость для защитника.
 * @param defencemanHockeyistDexterity Возвращает значение атрибута ловкость для защитника.
 * @param defencemanHockeyistAgility Возвращает значение атрибута подвижность для защитника.
 * @param minRandomHockeyistParameter Возвращает минимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
 * @param maxRandomHockeyistParameter Возвращает максимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
 * @param struckPuckInitialSpeedFactor Возвращает модуль скорости, устанавливаемой шайбе, попавшей под удар силы 1.0.
 * @param puckBindingRange Возвращает расстояние от центра хоккеиста, контролирующего шайбу, до центра шайбы.
 */
class Game(val randomSeed: Long,
           val tickCount: Int,
           val worldWidth: Double,
           val worldHeight: Double,
           val goalNetTop: Double,
           val goalNetWidth: Double,
           val goalNetHeight: Double,
           val rinkTop: Double,
           val rinkLeft: Double,
           val rinkBottom: Double,
           val rinkRight: Double,
           val afterGoalStateTickCount: Int,
           val overtimeTickCount: Int,
           val defaultActionCooldownTicks: Int,
           val swingActionCooldownTicks: Int,
           val cancelStrikeActionCooldownTicks: Int,
           val actionCooldownTicksAfterLosingPuck: Int,
           val stickLength: Double,
           val stickSector: Double,
           val passSector: Double,
           val hockeyistAttributeBaseValue: Int,
           val minActionChance: Double,
           val maxActionChance: Double,
           val strikeAngleDeviation: Double,
           val passAngleDeviation: Double,
           val pickUpPuckBaseChance: Double,
           val takePuckAwayBaseChance: Double,
           val maxEffectiveSwingTicks: Int,
           val strikePowerBaseFactor: Double,
           val strikePowerGrowthFactor: Double,
           val strikePuckBaseChance: Double,
           val knockdownChanceFactor: Double,
           val knockdownTicksFactor: Double,
           val maxSpeedToAllowSubstitute: Double,
           val substitutionAreaHeight: Double,
           val passPowerFactor: Double,
           val hockeyistMaxStamina: Double,
           val activeHockeyistStaminaGrowthPerTick: Double,
           val restingHockeyistStaminaGrowthPerTick: Double,
           val zeroStaminaHockeyistEffectivenessFactor: Double,
           val speedUpStaminaCostFactor: Double,
           val turnStaminaCostFactor: Double,
           val takePuckStaminaCost: Double,
           val swingStaminaCost: Double,
           val strikeStaminaBaseCost: Double,
           val strikeStaminaCostGrowthFactor: Double,
           val cancelStrikeStaminaCost: Double,
           val passStaminaCost: Double,
           val goalieMaxSpeed: Double,
           val hockeyistMaxSpeed: Double,
           val struckHockeyistInitialSpeedFactor: Double,
           val hockeyistSpeedUpFactor: Double,
           val hockeyistSpeedDownFactor: Double,
           val hockeyistTurnAngleFactor: Double,
           val versatileHockeyistStrength: Int,
           val versatileHockeyistEndurance: Int,
           val versatileHockeyistDexterity: Int,
           val versatileHockeyistAgility: Int,
           val forwardHockeyistStrength: Int,
           val forwardHockeyistEndurance: Int,
           val forwardHockeyistDexterity: Int,
           val forwardHockeyistAgility: Int,
           val defencemanHockeyistStrength: Int,
           val defencemanHockeyistEndurance: Int,
           val defencemanHockeyistDexterity: Int,
           val defencemanHockeyistAgility: Int,
           val minRandomHockeyistParameter: Int,
           val maxRandomHockeyistParameter: Int,
           val struckPuckInitialSpeedFactor: Double,
           val puckBindingRange: Double)
