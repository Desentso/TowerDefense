import scala.swing._
import java.awt.event.ActionListener
import java.awt.{ Point, Rectangle }
import scala.io.Source
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.swing.event._

object GUI extends SimpleSwingApplication {

  
  def top = new MainFrame {
    title = "Tower Defense ABC"

    val width = 800
    val height = 560
    
    // Set the width and height of the window
    minimumSize = new Dimension(width, height)
    preferredSize = new Dimension(width, height)
    maximumSize = new Dimension(width, height)
    
    def container = new BoxPanel(Orientation.Vertical) {

      def startGameBtn = new Button("Start Game")
      def loadGameBtn = new Button("Load Game")
      
      def exitGameBtn = new Button("Exit")
      
      contents += startGameBtn
      contents += loadGameBtn
      contents += exitGameBtn
    }
    
    // Set the contents 
    contents = container
    
    val gameLoopEventListener = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        container.repaint() 
      }  
    }

    // This calls the gameLoopEventListener every 16ms
    // Which allows animation and dynamic screens
    val gameLoop = new javax.swing.Timer(16, gameLoopEventListener)
    gameLoop.start()
  }
}
