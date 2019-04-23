package game

import java.awt.{ Point }
import play.api.libs.json._

class Special(point: Point, val damage: Int, val cost: Int, val range: Int, val requiredTicks: Int) extends DoesDamage {
  val position = new Coords(point.x, point.y)
  var ticks: Int = 0
  var isExploding: Boolean = false

  def explode(enemies: Vector[Enemy]): Boolean = {
    // Either do damage to enemies, show the exploding "animation" with isExploding or increase the ticks i.e. advance time
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


// Handles the correct applying of the configuration file values
class SpecialHandler(jsonConf: JsValue) {

  val damage = (jsonConf \ "damage").as[Int]
  val cost = (jsonConf \ "cost").as[Int]
  val range = (jsonConf \ "range").as[Int]
  val requiredTicks = (jsonConf \ "ticksBeforeExplode").as[Int]

  def getSpecial(point: Point) = new Special(point, damage, cost, range, requiredTicks)
}

