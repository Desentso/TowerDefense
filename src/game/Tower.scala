package game

abstract class Tower(val position: Coords, val game: Game) {
  var damage: Int 
  var range: Int
  var rateOfFire: Double
  val towerType: String
  
  def shoot() = {
    println(enemiesInRange.map(e => e.distanceToGoal))
    this.enemiesInRange().lift(0).getOrElse(DummyEnemy).shoot(this.damage)
  }
  
  def enemiesInRange(): Vector[Enemy] = {
    this.game.enemies
      .filter(enemy => math.sqrt(math.pow(this.position.x - enemy.position.x, 2) + math.pow(this.position.y - enemy.position.y, 2)) < this.range)
      .sortBy(enemy => enemy.distanceToGoal)
      .toVector
  }
}


class Tower1(position: Coords, game: Game) extends Tower(position, game) {
  var damage: Int = 10
  var range: Int = 50
  var rateOfFire: Double = 1.0
  val towerType = "Tower 1"
}

class Tower2(position: Coords, game: Game) extends Tower(position, game) {
  var damage: Int = 8
  var range: Int = 100
  var rateOfFire: Double = 0.8
  val towerType = "Tower 2"
}
