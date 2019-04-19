import scala.swing._
import java.awt.event.ActionListener
import java.awt.{ Point, Rectangle, Color }
import scala.io.Source
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.swing.event._
import javax.swing.ImageIcon
import game._
import java.nio.file.Paths

object GUI extends SimpleSwingApplication {

  var game = new Game()
  
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
    val playAgainBtn = new Button("Play Again")
    val changeSpeedBtn = new Button(">")

    val tower1Btn = new Button("Tower 1")
    val tower2Btn = new Button("Tower 2")
    val tower3Btn = new Button("Tower 3")
    val tower4Btn = new Button("Tower 4")
    val tower5Btn = new Button("Tower 5")
    val specialBtn = new Button("Special")

    val notificationsLabel = new Label("")
    val levelLabel = new Label("Level: " + (game.currentLevel))
    val healthLabel = new Label("   Health: " + game.player.health)
    val coinsLabel = new Label("   Coins: " + game.player.coins)
    val selectedTowerLabel = new Label("<html><br></html>")
    
    def updateLabels() = {
      levelLabel.text = "Level: " + (game.currentLevel) 
      healthLabel.text = "   Health: " + game.player.health
      coinsLabel.text = "   Coins: " + game.player.coins
      val selectingTower = game.towerHandler.getTower(game.selectingTower)
      selectedTowerLabel.text = if (selectingTower == None) "<html><br><br><br><br><br><br></html>" else (
        "<html><br>" + 
        "Tower " + selectingTower.get.towerType + 
        "<br><br>Cost: " + selectingTower.get.cost + " coins" + 
        "<br>Damage: " + selectingTower.get.damage + 
        "<br>Rate of fire: " + selectingTower.get.rateOfFire +
        "<br></html>"
      )
    }

    def newNotification(notificationText: String) = {
      notificationsLabel.text = "   " + notificationText
    }


    val startScreen = new BoxPanel(Orientation.Vertical) {
      val startScreenButtons: BoxPanel = new BoxPanel(Orientation.Horizontal) {
        contents += startGameBtn
        contents += loadGameBtn
        contents += exitGameBtn
      }
      contents += new BoxPanel(Orientation.Horizontal) {contents += new Label("<html><br><br><br><br><h1>Tower Defense Game</h1><br><br><br><br></html>")}
      contents += startScreenButtons
      contents += new BoxPanel(Orientation.Horizontal) {contents += notificationsLabel}
    }
    

    class GamePanel extends Panel {

      override def paintComponent(g: Graphics2D): Unit = {
        g.clearRect(0, 0, Constants.gameAreaWidth, Constants.gameAreaHeight)
        
        GameArea.draw(game.gameArea.getArea, g, Constants.gameAreaWidth, Constants.gameAreaHeight)
        
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
          g.fillRect(tower.position.x - (Constants.towerSize / 2), tower.position.y - (Constants.towerSize), Constants.towerSize, Constants.towerSize)
          g.drawOval(tower.position.x - tower.range, tower.position.y - tower.range - (Constants.towerSize / 2), tower.range * 2, tower.range * 2)
        })

        g.setColor(Constants.specialColor)
        game.specials.foreach(special => {
          g.fillRect(special.position.x - (Constants.specialSize / 2), special.position.y - (Constants.specialSize), Constants.specialSize, Constants.specialSize)
          if (special.isExploding) {
            g.fillOval(special.position.x - special.range, special.position.y - special.range - (Constants.specialSize / 2), special.range * 2, special.range * 2)
          }
        })
      }
    }


    val gameScreen: BoxPanel = new BoxPanel(Orientation.Vertical) {
      val gameScreenLabels: BoxPanel = new BoxPanel(Orientation.Horizontal) {
        contents += levelLabel
        contents += healthLabel
        contents += coinsLabel
        contents += notificationsLabel
      }

      val gameScreenButtons: BoxPanel = new BoxPanel(Orientation.Horizontal) {
        //val exitGameBtn = new Button("Exit") // The other exit game btn is empty/garbage collected(?) after contents change
        
        //contents += exitGameBtn
        contents += tower1Btn
        contents += tower2Btn
        contents += tower3Btn
        contents += tower4Btn
        contents += tower5Btn
        contents += specialBtn
        contents += changeSpeedBtn
        
        //listenToBtn(exitGameBtn)
      }
      
      val towerInfoPanel = new BoxPanel(Orientation.Horizontal) {
        contents += selectedTowerLabel
      }

      val bottomInfoPanel = new BoxPanel(Orientation.Horizontal) {
        contents += gameScreenButtons
        contents += towerInfoPanel
      }

      contents += gameScreenLabels
      contents += new GamePanel()
      contents += bottomInfoPanel
    }


    val gameOverLabel = new Label("")
    def updateGameOverLabel() = {
      gameOverLabel.text = ("<html><br><br><br><br><h1>Game over!</h1><br><br><h3>You reached level " + game.currentLevel +  "</h3><br><br></html>")
    }

    val gameOverScreen = new BoxPanel(Orientation.Vertical) {
      val gameOverScreenButtons = new BoxPanel(Orientation.Horizontal) {
        contents += playAgainBtn
        contents += exitGameBtn
      }
      val gameOverLabelCont = new BoxPanel(Orientation.Horizontal) { 
        contents += gameOverLabel
      }

      contents += gameOverLabelCont
      contents += gameOverScreenButtons
    }


    val gameWonLabel = new Label("<html><br><br><br><br><h1>You won!</h1><br><br></html>")
    val gameWonLabelInfo = new Label("")
    def updateGameWonLabel() = {
      gameWonLabelInfo.text = ("<html><h3>You reached level " + game.currentLevel + ", with " + game.player.health + " health left!</h3><br><br></html>")
    }

    val gameWonScreen = new BoxPanel(Orientation.Vertical) {
      val gameWonScreenButtons = new BoxPanel(Orientation.Horizontal) {
        contents += playAgainBtn
        contents += exitGameBtn
      }
      val gameWonLabelCont = new BoxPanel(Orientation.Horizontal) { 
        contents += gameWonLabel
      }

      val gameWonLabelInfoCont = new BoxPanel(Orientation.Horizontal) {
        contents += gameWonLabelInfo
      }
      contents += gameWonLabelCont
      contents += gameWonLabelInfoCont
      contents += gameWonScreenButtons
    }

    val container: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += startScreen
    }

    def startGame() = {
      game.startGame()

      container.contents -= startScreen
      container.contents += gameScreen

      container.revalidate()
      container.repaint()

      top.repaint()
      
      gameLoop.start()
    }
    
    def loadGame() = {
      val loadedSuccesfully = game.loadGame()
      if (loadedSuccesfully) {
        startGame()
      } else {
        newNotification("There was an error while loading the game. You have to either fix the save file manually or start a new game.")
      }
    }

    def playAgain() = {
      game = new Game()
      container.contents -= gameWonScreen
      container.contents -= gameOverScreen
      startGame()
    }

    // Listen to buttons
    this.listenTo(startGameBtn)
    this.listenTo(loadGameBtn)
    this.listenTo(exitGameBtn)
    this.listenTo(playAgainBtn)
    this.listenTo(tower1Btn)
    this.listenTo(tower2Btn)
    this.listenTo(tower3Btn)
    this.listenTo(tower4Btn)
    this.listenTo(tower5Btn)
    this.listenTo(specialBtn)
    this.listenTo(changeSpeedBtn)
    
    tower1Btn.icon = new ImageIcon(Paths.get(".").toAbsolutePath + "/tower1.png")

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
            loadGame()
          }
          case "Play Again" => {
            playAgain()
          }
          case "Tower 1" => {
            game.selectedTower(1)
          }
          case "Tower 2" => {
            game.selectedTower(2)
          }
          case "Tower 3" => {
            game.selectedTower(3)
          }
          case "Tower 4" => {
            game.selectedTower(4)
          }
          case "Tower 5" => {
            game.selectedTower(5)
          }
          case "Special" => {
            game.selectedSpecial()
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

        gameScreen.revalidate()
        gameScreen.repaint()

        newNotification(game.notification)
        updateLabels()
        
        game.onTick()
        
        if (game.isGameWon) {
          (e.getSource.asInstanceOf[javax.swing.Timer]).stop()

          updateGameWonLabel()

          container.contents -= gameScreen
          container.contents += gameWonScreen

          container.revalidate()
          container.repaint()

          top.repaint()
        } else if (game.isGameOver) {
          (e.getSource.asInstanceOf[javax.swing.Timer]).stop()
          // Render game over screen
          updateGameOverLabel()

          container.contents -= gameScreen
          container.contents += gameOverScreen

          container.revalidate()
          container.repaint()

          top.repaint()
        }
      }
    }

    // This calls the gameLoopEventListener every 16ms
    // Which allows animation and dynamic screens
    var gameLoop = new javax.swing.Timer(16, gameLoopEventListener)
  }
}

