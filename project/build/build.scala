import sbt._

class Build(info: ProjectInfo) extends DefaultProject(info) {   
  //override def compileOptions = super.compileOptions ++ Seq(ExplainTypes)
  
  object sources {
	  val index = path("index") ##
	  val testSamples = path("test") ##	  
	  //val run = path("run")
  }

  override def outputDirectoryName = "bin"
  
  override def mainScalaSourcePath = "index"
	  
  override def mainSources : PathFinder = {
	  println("invoked")
	  ("index" ** "*.scala") +++ ("test" ** "*.scala") 
  }
  
  val dep = "org.apache.ant" % "ant" % "1.8.0"
  //val dep2 = "org.scala-lang" % "scala-compiler" % "2.8.0-SNAP"
  override def filterScalaJars = false
  
  //val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  
  val mavenLocal = "Local Maven Repository" at "file://"+"C:/java/maven/repo"

  
}