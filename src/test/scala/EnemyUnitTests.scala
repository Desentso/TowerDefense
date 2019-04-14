import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer

import game._

class EnemyUnitTests {

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

    testGame.initEnemies()

    val enemyInitPositionX = testEnemy1.position.x
    val enemyInitPositionY = testEnemy1.position.y

    testGame.moveEnemies()

    val firstTile = path(0)
    val secondTile = path(1)

    if (firstTile.x - secondTile.x != 0) {
      assertEquals("Enemy should move towards goal", (enemyInitPositionX + 1, enemyInitPositionY), (testEnemy1.position.x, testEnemy1.position.y))
    } else if (firstTile.y - secondTile.y != 0) {
      assertEquals("Enemy should move towards goal", (enemyInitPositionX, enemyInitPositionY + 1), (testEnemy1.position.x, testEnemy1.position.y))
    }

  }
}
