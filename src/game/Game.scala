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
  var enemies = Buffer[(Int, Int)]((0, 0))
  
  def currentState = this.state
  
  def startGame() = {
    this.state = GUIState.InGame
    println("Start Game")
  }
  
  def onTick() = {
    this.moveEnemies()
    //println(this.enemies)
  }
  
  def onMouseClick(src: Component, point: Point) = {
    println(point)
  }
  
  def moveEnemies() = {
    this.enemies = this.enemies.map(enemy => (enemy._1 + 1, enemy._2))
  }
}
