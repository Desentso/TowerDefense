package game

class Enemy() {
  var position: Coords = new Coords(0, 0)
  var health: Int = 100
  var speed: Double = 1.0
  var distanceToGoal: Int = 100
  
  def shoot(damageDealt: Int) = {
    this.health -= damageDealt
  }
  
}

object DummyEnemy extends Enemy {
  
}
