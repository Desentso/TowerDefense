# Tower Defense Game
Tower defense game built with Scala. It's a "normal" tower defense game where group of enemies try to run to the end of path. Your goal is to eliminate the enemies before they reach the end of path. You win after reaching level 50. You lose if your health points drop to zero, you lose one health point each time enemy reaches to the end of the path.

The game has simple 2D graphics. It also has save files and configuration files. So you can exit the game and continue where you left of. And with configuration file you can change bunch of settings, such as how the map looks, how much damage towers do, how much coins you get etc.

# Requirements
- sbt 1.x+ [Download sbt](https://www.scala-sbt.org/download.html)

# How to run
    > sbt
    > run

# How to run tests
    > sbt
    > test