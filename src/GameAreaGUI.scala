import scala.swing.Graphics2D
import java.awt.Color

import game.Constants

object GameArea {
  
  def draw(area: Array[Array[Int]], g: Graphics2D, width: Int, height: Int) = {
    //val tileWidth = width / area(0).length
    //val tileHeight = height / area.length
    
    var x = 0
    var y = 0
    
    area.foreach(row => {
      row.foreach(tile => {
        g.setColor(if (tile == 0) Constants.fillColor else Constants.pathColor)
        g.fillRect(x, y, Constants.tileWidth, Constants.tileHeight)
        x += Constants.tileWidth
      })
      x = 0
      y += Constants.tileHeight
    })
  }
}
