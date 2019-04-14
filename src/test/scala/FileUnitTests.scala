import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer

import game._

class FileUnitTests {

  @Test def savedFileSavingAndReading {
    val testGame1 = new Game()
    
    testGame1.startGame()
    val testTower = testGame1.towerHandler.getTower(1, new Coords(100, 50)).get
    testGame1.placeTower(testTower)

    testGame1.levelHandler.nextLevel()

    testGame1.saveGame()

    val testGame2 = new Game()
    val loadedSuccesfully = testGame2.loadGame()
    
    assertEquals("Saved game loaded succesfully", true, loadedSuccesfully)

    testGame2.towers(0)

    assertEquals("Saved game and loaded game have same level", testGame1.levelHandler.currentLevel, testGame2.levelHandler.currentLevel)
    assertEquals("Saved game and loaded game have same towers", testGame1.towers.length, testGame2.towers.length)
    assertEquals("Saved game and loaded game have same towers", testGame1.towers(0).towerType, testGame2.towers(0).towerType)
    assertEquals("Saved game and loaded game have same towers", testGame1.towers(0).towerType, testGame2.towers(0).towerType)
    assertEquals("Saved game and loaded game have same player coins", testGame1.player.coins, testGame2.player.coins)
    assertEquals("Saved game and loaded game have same player health", testGame1.player.health, testGame2.player.health)
  }
}
