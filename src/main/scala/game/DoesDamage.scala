package game

trait DoesDamage {
  // Tower and Special implement this trait
  val damage: Int
  val range: Int
  val cost: Int
  val position: Coords

  def enemiesInRange(enemies: Vector[Enemy]): Vector[Enemy] = {
    enemies
      .filter(enemy => math.sqrt(math.pow(this.position.x - enemy.position.x, 2) + math.pow(this.position.y - enemy.position.y, 2)) < this.range)
      .sortBy(enemy => enemy.distanceToGoal)
      .toVector
  }
}
