package game

class Coords(initX: Int, initY: Int) {
  var x: Int = initX
  var y: Int = initY
  
  def x(add: Int): Unit = {
    this.x += add
  }
  
  def y(add: Int): Unit = {
    this.y += add
  }
  
  override def toString() = "(" + this.x + "," + this.y + ")"
}


class CoordsD(initX: Double, initY: Double){
  var x: Double = initX
  var y: Double = initY
  
  def x(add: Double): Unit = {
    this.x += add
  }
  
  def y(add: Double): Unit = {
    this.y += add
  }
  
  override def toString() = "(" + this.x + "," + this.y + ")"
}

