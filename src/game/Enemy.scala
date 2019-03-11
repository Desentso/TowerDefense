package game

class Enemy() {
  var position: Coords = new Coords(0, 0)
  var health: Int = 100
  var speed: Double = 1.0
  var distanceToGoal: Int = 0
  var pathPosition: Int = 0
  var direction = Direction.none
  
  def shoot(damageDealt: Int) = {
    this.health -= damageDealt
    println("health: ", this.health)
  }
  
  def move(x: Int, y: Int) = {
    this.position.x(x)
    this.position.y(y)
    
    this.distanceToGoal -= x
  }
  
  def hasReachedEnd = (
    this.position.y > Constants.windowHeight || 
    this.position.y < 0 || 
    this.position.x < 0 || 
    this.position.x > Constants.windowWidth
  )
  
}

object DummyEnemy extends Enemy {
  
}
