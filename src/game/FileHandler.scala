package game

import scala.io.Source
import java.nio.file.Paths
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import play.api.libs.json._

class FileHandler(val game: Game) {
  
  def saveGame() = {
    println("Save game")
    val json: JsValue = Json.parse("""
      {
        "name" : "Watership Down",
        "location" : {
          "lat" : 51.235685,
          "long" : -1.309197
        },
        "residents" : [ {
          "name" : "Fiver",
          "age" : 4,
          "role" : null
        }, {
          "name" : "Bigwig",
          "age" : 6,
          "role" : "Owsla"
        } ]
      }
      """)
    val jsonString = Json.stringify(json)
    
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
    println("Path:", Paths.get(".").toAbsolutePath)
    val outputFile = new File(Paths.get(".").toAbsolutePath + "/save_files/save_file_1.json")
    val writer = new BufferedWriter(new FileWriter(outputFile))
    writer.write(saveJsonString)
    writer.newLine()
    writer.flush()
    writer.close()
    
    println(saveJsonString)
  }
  
  def loadGame() = {
    
  }
  
}
