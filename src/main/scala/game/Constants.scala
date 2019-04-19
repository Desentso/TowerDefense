package game

import java.awt.Color
import play.api.libs.json._

object Constants {
  val pathColor: Color = new Color(225, 191, 146)
  val fillColor: Color = new Color(14, 146, 60)
  val enemyColor: Color = Color.RED
  val towerColor: Color = Color.BLUE
  val specialColor: Color = Color.ORANGE

  val gameAreaWidth: Int = 800
  val gameAreaHeight: Int = 560
  
  val tileWidth: Int = gameAreaWidth / 7
  val tileHeight: Int = gameAreaHeight / 7
  
  val enemySize: Int = 10
  val towerSize: Int = 30
  val specialSize: Int = 15

  val defaultStartHealth: Int = 250
  val defaultStartCoins: Int = 1000
  val defaultLevelCompleteReward: Int = 100
  val defaultLevelCompleteIncreaseRate: Double = 1.005
  val defaultEnemyKilledReward: Int = 1

  val defaultLevelRequiredForWin: Int = 50

  val defaultTowerConf: List[JsValue] = List(
    Json.obj("cost" -> 400,  "damage" -> 10, "rateOfFire" -> 0.7, "range" -> 75),
    Json.obj("cost" -> 500,  "damage" -> 8,  "rateOfFire" -> 0.7, "range" -> 100),
    Json.obj("cost" -> 1000, "damage" -> 10, "rateOfFire" -> 0.8, "range" -> 150),
    Json.obj("cost" -> 1000, "damage" -> 50, "rateOfFire" -> 0.1, "range" -> 400),
    Json.obj("cost" -> 2500, "damage" -> 20, "rateOfFire" -> 1.0, "range" -> 250)
  )

  val defaultSpecial: JsValue = Json.obj(
    "cost" -> 50,
    "damage" -> 50,
    "range" -> 50,
    "ticksBeforeExplode" -> 200
  )
}
