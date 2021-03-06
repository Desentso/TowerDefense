package game

class Enemy(initSpeed: Double = Constants.enemyDefaultSpeed, val initHealth: Int = Constants.enemyDefaultHealth) {
  var position: CoordsD = new CoordsD(0, 0)
  var health: Int = initHealth
  var speed: Double = initSpeed
  var distanceToGoal: Int = 0
  var pathPosition: Int = 0
  var direction = Direction.none
  
  def doDamage(damageDealt: Int) = {
    this.health -= damageDealt
  }
  
  def move(x: Int, y: Int, noSpeed: Boolean = false) = {
    val actualSpeed = (if (noSpeed) 1 else this.speed)
    this.position.x(x * actualSpeed)
    this.position.y(y * actualSpeed)
    
    this.distanceToGoal -= (math.abs(x) + math.abs(y))
  }
  
  // Check if this enemy has reached the end of path, i.e. is out of the game area
  def hasReachedEnd = (
    this.position.y > Constants.gameAreaHeight || 
    this.position.y < 0 || 
    this.position.x < 0 || 
    this.position.x > Constants.gameAreaWidth
  )
  
}

object DummyEnemy extends Enemy {
  override def doDamage(dmg: Int) = health -= dmg
}
