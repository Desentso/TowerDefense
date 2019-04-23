import scala.swing.Graphics2D
import java.awt.Color

import game.Tower
import game.Constants

object TowerGUI {
  
  def draw(tower: Tower, g: Graphics2D) = {
    g.fillRect(tower.position.x - (Constants.towerSize / 2), tower.position.y - (Constants.towerSize), Constants.towerSize, Constants.towerSize)
    g.drawOval(tower.position.x - tower.range, tower.position.y - tower.range - (Constants.towerSize / 2), tower.range * 2, tower.range * 2)
  }
}