package game

import scala.io.Source
import java.nio.file.Paths
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import play.api.libs.json._
import scala.util.control.NonFatal

class FileHandler(val game: Game) {
  
  def saveGame() = {
    try {
      println("SAVE GAME")
      
      // Construct the save file json object
      val saveJson: JsValue = Json.obj(
        "level" -> JsNumber(game.levelHandler.currentLevel),
        "coins" -> JsNumber(game.player.coins),
        "healthLeft" -> JsNumber(game.player.health),
        "towers" -> game.towers.map(tower => 
          Json.obj(
            "type" -> tower.towerType,
            "damage" -> tower.damage,
            "range" -> tower.range,
            "position" -> Json.obj("x" -> tower.position.x, "y" -> tower.position.y)
          )
        )
      )
      
      val saveFileAsJSON = Json.prettyPrint(saveJson)

      println("SAVE FILE START")
      println(saveFileAsJSON)
      println("END OF SAVE FILE")

      // Save the constructed json to save_file_1.json at project root
      val outputFile = new File(Paths.get(".").toAbsolutePath + "/save_file_1.json")
      val writer = new BufferedWriter(new FileWriter(outputFile))
      writer.write(saveFileAsJSON)
      writer.newLine()
      writer.flush()
      writer.close()

    } catch {
      case NonFatal(e) => {
        println("Error while writing save file: ", e)
      }
    }
  }
  
  def loadGame(): JsValue = {
    try {
      println("Load game")
      val saveFile = scala.io.Source.fromFile(Paths.get(".").toAbsolutePath + "/save_file_1.json")
      val saveJSONString = try saveFile.getLines.mkString("\n") finally saveFile.close()

      val saveFileAsJSON: JsValue = Json.parse(saveJSONString)

      println(saveJSONString)

      saveFileAsJSON
    } catch {
      case NonFatal(e) => {
        println("Error while reading save file: ", e)
        Json.obj()
      }
    }
  }

  def loadConfiguration() = {
    try {
      val confFile = scala.io.Source.fromFile(Paths.get(".").toAbsolutePath + "/conf.json")

      val confJSONString = try confFile.getLines.mkString("\n") finally confFile.close()

      val confFileAsJSON: JsValue = Json.parse(confJSONString)

      println(confJSONString)

      confFileAsJSON
    } catch {
      case NonFatal(e) => {
        println("Error while reading configuration file: ", e)
        Json.obj()
      }
    }
  }

}
