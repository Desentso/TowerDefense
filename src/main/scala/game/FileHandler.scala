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
    
    val saveJsonString = Json.stringify(saveJson)
    println("PATH:", Paths.get(".").toAbsolutePath)
    val outputFile = new File(Paths.get(".").toAbsolutePath + "/save_files/save_file_1.json")
    val writer = new BufferedWriter(new FileWriter(outputFile))
    writer.write(saveJsonString)
    writer.newLine()
    writer.flush()
    writer.close()
    
    println(saveJsonString)
  }
  
  def loadGame() = {
    println("Load game")
    val json: JsValue = Json.parse("""
      {
        "level": 5,
        "coins": 550,
        "healthLeft": 240,
        "towers": [
          {
            "type": 2,
            "damage": 8,
            "range": 175,
            "position": {"x": 150, "y": 250}
          },
          {
            "type": 1,
            "damage": 10,
            "range": 150,
            "position": {"x": 350, "y": 260}
          }
        ]
      }
      """
    )
    
    val level = (json \ "level").as[Int]
    
    val jsonString = Json.stringify(json)
  }
  
}
