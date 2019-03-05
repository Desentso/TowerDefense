import scala.swing.Graphics2D
import java.awt.Color

object GameArea {
  
  def draw(area: Vector[Vector[Int]], g: Graphics2D, width: Int, height: Int) = {
    val tileWidth = width / area(0).length
    val tileHeight = height / area.length
    
    var x = 0
    var y = 0
    
    area.foreach(row => {
      row.foreach(tile => {
        g.setColor(if (tile == 0) new Color(14, 146, 60) else new Color(225, 191, 146))
        g.fillRect(x, y, tileWidth, tileHeight)
        x += tileWidth
      })
      x = 0
      y += tileHeight
    })
  }
}
