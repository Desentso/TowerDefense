import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer

import game._

class PlayerUnitTests {

  @Test def playerGetsCoinsAfterKillingEnemy {
    val testGame = new Game()
    val playerInitCoins = testGame.player.coins
    val towerDamage = 100
    val testTower = new Tower(towerDamage, 100, 1, 100, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1)

    testGame.shootTowers(0)
    testGame.filterDeadEnemies()

    assertEquals("Player should get coins after killing enemy", playerInitCoins + testGame.enemyKilledReward, testGame.player.coins)
  }

  @Test def playerGetsCoinsAfterCompletingLevel {
    val testGame = new Game()
    val playerInitCoins = testGame.player.coins
    val towerDamage = 100
    val testTower = new Tower(towerDamage, 1000, 1, 0, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1)

    testGame.initEnemies()
    testGame.onTick()
    testGame.onTick()

    assertEquals("Player should get coins after completing level", playerInitCoins + testGame.levelCompleteReward + testGame.enemyKilledReward, testGame.player.coins)
  }

  @Test def buyingTowerReducesCoins {
    val testGame = new Game()
    val playerInitCoins = testGame.player.coins

    val towerCost = 100
    val testTower = new Tower(1, 1000, 1, towerCost, "testTower", new Coords(0, 0), testGame)

    testGame.placeTower(testTower)

    assertEquals("Player should get coins after completing level", playerInitCoins - towerCost, testGame.player.coins)
  }

  @Test def cantBuyTowerWithInsufficientCoins {
    val testGame = new Game()
    testGame.player.coins = 0

    val towerCost = 100
    val testTower = new Tower(1, 1000, 1, towerCost, "testTower", new Coords(0, 0), testGame)

    assertEquals("Player should not be able to buy tower with insufficient coins", false, testGame.player.hasCoinsToBuyTower(testTower))
  }

  @Test def playerLosesHealthIfEnemyReachesGoal {
    val testGame = new Game()
    val playerInitHealth = testGame.player.health

    val enemyHealth = 100
    val enemySpeed = 100.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.enemies = Buffer(testEnemy1)

    testGame.initEnemies()
    (0 to 100).foreach(_ => testGame.moveEnemies())

    assertEquals("Player should lose health if enemy reaches goal", playerInitHealth - 1, testGame.player.health)
  }

  @Test def playerLosesWhenHealthIsZero {
    val testGame = new Game()
    testGame.player.health = 0

    assertEquals("Game should be over when player has zero health", true, testGame.isGameOver)
  }
}
