package game

import play.api.libs.json._

class Tower(val damage: Int, val range: Int, val rateOfFire: Double, val cost: Int, val towerType: String, val position: Coords, val game: Game) extends DoesDamage {
  
  def shoot(tick: Int) = {
    if (this.shouldFire(tick)) {
      val enemies = this.enemiesInRange(this.game.enemies.toVector)
      enemies.lift(0).getOrElse(DummyEnemy).doDamage(this.damage)
    }
  }

  private def shouldFire(tick: Int) = tick % (Constants.rateOfFireGranularity.toDouble / (rateOfFire *  Constants.rateOfFireGranularity)) < 1
}

class TowerConf(val range: Int, val damage: Int, val cost: Int, val rateOfFire: Double) {}

// Handles the correct applying of the configuration file values
class TowerHandler(val game: Game, towersAsJson: List[JsValue]) {

  val towers: Map[Int, TowerConf] = towersAsJson.zipWithIndex.map(p => {
    val tower = p._1
    val index = p._2

    val range = (tower \ "range").as[Int]
    val damage = (tower \ "damage").as[Int]
    val cost = (tower \ "cost").as[Int]
    val rateOfFire = (tower \ "rateOfFire").as[Double]

    (index + 1, new TowerConf(range, damage, cost, rateOfFire))
  }).toMap

  // Get a new instance for certain tower type
  def getTower(towerType: Int, coords: Coords = new Coords(0, 0)): Option[Tower] = {
    val towerConfOption = towers.get(towerType)

    if (towerConfOption != None) {
      val towerConf = towerConfOption.get
      Some(new Tower(towerConf.damage, towerConf.range, towerConf.rateOfFire, towerConf.cost, towerType.toString, coords, game))
    } else {
      None
    }
  }
}
