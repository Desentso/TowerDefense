import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer

import game._

class UnitTests {

  @Test def enemyDiesWhenAtZeroHealth {
    val testGame = new Game()
    val towerDamage = 100
    val testTower = new Tower(towerDamage, 100, 1.0, 100, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1)

    assertEquals("Game should have enemies alive", 1, testGame.enemies.length)

    testGame.shootTowers(0)
    testGame.filterDeadEnemies()

    assertEquals("All enemies should be dead", 0, testGame.enemies.length)
  }

  @Test def enemyMovesTowardsGoal {
    val testGame = new Game()

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.enemies = Buffer(testEnemy1)

    val path = testGame.gameArea.path

    //testGame.initEnemies()
    //testGame.moveEnemies()

  }

  @Test def towerDoesDamage {
    val testGame = new Game()
    val towerDamage = 10
    val testTower = new Tower(towerDamage, 100, 1.0, 100, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1)

    testGame.shootTowers(0)

    assertEquals("Tower should do damage to enemy in range", enemyHealth - towerDamage, testEnemy1.health)
  }

  @Test def towerShootsNearestEnemy {
    val testGame = new Game()
    val towerDamage = 10
    val testTower = new Tower(towerDamage, 100, 1, 100, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)
    val testEnemy2 = new Enemy(enemySpeed, enemyHealth)

    testEnemy1.move(10, 0)
    testEnemy2.move(5, 0)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1, testEnemy2)

    testGame.shootTowers(0)

    assertEquals("Tower should shoot the nearest enemy", enemyHealth - towerDamage, testEnemy1.health)
    assertEquals("Tower should not shoot other enemies", enemyHealth, testEnemy2.health)
  }

  @Test def towerDoesntShootEnemiesOutOfRange {
    val testGame = new Game()
    val towerDamage = 10
    val testTower = new Tower(towerDamage, 100, 1, 100, "testTower", new Coords(0, 0), testGame)

    val enemyHealth = 100
    val enemySpeed = 1.0
    val testEnemy1 = new Enemy(enemySpeed, enemyHealth)
    val testEnemy2 = new Enemy(enemySpeed, enemyHealth)

    testEnemy1.move(150, 0)
    testEnemy2.move(101, 0)

    testGame.towers = Buffer(testTower)
    testGame.enemies = Buffer(testEnemy1, testEnemy2)

    testGame.shootTowers(0)

    assertEquals("Tower should not shoot other enemies out of range", enemyHealth, testEnemy1.health)
    assertEquals("Tower should not shoot other enemies out of range", enemyHealth, testEnemy2.health)
  }



}
