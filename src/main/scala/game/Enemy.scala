package game

class Enemy(initSpeed: Double = 1.0, initHealth: Int = 150) {
  var position: CoordsD = new CoordsD(0, 0)
  var health: Int = initHealth
  var speed: Double = initSpeed
  var distanceToGoal: Int = 0
  var pathPosition: Int = 0
  var direction = Direction.none
  
  def shoot(damageDealt: Int) = {
    this.health -= damageDealt
    //println("Shot enemy, health:", this.health)
  }
  
  def move(x: Int, y: Int, noSpeed: Boolean = false) = {
    val actualSpeed = (if (noSpeed) 1 else this.speed)
    this.position.x(x * actualSpeed)
    this.position.y(y * actualSpeed)
    
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
