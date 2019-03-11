package game

import scala.swing._
import scala.collection.mutable.Buffer

object GUIState extends Enumeration {
  type GUIState = Value
  val Start, InGame, Paused = Value
}

class Game() {
  private var state = GUIState.Start
  val gameArea = new GameArea()
  var enemies = Buffer[Enemy](new Enemy(), new Enemy(), new Enemy())
  var towers = Buffer[Tower]()
  var currentLevel = 1
  var selectingTower = 0
  
  //gameArea.findPath()
  
  def currentState = this.state
  
  def startGame() = {
    this.state = GUIState.InGame
    this.initEnemies()
    
    println("Start Game")
  }
  
  def onTick() = {
    this.filterDeadEnemies()
    this.moveEnemies()
    this.shootTowers()
    //println(this.enemies)
  }
  
  def onMouseClick(src: Component, point: Point) = {
    println(point)
    if (this.selectingTower != 0) {
      this.placeTower(point)
      this.selectingTower = 0
    }
  }
  
  def selectedTower(towerID: Int) = this.selectingTower = towerID
  
  def placeTower(point: Point) = {
    this.selectingTower match {
      case 1 => towers += new Tower1(new Coords(point.x, point.y), this)
      case 2 => towers += new Tower2(new Coords(point.x, point.y), this)
    }
  }
  
  def enemyDidReachEndOfPath(indexToBeRemoved: Int) = {
    this.enemies.remove(indexToBeRemoved)
    //this.player
  }
  
  private def initEnemies() = {
    val totalDistance = gameArea.path.map(p => p.x * Constants.tileWidth).reduce(_ + _)
    println(totalDistance)
    enemies.zipWithIndex.foreach(p => {
      val enemy = p._1
      enemy.move(-p._2 * 10, Constants.tileHeight + (Constants.tileHeight / 2))
      this.enemyFirstDirection(enemy)
      enemy.distanceToGoal = totalDistance + (p._2 * 10)
    })
  }
  
  private def enemyFirstDirection(enemy: Enemy) = {
    val currentTile = gameArea.path(enemy.pathPosition)
    val nextTile = gameArea.path(enemy.pathPosition + 1)
    
    if (enemy.direction == Direction.none) {
      println((nextTile.x - currentTile.x, nextTile.y - currentTile.y))
      enemy.direction = (nextTile.x - currentTile.x, nextTile.y - currentTile.y) match {
        case (1, 0) => Direction.right
        case (-1, 0) => Direction.left
        case (0, 1) => Direction.down
        case (0, -1) => Direction.up
      }
    }
  }
  
  private def isLastTileForEnemy(enemy: Enemy) = (gameArea.path.length - 1) == enemy.pathPosition
  
  def filterDeadEnemies() = this.enemies = this.enemies.filter(enemy => enemy.health > 0)
  
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
          println("change dir x")
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
          println("change dir y")
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
  
  def shootTowers() = {
    this.towers.foreach(tower => tower.shoot())
  }
  
}

object Direction {
  val none = 0
  val left = 1
  val right = 2
  val up = 3
  val down = 4
}


object Levels {
  val enemies = Map(
    1 -> Vector((0 to 100).toArray.map(i => new Enemy())),
    5 -> Vector((0 to 100).toArray.map(i => new Enemy())),
    10 -> Vector((0 to 100).toArray.map(i => new Enemy()))
  )
    
  def getEnemies(level: Int) = {
    
  }
}

