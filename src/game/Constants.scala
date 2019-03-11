package game

import java.awt.Color

object Constants {
  val pathColor: Color = new Color(225, 191, 146)
  val fillColor: Color = new Color(14, 146, 60)
  val enemyColor: Color = Color.RED
  val towerColor: Color = Color.BLUE
  
  val windowWidth: Int = 800
  val windowHeight: Int = 560
  
  val tileWidth: Int = windowWidth / 7
  val tileHeight: Int = windowHeight / 6
  
  val enemySize: Int = 10
  val towerSize: Int = 30
}
