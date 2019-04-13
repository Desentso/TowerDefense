package game

import scala.io.Source
import java.nio.file.Paths
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import play.api.libs.json._

class FileHandler(val game: Game) {
  
  def saveGame() = {
    println("SAVE GAME")
    
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
    println(saveFileAsJSON)

    val outputFile = new File(Paths.get(".").toAbsolutePath + "/save_file_1.json")
    val writer = new BufferedWriter(new FileWriter(outputFile))
    writer.write(saveFileAsJSON)
    writer.newLine()
    writer.flush()
    writer.close()
  }
  
  def loadGame(): JsValue = {
    println("Load game")
    val saveFile = scala.io.Source.fromFile(Paths.get(".").toAbsolutePath + "/save_file_1.json")
    val saveJSONString = try saveFile.getLines.mkString("\n") finally saveFile.close()

    val saveFileAsJSON: JsValue = Json.parse(saveJSONString)

    println(saveJSONString)

    saveFileAsJSON
  }

  def loadConfiguration() = {
    val confFile = scala.io.Source.fromFile(Paths.get(".").toAbsolutePath + "/conf.json")

    val confJSONString = try confFile.getLines.mkString("\n") finally confFile.close()

    val confFileAsJSON: JsValue = Json.parse(confJSONString)

    println(confJSONString)

    confFileAsJSON

  }

}
