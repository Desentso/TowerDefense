import scala.swing.Graphics2D
import java.awt.Color

import game.Enemy
import game.Constants

object EnemyGUI {
  
  def draw(enemy: Enemy, g: Graphics2D) = {
    val enemyX = math.round(enemy.position.x).toInt
    val enemyY = math.round(enemy.position.y).toInt
    
    g.setColor(Color.BLACK)
    g.fillRect(enemyX - (Constants.enemySize / 2) - 1, enemyY - (Constants.enemySize / 2) - 1, Constants.enemySize + 2, Constants.enemySize + 2)
    g.setColor(Constants.enemyColor)
    g.fillRect(enemyX - (Constants.enemySize / 2), enemyY - (Constants.enemySize / 2), Constants.enemySize, Constants.enemySize)
    
    // Draw enemy health bars
    if (enemy.health < enemy.initHealth) {
      g.setColor(Color.GREEN)
      g.fillRect(enemyX - (Constants.enemySize / 2), enemyY - (Constants.enemySize / 2) - 5, (Constants.enemySize * ((enemy.health * 1.0) / enemy.initHealth)).toInt, 3)
    }
  }
}