name := "TowerDefense"

version := "0.1"

mainClass in (Compile,run) := Some("GUI")

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0"

libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
