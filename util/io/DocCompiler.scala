package io

import scala.tools.nsc.io._

class DocCompiler(val files:List[File]) {
  import scala.tools.nsc.reporters.{Reporter, ConsoleReporter}
  import scala.tools.nsc.util.FakePos
  import scala.tools.nsc._
  
  var reporter: ConsoleReporter = _
  
  def error(msg: String): Unit = {
    reporter.error(FakePos("scalac"), msg + "\n  scalac -help  gives more information")
  }
  
  val docSettings: doc.Settings = new doc.Settings(error)
    
  val command = new CompilerCommand(files map {_.path}, docSettings)
  
  reporter = new ConsoleReporter(docSettings) {
	override def hasErrors = false // need to do this so that the Global instance doesn't trash all the symbols just because there was an error
  }
    
  def docUniverse = {
	  val docProcessor = new doc.DocFactory(reporter, docSettings)
	  docProcessor.universe(command.files).get
  }
      
}
object util {
	/** Given a directory recursivelly returns the list of files ending with `.scala`. 
	 *  Directories named `.git` or `.svn` are ignored. */
	def scalaFiles(directory:Directory) = 
	  directory.walkFilter( x => x.name != ".git" && x.name != ".svn")
	  .filter(f => f.isFile && f.name.endsWith(".scala")) map(_.toFile) toList
	  
	def docUniverse(files:List[File]) = new DocCompiler(files).docUniverse 
}
object Run {
	def main(args:Array[String]) = {
		
	}
}