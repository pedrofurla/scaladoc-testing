import sbt._

class MyBuild extends Build {

  override def shouldCheckOutputDirectories = false

  override def mainSources : PathFinder = "bleh" ** "*.scala"



  lazy val root = Project("root", file(".")) settings(
    libraryDependencies += "junit" % "junit" % "4.8" % "test"
    mainClass  in (Compile, run) := Some("view.JettyRunner")
  )

  /*
  val mavenLocal = "Local Maven Repository" at "file://C:/java/maven/repo"

  val dep = "org.apache.ant" % "ant" % "1.8.0"

lazy val main = project(".","index", new ScalaIndexProject(_))
  lazy val plugin = project(".", "index-sbt", new IndexPluginProject(_))


  class ScalaIndexProject(info:ProjectInfo) extends DefaultProject(info) {
	  override def outputDirectoryName = "bin"
	  
	  override def mainScalaSourcePath = "index"
		  
	  override def mainSources : PathFinder = 
		  ("index" ** "*.scala") //+++ ("test" ** "*.scala") +++ ("src" ** "*.scala")
	  
	  //val dep = "org.apache.ant" % "ant" % "1.8.0"	  
	  override def filterScalaJars = false
  }*/
  
  /*class IndexPluginProject(info: ProjectInfo) extends PluginProject(info) {
	 override def mainSources : PathFinder = 
		 ("sbt-plugin" ** "*.scala") +++ ("index" ** "*.scala")
	 override def outputDirectoryName = "bin-plugin"	  
	 override def filterScalaJars = false
	 //override def buildScalaVersion = "2.8.0-snapshot"
  }*/
  
}