import scala.swing.Graphics2D
import java.awt.Color

import game.Special
import game.Constants

object SpecialGUI {
  
  def draw(special: Special, g: Graphics2D) = {
    g.fillRect(special.position.x - (Constants.specialSize / 2), special.position.y - (Constants.specialSize), Constants.specialSize, Constants.specialSize)
    // Draw the explosion
    if (special.isExploding) {
      g.fillOval(special.position.x - special.range, special.position.y - special.range - (Constants.specialSize / 2), special.range * 2, special.range * 2)
    }
  }
}