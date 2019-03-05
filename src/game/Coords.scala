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
}
