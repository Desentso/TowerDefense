package game
import play.api.libs.json._

class Player(var health: Int, var coins: Int) {
  
  def enemyDidReachEndOfPath() = this.health -= 1
  
  def towerBought(tower: Tower) = this.coins -= tower.cost
  
  def hasCoinsToBuyTower(tower: Tower) = this.coins >= tower.cost

  def specialBought(special: Special) = this.coins -= special.cost

  def hasCoinsToBuySpecial(special: Special) = this.coins >= special.cost

  def isAlive = this.health > 0
  
  def addCoins(coins: Int) = this.coins += coins

  def reinitSavedPlayer(json: JsValue) = {
    this.coins = (json \ "coins").as[Int]
    this.health = (json \ "healthLeft").as[Int]
  }

}
