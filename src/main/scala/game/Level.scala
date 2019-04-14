package game

import scala.collection.mutable.Buffer

class Level(private val baseReward: Int, private val rewardMultiplier: Double) {
  var currentLevel: Int = 1

  val enemyIncreaseRate: Double = 1.06
  val enemySpeedMultiplier: Double = 1.02
  val enemyHealthMultiplier: Double = 1.05
  
  //(noOfEnemies, health, speed, noOfSuperEnemies)
  val baseValues = Map(
    1 -> (100, 100, 1.0, 1),
    5 -> (400, 250, 1.05, 3),
    10 -> (1000, 350, 1.1, 6),
    15 -> (1750, 500, 1.15, 10),
    20 -> (2500, 700, 1.2, 15),
    25 -> (3000, 950, 1.3, 20),
    30 -> (3500, 1250, 1.4, 30),
    35 -> (4000, 1600, 1.5, 35),
    40 -> (4250, 2000, 1.6, 40),
    45 -> (4500, 2450, 1.7, 50),
    50 -> (5000, 3000, 1.9, 60),
  )
  
  def getCurrentBaseValues = {
    val ones = this.currentLevel % 10
    val nearest5 = if (this.currentLevel < 5) 1 else (if (ones < 5) this.currentLevel - ones else (this.currentLevel - ones) + 5)
    val next5 = if (nearest5 == 1) 5 else nearest5 + 5
    
    val (baseNoOfEnemies, baseHealth, baseSpeed, baseNoOfSuperEnemies) = baseValues(nearest5)
    val (baseNoOfEnemies2, baseHealth2, baseSpeed2, baseNoOfSuperEnemies2) = baseValues(next5)
    
    val noOfEnemies = baseNoOfEnemies + (((baseNoOfEnemies2 - baseNoOfEnemies) / 5) * (if (ones < 5) ones else ones - 5))
    val health = baseHealth + (((baseHealth2 - baseHealth) / 5) * (if (ones < 5) ones else ones - 5))
    val speed = baseSpeed + (((baseSpeed2 - baseSpeed) / 5) * (if (ones < 5) ones else ones - 5))
    val noOfSuperEnemies = baseNoOfSuperEnemies + (((baseNoOfSuperEnemies2 - baseNoOfSuperEnemies) / 5) * (if (ones < 5) ones else ones - 5))
    println("NEW LEVEL: ", this.currentLevel, noOfEnemies, health, speed, noOfSuperEnemies )
    (noOfEnemies, health, speed, noOfSuperEnemies)
    //baseValues(nearest5)
  }
  
  def getSuperEnemies = {
    val (_, baseHealth, baseSpeed, noOfSuperEnemies) = this.getCurrentBaseValues
    val enemySpeed = baseSpeed //* math.pow(enemySpeedMultiplier, currentLevel)
    val enemyHealth = baseHealth * 3// * math.pow(enemyHealthMultiplier, currentLevel).toInt
    
    (0 to noOfSuperEnemies).toArray.map(i => new Enemy(enemySpeed, enemyHealth))
  }
  
  def nextLevel() = this.currentLevel += 1
  
  def setLevel(level: Int) = this.currentLevel = level

  def getEnemies(): Buffer[Enemy] = {
    val (baseNoOfEnemies, baseHealth, baseSpeed, _) = this.getCurrentBaseValues
    val enemySpeed = baseSpeed //* math.pow(enemySpeedMultiplier, currentLevel)
    val enemyHealth = baseHealth //* math.pow(enemyHealthMultiplier, currentLevel).toInt
    val enemiesTotal = baseNoOfEnemies //* math.pow(enemyIncreaseRate, currentLevel).toInt
    
    val superEnemies = this.getSuperEnemies

    ((0 to enemiesTotal).map(i => new Enemy(enemySpeed, enemyHealth))).toBuffer
  }
  
  def getReward(): Int = {
    (baseReward * math.pow(rewardMultiplier, currentLevel)).toInt
  }
}
