package game

class Player() {
  var coins: Int = 500
  var health: Int = 250
  
  def enemyDidReachEndOfPath() = this.health -= 1
  
  def towerBought(tower: Tower) = this.coins -= tower.cost
  
  def hasCoinsToBuyTower(tower: Tower) = this.coins >= tower.cost
  
  def isAlive = this.health > 0
  
  def addCoins(coins: Int) = this.coins += coins
}
