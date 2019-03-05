import scala.swing._
import java.awt.event.ActionListener
import java.awt.{ Point, Rectangle, Color }
import scala.io.Source
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.swing.event._
import game._


object GUI extends SimpleSwingApplication {

  val game = new Game()
  var state = game.currentState
  
  def top = new MainFrame {
    title = "Tower Defense ABC"

    val width = 800
    val height = 560
    
    // Set the width and height of the window
    minimumSize = new Dimension(width, height)
    preferredSize = new Dimension(width, height)
    maximumSize = new Dimension(width, height)
    
    val startGameBtn = new Button("Start Game")
    val loadGameBtn = new Button("Load Game")
    val exitGameBtn = new Button("Exit")

    def startScreen: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += startGameBtn
      contents += loadGameBtn
      contents += exitGameBtn
    }
    
    class gamePanel extends Panel {

      override def paintComponent(g: Graphics2D): Unit = {
        g.clearRect(0, 0, width, height)
        
        //g.setBackground()
        g.setColor(Color.RED)
        
        game.enemies.foreach(enemy => g.fillRect(enemy._1, enemy._2, 10, 10))
      }
    }
    
    val gameScreen: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += new gamePanel()
      contents += exitGameBtn
    }
    
    val container: BoxPanel = new BoxPanel(Orientation.Vertical) {
      println("State in container: " + state)
      // Set the contents of the window

      contents += startScreen
    }
    
    // Listen to buttons
    this.listenTo(startGameBtn)
    this.listenTo(exitGameBtn)
    
    // Listen to game area clicks
    this.listenTo(gameScreen.mouse.clicks)
    
    this.reactions += {
      case btnClicked: ButtonClicked => {
        val sourceBtn = btnClicked.source
        val btnText = sourceBtn.text

        btnText match {
          case "Start Game" => {
            game.startGame()
            state = game.currentState
            println("State in listener: " + state)
            
            container.contents -= startScreen
            
            container.revalidate()
            container.repaint()
            
            container.contents += gameScreen

            top.repaint()
            
            container.revalidate()
            container.repaint()
            
            top.validate()
            top.repaint()
            
            gameLoop.start()
            
          }
          case "Exit" => System.exit(0)
        }
      }
      //case scala.swing.event.MousePressed(src, point, _, _, _) => game.onMouseClick(src, point)
    }
    
    contents = container
    
    val gameLoopEventListener = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        state = game.currentState
        
        gameScreen.revalidate()
        gameScreen.repaint()
        //top.repaint()
        
        game.onTick()
      }
    }

    // This calls the gameLoopEventListener every 16ms
    // Which allows animation and dynamic screens
    val gameLoop = new javax.swing.Timer(16, gameLoopEventListener)
    //gameLoop.start()
  }
}


