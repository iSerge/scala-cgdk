package model

/**
 * Содержит данные о текущем состоянии игрока.
 * @param id Возвращает уникальный идентификатор игрока.
 * @param me Возвращает `true` в том и только в том случае, если этот игрок ваш.
 * @param name Возвращает имя игрока.
 * @param goalCount Возвращает количество шайб, заброшенных хоккеистами данного игрока в сетку противника.
 *                  Шайбы, заброшенные во время состояния вне игры, не влияют на этот счётчик.
 * @param strategyCrashed Возвращает специальный флаг --- показатель того, что стратегия игрока ''упала''.
 *                        Более подробную информацию можно найти в документации к игре.
 * @param netTop Возвращает ординату верхней штанги ворот.
 * @param netLeft Возвращает абсциссу левой границы ворот.
 * @param netBottom Возвращает ординату нижней штанги ворот.
 * @param netRight Возвращает абсциссу правой границы ворот.
 * @param netFront Возвращает абсциссу ближайшей к вратарю вертикальной границы ворот.
 *                 Соответствует одному из значений [[model.Player#getNetLeft]] или [[model.Player#getNetRight]].
 * @param netBack Возвращает абсциссу дальней от вратаря вертикальной границы ворот.
 *                Соответствует одному из значений [[model.Player#getNetLeft]] или [[model.Player#getNetRight]].
 * @param justScoredGoal Возвращает `true` в том и только в том случае, если игрок только что забил гол.
 *                       <p/>
 *                       Вместе с установленным флагом {@link justMissedGoal} другого игрока означает,
 *                       что сейчас состояние вне игры и новые голы не будут засчитаны.
 *                       Длительность состояния вне игры составляет [[model.Game#getAfterGoalStateTickCount]] тиков.
 * @param justMissedGoal Возвращает `true` в том и только в том случае, если игрок только что пропустил гол.
 *                       <p/>
 *                       Вместе с установленным флагом {@link justScoredGoal} другого игрока означает,
 *                       что сейчас состояние вне игры и новые голы не будут засчитаны.
 *                       Длительность состояния вне игры составляет [[model.Game#getAfterGoalStateTickCount]] тиков.
 */
class Player(val id: Long,
             val me: Boolean,
             val name: String,
             val goalCount: Int,
             val strategyCrashed: Boolean,
             val netTop: Double,
             val netLeft: Double,
             val netBottom: Double,
             val netRight: Double,
             val netFront: Double,
             val netBack: Double,
             val justScoredGoal: Boolean,
             val justMissedGoal: Boolean)
