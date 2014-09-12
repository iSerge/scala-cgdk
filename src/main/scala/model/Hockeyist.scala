package model

/**
 * Класс, определяющий хоккеиста. Содержит также все свойства юнита.
 * @param id Возвращает уникальный идентификатор объекта.
 * @param playerId Возвращает идентификатор игрока, в команду которого входит хоккеист.
 * @param teammateIndex Возвращает 0-индексированный номер хоккеиста в команде.
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
 * @param teammate Возвращает `true`, если и только если данный хоккеист входит в команду вашего игрока.
 * @param hokeyistType Возвращает тип хоккеиста.
 * @param strength Возвращает значение атрибута сила.
 * @param endurance Возвращает значение атрибута стойкость.
 * @param dexterity Возвращает значение атрибута ловкость.
 * @param agility Возвращает значение атрибута подвижность.
 * @param stamina Возвращает текущее значение выносливости.
 * @param state Возвращает состояние хоккеиста.
 * @param originalPositionIndex Возвращает индекс исходной позиции хоккеиста или `1` для вратаря или хоккеиста,
 *                              отдыхающего за пределами игрового поля. На эту позицию хоккеист будет помещён
 *                              при разыгрывании шайбы.При выполнении действия замена [[model.ActionType.Substitute]]
 *                              индексы исходных позиций хоккеистов, участвующих в замене, меняются местами.
 * @param remainingKnockdownTicks Возвращает количество тиков, по прошествии которого хоккеист восстановится после падения,
 *                                или `0`, если хоккеист не сбит с ног.
 * @param remainingCooldownTicks Возвращает количество тиков, по прошествии которого хоккеист сможет совершить какое-либо
 *                               действие ([[model.Move#setAction]]), или `0`, если хоккеист может совершить
 *                               действие в данный тик.
 * @param swingTicks Для хоккеиста, находящегося в состоянии замаха ([[model.HockeyistState.Swinging]]),
 *                   возвращает количество тиков, прошедших от начала замаха. В противном случае возвращает `0`.
 * @param lastAction Возвращает последнее действие ([[model.Move#setAction]]), совершённое хоккеистом,
 *                   или [[model.ActionType.None]] в случае, если хоккеист ещё не совершил ни одного действия.
 * @param lastActionTick Возвращает номер тика, в который хоккеист совершил своё последние действие ([[model.Move#setAction]]),
 *                       или `None` в случае, если хоккеист ещё не совершил ни одного действия.
 */
class Hockeyist(id: Long,
                val playerId: Long,
                val teammateIndex: Int,
                mass: Double,
                radius: Double,
                x: Double,
                y: Double,
                speedX: Double,
                speedY: Double,
                angle: Double,
                angularSpeed: Double,
                val teammate: Boolean,
                val hokeyistType: Option[HockeyistType],
                val strength: Int,
                val endurance: Int,
                val dexterity: Int,
                val agility: Int,
                val stamina: Double,
                val state: Option[HockeyistState],
                val originalPositionIndex: Int,
                val remainingKnockdownTicks: Int,
                val remainingCooldownTicks: Int,
                val swingTicks: Int,
                val lastAction: Option[ActionType],
                val lastActionTick: Option[Integer])
  extends Unit(id, mass, radius, x, y, speedX, speedY, angle, angularSpeed)
