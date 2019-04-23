package game

import scala.util.control.NonFatal
import scala.swing._
import scala.collection.mutable.Buffer
import play.api.libs.json._

class Game() {
  private val fileHandler = new FileHandler(this)

  val (
    startHealth, 
    startCoins, 
    levelCompleteReward, 
    levelCompleteIncreaseRate, 
    enemyKilledReward, 
    levelRequiredForWin, 
    towersAsJson, 
    specialAsJson,
    mapAsVector
  ) = this.loadConfiguration()

  private val random = new scala.util.Random

  val levelHandler: Level = new Level(levelCompleteReward, levelCompleteIncreaseRate)
  val player: Player = new Player(startHealth, startCoins)
  val towerHandler: TowerHandler = new TowerHandler(this, towersAsJson)
  val specialHandler: SpecialHandler = new SpecialHandler(specialAsJson)

  val gameArea = new GameArea(mapAsVector)
  var enemies = Buffer[Enemy]()
  var towers = Buffer[Tower]()
  var specials = Buffer[Special]()

  var selectingTower = 0
  var selectingSpecial = false
  private var tick = 1
  var notification = ""
  private var notificationTicks = 0
  private var hasWon = false


  def loadConfiguration() = {
    // Load and setup the game from a configuration file
    val confJson = this.fileHandler.loadConfiguration()

    val startHealth: Int = (confJson \ "startHealth").asOpt[Int].getOrElse(Constants.defaultStartHealth)
    val startCoins: Int = (confJson \ "startCoins").asOpt[Int].getOrElse(Constants.defaultStartCoins)
    val levelCompleteReward: Int = (confJson \ "levelCompleteReward").asOpt[Int].getOrElse(Constants.defaultLevelCompleteReward)
    val levelCompleteIncreaseRate: Double = (confJson \ "levelCompleteIncreaseRate").asOpt[Double].getOrElse(Constants.defaultLevelCompleteIncreaseRate)
    val enemyKilledReward: Int = (confJson \ "enemyKilledReward").asOpt[Int].getOrElse(Constants.defaultEnemyKilledReward)
    val levelRequiredForWin: Int = (confJson \ "levelRequiredForWin").asOpt[Int].getOrElse(Constants.defaultLevelRequiredForWin)
    val specialAsJson: JsValue = (confJson \ "special").asOpt[JsValue].getOrElse(Constants.defaultSpecial)
    
    var towersAsJson: List[JsValue] = (confJson \ "towers").asOpt[List[JsValue]].getOrElse(Constants.defaultTowerConf)
    towersAsJson = if (towersAsJson.length < 5) Constants.defaultTowerConf else towersAsJson
    
    var mapAsVector: Vector[Vector[Int]] = (confJson \ "map").asOpt[Vector[Vector[Int]]].getOrElse(Constants.defaultMap)
    // Require two rows and at least two cells marked as path
    mapAsVector = if (mapAsVector.length < 2 || mapAsVector.foldLeft(0)((total, row) => total + row.count(cell => cell == 1)) < 2) Constants.defaultMap else mapAsVector
    Constants.tileHeight = Constants.gameAreaHeight / mapAsVector.length
    Constants.tileWidth = Constants.gameAreaWidth / mapAsVector(0).length

    (
      startHealth, 
      startCoins, 
      levelCompleteReward, 
      levelCompleteIncreaseRate, 
      enemyKilledReward, 
      levelRequiredForWin, 
      towersAsJson, 
      specialAsJson,
      mapAsVector
    )
  }
  
  def loadGame(): Boolean = {
    // Load and setup the game from save file
    try {
      val savedJson = fileHandler.loadGame()

      player.reinitSavedPlayer(savedJson)
      levelHandler.setLevel((savedJson \ "level").as[Int])
      val savedTowers = (savedJson \ "towers").as[List[JsValue]]

      this.towers = savedTowers.map(tower => {
        val towerType = (tower \ "type").as[String]

        val position = (tower \ "position").as[Map[String, Int]]

        (towerType match {
          case "1" => this.towerHandler.getTower(1, new Coords(position("x"), position("y")))
          case "2" => this.towerHandler.getTower(2, new Coords(position("x"), position("y")))
          case "3" => this.towerHandler.getTower(3, new Coords(position("x"), position("y")))
          case "4" => this.towerHandler.getTower(4, new Coords(position("x"), position("y")))
          case "5" => this.towerHandler.getTower(5, new Coords(position("x"), position("y")))
        }).get
      }).toBuffer

      println(this.towers)
      true
    } catch {
      case NonFatal(err) => {
        println(err)
        false
      }
    }
  }
  
  def startGame() = {
    this.enemies = levelHandler.getEnemies()
    this.initEnemies()
    
    println("Start Game")
  }
  
  def saveGame() {
    fileHandler.saveGame()
  }
  
  def onTick() = {
    // This function is called every "tick" by the GUI, basically every 16ms or 8ms
    // Handles the core game logic
    this.filterDeadEnemies()
    this.moveEnemies()
    this.shootTowers(tick)
    this.explodeSpecials()

    if (this.hasLevelEnded) {
      this.onLevelEnd()
    }

    tick += 1
    if (tick >= Constants.rateOfFireGranularity) {
      tick = 1
    }

    notificationTicks += 1
    if (notificationTicks >= 200) {
      this.clearNotification()
    }
  }

  def onLevelEnd() = {
    // All the logic that should have happen at the end of each level
    this.player.addCoins(this.levelHandler.getReward())
    this.levelHandler.nextLevel()
    this.enemies = this.levelHandler.getEnemies()
    this.initEnemies()
    this.saveGame()
  }

  def isGameOver = !this.player.isAlive
  
  def isGameWon = {
    if (this.currentLevel == levelRequiredForWin && !this.hasWon) {
      this.hasWon = true
      true
    } else {
      false
    }
  }

  def hasLevelEnded = this.enemies.length == 0

  def currentLevel = this.levelHandler.currentLevel

  def clearNotification() = {
    this.notification = ""
    this.notificationTicks = 0
  }

  def newNotification(msg: String) = {
    this.notification = msg
    this.notificationTicks = 0
  }

  def onMouseClick(src: Component, point: Point) = {

    if (this.selectingTower != 0) {
      val tower = this.getSelectedTower(point)
      this.tryToPlaceTower(point, tower.get)
    } else if (this.selectingSpecial) {
      val special = specialHandler.getSpecial(point)
      this.tryToPlaceSpecial(point, special)
    }
  }

  def selectedTower(towerID: Int) = {
    this.selectingTower = towerID
    this.selectingSpecial = false
  }
  def selectedSpecial() = {
    this.selectingSpecial = true
    this.selectingTower = 0
  }

  def tryToPlaceTower(point: Point, tower: Tower) = {
    // Check if possible to place tower here and with the current state
    if (!player.hasCoinsToBuyTower(tower)) {
      this.newNotification("Not enough coins to buy this tower")
    } else if (!gameArea.isPointInsidePath(point) || gameArea.isPointOutSideArea(point)) {
      this.newNotification("Can't place tower here")
    } else {
      this.placeTower(tower)
      this.selectingTower = 0
    }
  }

  def tryToPlaceSpecial(point: Point, special: Special) = {
    // Check if possible to place special here and with the current state
    if (!player.hasCoinsToBuySpecial(special)) {
      this.newNotification("Not enough coins to buy special")
    } else if (gameArea.isPointInsidePath(point)) {
      this.newNotification("Can't place special here")
    } else {
      this.placeSpecial(special)
      this.selectingSpecial = false
    }
  }

  def placeTower(tower: Tower) = {
    towers += tower
    player.towerBought(tower)
  }

  def placeSpecial(special: Special) = {
    specials += special
    player.specialBought(special)
  }
  
  def getSelectedTower(point: Point): Option[Tower] = {
    this.towerHandler.getTower(this.selectingTower, new Coords(point.x, point.y))
  }
  
  def enemyDidReachEndOfPath(indexToBeRemoved: Int) = {
    this.enemies.remove(indexToBeRemoved)
    this.player.enemyDidReachEndOfPath()
  }
  
  def initEnemies() = {
    // Initialize the enemy positions to the start of the path
    val totalDistance = gameArea.path.map(p => p.x * Constants.tileWidth).reduce(_ + _)
    val firstTile = gameArea.path(0)

    enemies.zipWithIndex.foreach(p => {
      val enemy = p._1
      val offset = Constants.spaceBetweenEnemies + random.nextInt(Constants.spaceBetweenEnemies)
      
      val firstDirection = this.getEnemyFirstDirection(enemy)
      if (firstDirection == Direction.right) {
        enemy.move((-p._2 * Constants.spaceBetweenEnemiesMultiplier) - offset, ((firstTile.y - 1) * Constants.tileHeight) + (Constants.tileHeight / 2), true)
      } else if (firstDirection == Direction.down) {
        enemy.move(((firstTile.x - 1) * Constants.tileWidth) + (Constants.tileWidth / 2), (-p._2 * Constants.spaceBetweenEnemiesMultiplier) - offset, true)
      } 

      enemy.direction = firstDirection
      enemy.distanceToGoal = totalDistance + ((p._2 * Constants.spaceBetweenEnemiesMultiplier) + offset)
    })
  }
  
  private def getEnemyFirstDirection(enemy: Enemy) = {
    // Get the first direction for enemy, based on the first and second tile of the path
    val firstTile = gameArea.path(0)
    val secondTile = gameArea.path(1)
    
    (secondTile.x - firstTile.x, secondTile.y - firstTile.y) match {
      case (1, 0) => Direction.right
      case (-1, 0) => Direction.left
      case (0, 1) => Direction.down
      case (0, -1) => Direction.up
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
    val enemiesThatReachedEnd = Buffer[Int]()

    // Move enemies based on the enemies' current direction, current tile and next tile on path
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
        // Should never remove from array while looping so we store the to-be-deleted indexes.
        enemiesThatReachedEnd += pair._2
      }
      
      val move = enemy.direction match {
        case Direction.left => (-1, 0)
        case Direction.right => (1, 0)
        case Direction.up => (0, -1)
        case Direction.down => (0, 1)
      }
      
      enemy.move(move._1, move._2)
    })
    
    enemiesThatReachedEnd.zipWithIndex.foreach(p => this.enemyDidReachEndOfPath(p._1 - p._2))
  }
  
  def shootTowers(tick: Int) = {
    this.towers.foreach(tower => tower.shoot(tick))
  }

  def explodeSpecials() = {
    this.specials = this.specials.filter(special => !special.explode(this.enemies.toVector))
  }
  
}

object Direction {
  val none = 0
  val left = 1
  val right = 2
  val up = 3
  val down = 4
}
