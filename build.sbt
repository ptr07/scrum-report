name := "scrum-report"
version := "1.0"
scalaVersion := "2.12.1"
assemblyJarName in assembly := "scrum-report.jar"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

//scapegoatVersion := "1.1.0"
enablePlugins(CopyPasteDetector)

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8") sys.error("Java 8 is required for this project.")
}

unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/resources"
}
unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src" / "main" / "scala"
}

libraryDependencies += "commons-io" % "commons-io" % "2.5"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.8.6"
libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.8.6"
libraryDependencies += "com.jsuereth" %% "scala-arm" % "2.0"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
libraryDependencies += "org.freemarker" % "freemarker" % "2.3.14"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.apache.poi" % "poi" % "3.15"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.15"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.2"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"
libraryDependencies += "com.github.melrief" %% "pureconfig" % "0.5.1"


//import sbtassembly.AssemblyPlugin.defaultShellScript
//assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

