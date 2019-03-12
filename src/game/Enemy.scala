package game

class Enemy(initSpeed: Double = 1.0, initHealth: Int = 150) {
  var position: Coords = new Coords(0, 0)
  var health: Int = initHealth
  var speed: Double = initSpeed
  var distanceToGoal: Int = 0
  var pathPosition: Int = 0
  var direction = Direction.none
  
  def shoot(damageDealt: Int) = {
    this.health -= damageDealt
    println("Shot enemy, health:", this.health)
  }
  
  def move(x: Int, y: Int) = {
    this.position.x(x)
    this.position.y(y)
    
    this.distanceToGoal -= (math.abs(x) + math.abs(y))
  }
  
  def hasReachedEnd = (
    this.position.y > Constants.gameAreaHeight || 
    this.position.y < 0 || 
    this.position.x < 0 || 
    this.position.x > Constants.gameAreaWidth
  )
  
}

object DummyEnemy extends Enemy {
  override def shoot(dmg: Int) = health -= dmg
}
