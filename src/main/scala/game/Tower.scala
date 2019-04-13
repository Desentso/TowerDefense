package game

import play.api.libs.json._

class Tower(val damage: Int, val range: Int, val rateOfFire: Double, val cost: Int, val towerType: String, val position: Coords, val game: Game) {
  
  def shoot(tick: Int) = {
    //println(tick, 10.0 / (rateOfFire * 10), tick % (10.0 / (rateOfFire * 10)))
    if (tick % (10.0 / (rateOfFire * 10)) < 1) {
      val enemies = this.enemiesInRange
      //println(enemies.map(e => e.distanceToGoal))
      //println(enemies.lift(0))
      enemies.lift(0).getOrElse(DummyEnemy).shoot(this.damage)
    }
  }
  
  def enemiesInRange: Vector[Enemy] = {
    this.game.enemies
      .filter(enemy => math.sqrt(math.pow(this.position.x - enemy.position.x, 2) + math.pow(this.position.y - enemy.position.y, 2)) < this.range)
      .sortBy(enemy => enemy.distanceToGoal)
      .toVector
  }
}

class TowerConf(val range: Int, val damage: Int, val cost: Int, val rateOfFire: Double) {}

class Towers(val game: Game, towersAsJson: List[JsValue]) {

  val towers: Map[Int, TowerConf] = towersAsJson.zipWithIndex.map(p => {
    val tower = p._1
    val index = p._2

    val range = (tower \ "range").as[Int]
    val damage = (tower \ "damage").as[Int]
    val cost = (tower \ "cost").as[Int]
    val rateOfFire = (tower \ "rateOfFire").as[Double]

    (index + 1, new TowerConf(range, damage, cost, rateOfFire))
  }).toMap

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
