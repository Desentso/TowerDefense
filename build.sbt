name := "TowerDefense"

version := "0.1"

mainClass in (Compile,run) := Some("GUI")

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0"