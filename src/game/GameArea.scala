package game

import scala.swing.Graphics2D

class GameArea() {
  // Legend:
  //  1 is the path enemies use
  //  0 places that player can place turrets on
  // For now just one hardcoded map, though should be easily expandable with e.g. files
  val area = Vector(
    Vector(0, 0, 0, 0, 0, 0, 0),
    Vector(1, 1, 1, 1, 1, 1, 0),
    Vector(0, 0, 0, 0, 0, 1, 0),
    Vector(0, 0, 1, 1, 1, 1, 0),
    Vector(0, 0, 1, 0, 0, 0, 0),
    Vector(0, 0, 1, 0, 0, 0, 0),
  )
  
  // Movement
  // Prefers from left to right, top to down
  
  def getNextStepForEnemy(coords: Coords) = {
    // Get adjacent tiles
    val adjacentTiles = this.area.slice(coords.y - 1, coords.y + 1).map(row => row.slice(coords.x - 1, coords.x + 1))
    //this.area.lift(coords.y + 1).getOrElse(Vector[Int]()).lift(coords.x + 1)
    this.area
  }

}
