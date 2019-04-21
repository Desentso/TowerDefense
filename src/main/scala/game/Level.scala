package game

import scala.collection.mutable.Buffer

class Level(private val baseReward: Int, private val rewardMultiplier: Double) {
  var currentLevel: Int = 1

  val enemyIncreaseRate: Double = 1.06
  val enemySpeedMultiplier: Double = 1.02
  val enemyHealthMultiplier: Double = 1.05
  
  // In format: (noOfEnemies, health, speed, noOfSuperEnemies)
  private val baseValues = Map(
    1 -> (100, 90, 1.0, 1),
    5 -> (300, 200, 1.05, 3),
    10 -> (800, 350, 1.1, 6),
    15 -> (1500, 500, 1.15, 10),
    20 -> (2250, 700, 1.2, 15),
    25 -> (3000, 950, 1.3, 20),
    30 -> (3500, 1250, 1.4, 30),
    35 -> (4000, 1600, 1.5, 35),
    40 -> (4250, 2000, 1.6, 40),
    45 -> (4500, 2450, 1.7, 50),
    50 -> (5000, 3000, 1.9, 60),
    
  )
  
  private def getCurrentBaseValues = {
    val ones = this.currentLevel % 10
    val nearest5 = if (this.currentLevel < 5) 1 else (if (ones < 5) this.currentLevel - ones else (this.currentLevel - ones) + 5)
    val (baseNoOfEnemies, baseHealth, baseSpeed, baseNoOfSuperEnemies) = baseValues(nearest5)

    // To allow unlimited levels after win
    if (nearest5 == 50) {
      val noOfEnemies = (baseNoOfEnemies * math.pow(enemyIncreaseRate, this.currentLevel)).toInt
      val health = (baseHealth * math.pow(enemyHealthMultiplier, this.currentLevel)).toInt
      val speed = (baseSpeed * math.pow(enemySpeedMultiplier, this.currentLevel)).toDouble
      val noOfSuperEnemies = (baseNoOfSuperEnemies * math.pow(enemyIncreaseRate, this.currentLevel)).toInt
      
      (noOfEnemies, health, speed, noOfSuperEnemies)

    } else {
      val next5 = if (nearest5 == 1) 5 else nearest5 + 5
      val (baseNoOfEnemiesNext, baseHealthNext, baseSpeedNext, baseNoOfSuperEnemiesNext) = baseValues(next5)
      
      val noOfEnemies = baseNoOfEnemies + (((baseNoOfEnemiesNext - baseNoOfEnemies) / 5) * (if (ones < 5) ones else ones - 5))
      val health = baseHealth + (((baseHealthNext - baseHealth) / 5) * (if (ones < 5) ones else ones - 5))
      val speed = baseSpeed + (((baseSpeedNext - baseSpeed) / 5) * (if (ones < 5) ones else ones - 5))
      val noOfSuperEnemies = baseNoOfSuperEnemies + (((baseNoOfSuperEnemiesNext - baseNoOfSuperEnemies) / 5) * (if (ones < 5) ones else ones - 5))
      
      (noOfEnemies, health, speed, noOfSuperEnemies)
    }
  }
  
  private def getSuperEnemies = {
    val (_, baseHealth, baseSpeed, noOfSuperEnemies) = this.getCurrentBaseValues
    val enemySpeed = baseSpeed 
    val enemyHealth = baseHealth * 3
    
    (0 to noOfSuperEnemies).toArray.map(i => new Enemy(enemySpeed, enemyHealth))
  }
  
  def nextLevel() = this.currentLevel += 1
  
  def setLevel(level: Int) = this.currentLevel = level

  def getEnemies(): Buffer[Enemy] = {
    val (baseNoOfEnemies, baseHealth, baseSpeed, _) = this.getCurrentBaseValues
    val enemySpeed = baseSpeed 
    val enemyHealth = baseHealth 
    val enemiesTotal = baseNoOfEnemies
    
    val superEnemies = this.getSuperEnemies

    ((0 to enemiesTotal).map(i => new Enemy(enemySpeed, enemyHealth))).toBuffer
  }
  
  def getReward(): Int = {
    (baseReward * math.pow(rewardMultiplier, currentLevel)).toInt
  }
}
