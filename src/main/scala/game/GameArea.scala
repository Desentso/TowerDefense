package game

import scala.collection.mutable.Buffer
import scala.swing.{Graphics2D, Point}

class GameArea() {
  // Legend:
  //  1 is the path enemies use
  //  0 places that player can place turrets on
  //  2 turret
  // For now just one hardcoded map, though should be easily expandable with e.g. files
  private val area = Array(
    Array(0, 1, 0, 0, 0, 0, 0),
    Array(0, 1, 1, 1, 1, 1, 0),
    Array(0, 0, 0, 0, 0, 1, 0),
    Array(0, 1, 1, 1, 1, 1, 0),
    Array(0, 1, 0, 0, 0, 0, 0),
    Array(0, 1, 0, 0, 0, 0, 0),
    Array(0, 1, 0, 0, 0, 0, 0),
  )
  
  val path = {
    var endIndex = 1
    var path = Array[Coords]()
    
    this.area.zipWithIndex.foreach(row => {
        val tiles = row._1.zipWithIndex.filter(p => p._1 == 1).map(p => new Coords(p._2+1, row._2+1))
        
        if (tiles.length > 0) {
          path = path ++ (if (tiles(0).x == endIndex) tiles else tiles.reverse)
          endIndex = tiles.last.x
        }
    })
    
    println(path.mkString(","))
    println(path.map(c => (c.x * Constants.tileWidth, c.y * Constants.tileHeight)).mkString(","))
    path
  }
  
  def getArea = this.area
  
  def isPointOutsidePath(point: Point) = {
    val tileUnderPoint = this.path
      .map(tile => new Coords((tile.x-1) * Constants.tileWidth, (tile.y-1) * Constants.tileHeight))
      .find(tile => point.x > tile.x && point.x < tile.x + Constants.tileWidth && point.y > tile.y && point.y < tile.y + Constants.tileHeight)
      
    tileUnderPoint == None
  }
  
  def addTower(x: Int, y: Int) = {
    this.area(y)(x) = 2
  }

}
