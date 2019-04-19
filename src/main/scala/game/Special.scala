package game

import java.awt.{ Point }

class Special(point: Point) extends DoesDamage {
  val position = new Coords(point.x, point.y)
  val damage: Int = 50
  val cost: Int = 50
  val range: Int = 50

  val requiredTicks: Int = 200
  var ticks: Int = 0
  var isExploding: Boolean = false

  def explode(enemies: Vector[Enemy]): Boolean = {
    // Either do damage to enemies, show the exploding animation with "isExploding" or increase the ticks i.e. advance time
    if (isExploding && ticks - 10 >= requiredTicks) {
      true
    } else if (!isExploding && ticks >= requiredTicks) {
      val enemiesInRange = this.enemiesInRange(enemies)
      enemiesInRange.foreach(enemy => enemy.doDamage(this.damage))
      isExploding = true
      
      ticks += 1
      false
    } else {
      ticks += 1
      false
    }

  }

}

