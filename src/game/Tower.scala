package game

abstract class Tower(val position: Coords) {
  var damage: Int 
  var range: Int
  val towerType: String
  
  def shoot() = {
    this.enemiesInRange().sortBy(enemy => enemy.distanceToGoal).lift(0).getOrElse(DummyEnemy).shoot(this.damage)
  }
  
  def enemiesInRange(): Vector[Enemy] = {
    
    Vector[Enemy]()
  }
}

