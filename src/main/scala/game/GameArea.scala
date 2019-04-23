package game

import scala.collection.mutable.Buffer
import scala.swing.{Graphics2D, Point}

class GameArea(private val area: Vector[Vector[Int]]) {

  val path = {
    var endIndex = 1
    var path = Array[Coords]()
    
    this.area.zipWithIndex.foreach(p => {
      val row = p._1
      val rowIndex = p._2
      
      val tiles = row.zipWithIndex.filter(p => p._1 == 1).map(p => new Coords(p._2+1, rowIndex+1))
      
      if (tiles.length > 0) {
        path = path ++ (if (tiles(0).x == endIndex) tiles else tiles.reverse)
        endIndex = tiles.last.x
      }
    })
    
    path
  }
  
  def getArea = this.area
  
  def isPointInsidePath(point: Point) = {
    val tileUnderPoint = this.path
      .map(tile => new Coords((tile.x - 1) * Constants.tileWidth, (tile.y - 1) * Constants.tileHeight))
      .find(tile => point.x > tile.x && point.x < tile.x + Constants.tileWidth && point.y > tile.y && point.y < tile.y + Constants.tileHeight)

    tileUnderPoint == None
  }

  def isPointOutSideArea(point: Point) = {
    (point.x < 0 || point.x > Constants.gameAreaWidth) || (point.y > Constants.gameAreaHeight || point.y < 0)
  }

}
