package helper

import java.io.File

import scala.tools.nsc.reporters.{Reporter, ConsoleReporter}
import scala.tools.nsc.util.FakePos //{Position}
import scala.tools.nsc._

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._
import scala.xml.{PrettyPrinter => PP}

object Doc {
	val versionMsg: String =
    "Scaladoc " +
    Properties.versionString + " -- " +
    Properties.copyrightString
    
  
  def error(msg: String): Unit = {
    reporter.error(FakePos("scalac"), msg + "\n  scalac -help  gives more information")
  }
  
  val docSettings: doc.Settings = new doc.Settings(error)  
  var reporter: ConsoleReporter = new ConsoleReporter(docSettings) 
  
  def process(args: Array[String]): Unit = {    
    
    val command = new CompilerCommand(args.toList, docSettings, error, false)
      
    if (!reporter.hasErrors) { // No need to continue if reading the command generated errors        
        val docProcessor = new MyDocFactory(reporter, docSettings)
        docProcessor.document(command.files)        
    }      
    reporter.printSummary()   
    
  }

  def main(args: Array[String]): Unit = {
	val fakeArgs = Array(
		"-d", "scalaDoc2Root/../tmp/scaladoc2/doc-testing", 
		"scalaDoc2Root/test/testing/TestDoc.scala", 
		"scalaDoc2Root/test/testing/ParentDoc.scala"		
	) map (_.replaceAll("scalaDoc2Root",args(0)))
    process(fakeArgs) 
    exit(if (reporter.hasErrors) 1 else 0)
  }
}

class MyDocFactory(reporter: Reporter, settings: doc.Settings) extends doc.DocFactory(reporter, settings) {
	import doc._
	var modelFactory:model.ModelFactory = _
	var htmlFactory:html.HtmlFactory = _
	var universe:Universe = _

	def testFindMember = {
	  // expected NONE
     /* println(docModel.findMember("java.lang.String"))
      println(docModel.findMember("java.lang.Integer"))
      println(docModel.findMember("blah"))
      println(docModel.findMember("testing.Blah.ParentDoc"))
      println(docModel.findMember(".ParentDoc"))
      
      // expected Some(_)
      println(docModel.findMember(""))
      println(docModel.findMember("testing"))      
      println(docModel.findMember("testing.ParentDoc"))*/      
      
	}
	
  override def document(files: List[String]): Unit = {
    (new compiler.Run()) compile files
    compiler.addSourceless
    if (!reporter.hasErrors) {
      modelFactory = (new model.ModelFactory(compiler, settings))      
      universe = modelFactory.makeModel
      
      testFindMember
      
      htmlFactory = (new html.HtmlFactory(reporter, settings))
      println("model contains " + modelFactory.templatesCount + " documentable templates")
      //htmlFactory generate docModel
    }
  }
  
}