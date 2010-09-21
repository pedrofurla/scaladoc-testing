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

class Sources(val directory:Directory) {		
	lazy val scalaFiles = 
	  directory.walkFilter( _.name != ".git" )
	  .filter(f => f.isFile && f.name.endsWith(".scala")) map(_.toFile) toList 
}

object Run {
	def main(args:Array[String]) = {
		
	}
}