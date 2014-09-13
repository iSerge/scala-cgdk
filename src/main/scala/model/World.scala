package model

/**
 * Этот класс описывает игровой мир. Содержит также описания всех игроков и игровых объектов (<<юнитов>>).
 * @param tick Возвращает номер текущего тика.
 * @param tickCount Возвращает базовую длительность игры в тиках.
 *                  Реальная длительность может отличаться от этого значения в большую сторону.
 * @param width Возвращает ширину мира.
 * @param height Возвращает высоту мира.
 * @param players Возвращает список игроков (в случайном порядке).
 *                После каждого тика объекты, задающие игроков, пересоздаются.
 * @param hockeyists Возвращает список хоккеистов (в случайном порядке), включая вратарей и хоккеиста стратегии,
 *                   вызвавшей этот метод. После каждого тика объекты, задающие хоккеистов, пересоздаются.
 * @param puck Возвращает шайбу.
 */
class World(val tick: Int,
            val tickCount: Int,
            val width: Double,
            val height: Double,
            val players: Vector[Option[Player]],
            val hockeyists: Vector[Hockeyist],
            val puck: Puck) {

  /**
   * @return Возвращает вашего игрока.
   */
  lazy val myPlayer: Option[Player] = players.collectFirst { case Some(p) if p.me => p }

  /**
   * @return Возвращает игрока, соревнующегося с вами.
   */
  lazy val opponentPlayer: Option[Player] = players.collectFirst { case Some(p) if !p.me => p }
}

object World extends CanBeEmpty[World]
