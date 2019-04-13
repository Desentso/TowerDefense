package game

import scala.swing._
import scala.collection.mutable.Buffer
import play.api.libs.json._

object GUIState extends Enumeration {
  type GUIState = Value
  val Start, InGame, Paused = Value
}

class Game() {
  val fileHandler = new FileHandler(this)

  val (startHealth, startCoins, levelCompleteReward, levelCompleteIncreaseRate, enemyKilledReward, towersAsJson) = this.loadConfiguration()

  private var state = GUIState.Start
  val random = new scala.util.Random

  val levelHandler: Level = new Level(levelCompleteReward, levelCompleteIncreaseRate)
  val player: Player = new Player(startHealth, startCoins)
  val towerHandler: Towers = new Towers(this, towersAsJson)

  val gameArea = new GameArea()
  var enemies = Buffer[Enemy]()
  var towers = Buffer[Tower]()
  
  var selectingTower = 0
  var tick = 1

  def loadConfiguration() = {
    val confJson = this.fileHandler.loadConfiguration()

    val startHealth = (confJson \ "startHealth").as[Int]
    val startCoins = (confJson \ "startCoins").as[Int]
    val levelCompleteReward = (confJson \ "levelCompleteReward").as[Int]
    val levelCompleteIncreaseRate = (confJson \ "levelCompleteIncreaseRate").as[Double]
    val enemyKilledReward = (confJson \ "enemyKilledReward").as[Int]
    val towersAsJson = (confJson \ "towers").as[List[JsValue]]

    (startHealth, startCoins, levelCompleteReward, levelCompleteIncreaseRate, enemyKilledReward, towersAsJson)
  }

  def currentState = this.state
  
  def isGameOver = !this.player.isAlive
  
  def loadGame() = {
    val savedJson = fileHandler.loadGame()

    player.reinitSavedPlayer(savedJson)
    levelHandler.setLevel((savedJson \ "level").as[Int])
    val savedTowers = (savedJson \ "towers").as[List[JsValue]]

    this.towers = savedTowers.map(tower => {
      val towerType = (tower \ "type").as[String]

      val position = (tower \ "position").as[Map[String, Int]]

      (towerType match {
        case "Tower 1" => this.towerHandler.getTower(1)
        case "Tower 2" => this.towerHandler.getTower(2)
      }).get
    }).toBuffer

    println(this.towers)
  }
  
  def startGame() = {
    this.state = GUIState.InGame
    this.enemies = levelHandler.getEnemies()
    this.initEnemies()
    
    println("Start Game")
  }
  
  def saveGame() {
    fileHandler.saveGame()
  }
  
  def onTick() = {
    
    this.filterDeadEnemies()
    this.moveEnemies()
    this.shootTowers(tick)
    
    if (this.hasLevelEnded) {
      this.levelHandler.getReward()
      this.levelHandler.nextLevel()
      this.enemies = this.levelHandler.getEnemies()
      this.initEnemies()
      this.saveGame()
    }
    tick += 1
    if (tick >= 10) {
      tick = 1
    }
  }
  
  def hasLevelEnded = this.enemies.length == 0
  
  def onMouseClick(src: Component, point: Point) = {
    println(point)
    val tower = this.getSelectedTower(point)
    
    if (this.selectingTower != 0 && gameArea.isPointOutsidePath(point) && player.hasCoinsToBuyTower(tower.get)) {
      this.placeTower(tower.get)
      this.selectingTower = 0
    }
  }
  
  def selectedTower(towerID: Int) = this.selectingTower = towerID
  
  def placeTower(tower: Tower) = {
    towers += tower
    player.towerBought(tower)
  }
  
  def getSelectedTower(point: Point): Option[Tower] = {
    this.towerHandler.getTower(this.selectingTower, new Coords(point.x, point.y))
  }
  
  def enemyDidReachEndOfPath(indexToBeRemoved: Int) = {
    this.enemies.remove(indexToBeRemoved)
    this.player.enemyDidReachEndOfPath()
  }
  
  private def initEnemies() = {
    val totalDistance = gameArea.path.map(p => p.x * Constants.tileWidth).reduce(_ + _)

    enemies.zipWithIndex.foreach(p => {
      val enemy = p._1
      val offset = 4 + random.nextInt(15)
      
      enemy.move(-p._2 * offset, Constants.tileHeight + (Constants.tileHeight / 2), true)
      this.enemyFirstDirection(enemy)
      enemy.distanceToGoal = totalDistance + (p._2 * offset)
    })
  }
  
  private def enemyFirstDirection(enemy: Enemy) = {
    val currentTile = gameArea.path(enemy.pathPosition)
    val nextTile = gameArea.path(enemy.pathPosition + 1)
    
    if (enemy.direction == Direction.none) {
      //println((nextTile.x - currentTile.x, nextTile.y - currentTile.y))
      enemy.direction = (nextTile.x - currentTile.x, nextTile.y - currentTile.y) match {
        case (1, 0) => Direction.right
        case (-1, 0) => Direction.left
        case (0, 1) => Direction.down
        case (0, -1) => Direction.up
      }
    }
  }
  
  private def isLastTileForEnemy(enemy: Enemy) = (gameArea.path.length - 1) == enemy.pathPosition
  
  def filterDeadEnemies() = {
    val enemiesBefore = this.enemies.length
    this.enemies = this.enemies.filter(enemy => enemy.health > 0)
    val enemiesAfter = this.enemies.length
    
    this.player.addCoins((enemiesBefore - enemiesAfter) * this.enemyKilledReward)
  }
  
  def moveEnemies() = {
    
    this.enemies.zipWithIndex.foreach(pair => {
      val enemy = pair._1
      if (!isLastTileForEnemy(enemy)) {
        val currentTile = gameArea.path(enemy.pathPosition)
        val nextTile = gameArea.path(enemy.pathPosition + 1)
        
        val enemyX = enemy.position.x
        val enemyY = enemy.position.y
        
        if (((enemyX > ((currentTile.x * Constants.tileWidth) - (Constants.tileWidth / 2))) && enemy.direction == Direction.right) ||
            ((enemyX < ((currentTile.x * Constants.tileWidth) - (Constants.tileWidth / 2))) && enemy.direction == Direction.left)) {
          if (nextTile.y == currentTile.y) {
            enemy.direction
          } else {
            if (nextTile.y - currentTile.y == 1) {
              enemy.direction = Direction.down
            } else {
              enemy.direction = Direction.up
            }
          }
          enemy.pathPosition += 1
        } else if (((enemyY > ((currentTile.y * Constants.tileHeight) - (Constants.tileHeight / 2))) && enemy.direction == Direction.down) || 
            ((enemyY < ((currentTile.y * Constants.tileHeight) - (Constants.tileHeight / 2))) && enemy.direction == Direction.up)) {
          if (nextTile.x == currentTile.x) {
            new Coords(0, nextTile.y - currentTile.y)
            enemy.direction
          } else {
            if (nextTile.x - currentTile.x == 1) {
              enemy.direction = Direction.right
            } else {
              enemy.direction = Direction.left
            }
          }
          enemy.pathPosition += 1
        }
      } else if (isLastTileForEnemy(enemy) && enemy.hasReachedEnd) {
        this.enemyDidReachEndOfPath(pair._2)
      }
      
      val move = enemy.direction match {
        case Direction.left => (-1, 0)
        case Direction.right => (1, 0)
        case Direction.up => (0, -1)
        case Direction.down => (0, 1)
      }
      
      enemy.move(move._1, move._2)
    })
  }
  
  def shootTowers(tick: Int) = {
    this.towers.foreach(tower => tower.shoot(tick))
  }
  
}

object Direction {
  val none = 0
  val left = 1
  val right = 2
  val up = 3
  val down = 4
}
