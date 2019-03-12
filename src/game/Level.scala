package game

import scala.collection.mutable.Buffer

class Level() {
  var currentLevel: Int = 0
  private val baseReward: Int = 100
  private val baseEnemies: Int = 100
  
  val rewardMultiplier: Double = 1.055
  val enemyIncreaseRate: Double = 1.055
  val enemySpeedMultiplier: Double = 1.015
  val enemyHealthMultiplier: Double = 1.015
  
  val enemies = Map(
    1 -> Vector((0 to 100).toArray.map(i => new Enemy())),
    5 -> Vector((0 to 100).toArray.map(i => new Enemy())),
    10 -> Vector((0 to 100).toArray.map(i => new Enemy()))
  )
  
  def nextLevel() = this.currentLevel += 1
  
  def getEnemies(): Buffer[Enemy] = {
    val enemySpeed = 1.0 * math.pow(enemySpeedMultiplier, currentLevel)
    val enemyHealth = 150 * math.pow(enemyHealthMultiplier, currentLevel).toInt
    val enemiesTotal = baseEnemies * math.pow(enemyIncreaseRate, currentLevel).toInt
    
    (0 to enemiesTotal).map(i => new Enemy(enemySpeed, enemyHealth)).toBuffer
  }
  
  def getReward() = {
    baseReward * math.pow(rewardMultiplier, currentLevel)
  }
}
