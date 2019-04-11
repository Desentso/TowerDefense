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
    
    def listenToBtn(btn: Button) = {
      this.listenTo(btn)
    }
    
    title = "Tower Defense ABC"

    val width = 800
    val height = 660
    
    // Set the width and height of the window
    minimumSize = new Dimension(width, height)
    preferredSize = new Dimension(width, height)
    maximumSize = new Dimension(width, height)
    
    val startGameBtn = new Button("Start Game")
    val loadGameBtn = new Button("Load Game")
    val exitGameBtn = new Button("Exit")
    val tower1Btn = new Button("Tower 1")
    val tower2Btn = new Button("Tower 2")
    val changeSpeedBtn = new Button(">")

    def startScreen: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += startGameBtn
      contents += loadGameBtn
      contents += exitGameBtn
    }
    
    class gamePanel extends Panel {

      override def paintComponent(g: Graphics2D): Unit = {
        g.clearRect(0, 0, Constants.gameAreaWidth, Constants.gameAreaHeight)
        
        GameArea.draw(game.gameArea.getArea, g, Constants.gameAreaWidth, Constants.gameAreaHeight)
        
        //g.setBackground()
        g.setColor(Constants.enemyColor)
        game.enemies.foreach(enemy => {
          val enemyX = math.round(enemy.position.x).toInt
          val enemyY = math.round(enemy.position.y).toInt
          
          g.setColor(Color.BLACK)
          g.fillRect(enemyX - (Constants.enemySize / 2) - 1, enemyY - (Constants.enemySize / 2) - 1, Constants.enemySize + 2, Constants.enemySize + 2)
          g.setColor(Constants.enemyColor)
          g.fillRect(enemyX - (Constants.enemySize / 2), enemyY - (Constants.enemySize / 2), Constants.enemySize, Constants.enemySize)
        })
        
        g.setColor(Constants.towerColor)
        game.towers.foreach(tower => {
          g.fillRect(tower.position.x - (Constants.towerSize / 2), tower.position.y - (Constants.towerSize / 2), Constants.towerSize, Constants.towerSize)
          g.drawOval(tower.position.x - tower.range, tower.position.y - tower.range, tower.range * 2, tower.range * 2)
        })
      }
    }
    
    val gameScreenButtons: BoxPanel = new BoxPanel(Orientation.Horizontal) {
      val exitGameBtn = new Button("Exit") // The other exit game btn is empty/garbage collected(?) after contents change
      
      contents += exitGameBtn
      contents += tower1Btn
      contents += tower2Btn
      contents += changeSpeedBtn
      
      listenToBtn(exitGameBtn)
    }
    
    val levelLabel = new Label("Level: " + (game.levelHandler.currentLevel + 1))
    val healthLabel = new Label("   Health: " + game.player.health)
    val coinsLabel = new Label("   Coins: " + game.player.coins)
    
    def updateLabels() = {
      levelLabel.text = "Level: " + (game.levelHandler.currentLevel + 1) 
      healthLabel.text = "   Health: " + game.player.health
      coinsLabel.text = "   Coins: " + game.player.coins
    }
    
    val gameScreenLabels: BoxPanel = new BoxPanel(Orientation.Horizontal) {
      contents += levelLabel
      contents += healthLabel
      contents += coinsLabel

    }
    
    val gameScreen: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += gameScreenLabels
      contents += new gamePanel()
      contents += gameScreenButtons
    }
    
    val container: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += startScreen
    }

    def startGame() = {
      game.startGame()
      state = game.currentState

      container.contents -= startScreen
      container.contents += gameScreen

      container.revalidate()
      container.repaint()

      top.repaint()
      
      gameLoop.start()
    }
    
    // Listen to buttons
    this.listenTo(startGameBtn)
    this.listenTo(loadGameBtn)
    this.listenTo(exitGameBtn)
    this.listenTo(tower1Btn)
    this.listenTo(tower2Btn)
    this.listenTo(changeSpeedBtn)
    
    // Listen to game area clicks
    this.listenTo(gameScreen.mouse.clicks)
    
    this.reactions += {
      case btnClicked: ButtonClicked => {
        val sourceBtn = btnClicked.source
        val btnText = sourceBtn.text

        btnText match {
          case "Start Game" => {
            startGame()
          }
          case "Load Game" => {
            game.loadGame()
            startGame()
          }
          case "Tower 1" => {
            game.selectedTower(1)
            println("Selected tower 1")
          }
          case "Tower 2" => {
            game.selectedTower(2)
            println("Selected tower 2")
          }
          case ">" => {
            println("Increase speed")
            gameLoop.stop()
            gameLoop = new javax.swing.Timer(8, gameLoopEventListener)
            gameLoop.start()
            changeSpeedBtn.text = ">>"
          }
          case ">>" => {
            println("Lower speed")
            gameLoop.stop()
            gameLoop = new javax.swing.Timer(16, gameLoopEventListener)
            gameLoop.start()
            changeSpeedBtn.text = ">"
          }
          case "Exit" => System.exit(0)
        }
      }
      case scala.swing.event.MousePressed(src, point, _, _, _) => game.onMouseClick(src, point)
    }
    
    contents = container
    
    val gameLoopEventListener = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        state = game.currentState
        
        gameScreen.revalidate()
        gameScreen.repaint()

        updateLabels()
        
        game.onTick()
        
        if (game.isGameOver) {
          (e.getSource.asInstanceOf[javax.swing.Timer]).stop()
          // Render game over screen
          
        }
      }
    }

    // This calls the gameLoopEventListener every 16ms
    // Which allows animation and dynamic screens
    var gameLoop = new javax.swing.Timer(16, gameLoopEventListener)
    //gameLoop.start()
  }
}

