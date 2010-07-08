import sbt._
trait ScalaIndexPlugin extends ScalaProject {
  lazy val hello = task { log.info("Hello World!"); None }
  
  override def scaladocTask(label: String, sources: PathFinder, 
		  outputDirectory: Path, classpath: PathFinder, options: ScaladocOption*): Task = {
	println("doc-ing")
	super.scaladocTask(label, sources, outputDirectory, classpath, options)		
  }
  override def scaladocTask(label: String, sources: PathFinder, outputDirectory: Path, classpath: PathFinder, options: => Seq[ScaladocOption]): Task = {
	println("doc-ing2")
	super.scaladocTask(label,sources,outputDirectory,classpath,options)
  }
}