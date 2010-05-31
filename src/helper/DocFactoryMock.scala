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

object DocFactoryMock {
	def error(msg: String): Unit = {
		reporter.error(FakePos("scalac"), msg + "\n  scalac -help  gives more information")
	}
	def settings: doc.Settings = new doc.Settings(error)  
	def reporter: ConsoleReporter = new ConsoleReporter(settings)
}

abstract class DocFactoryMock extends doc.DocFactory(DocFactoryMock.reporter, DocFactoryMock.settings) {
	
	
	val outputLocation:String
	val files:List[String]
	
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
		println(docModel.findMember("."))
		println(docModel.findMember(".."))
		
		// expected Some(_)
		println(docModel.findMember(""))
		println(docModel.findMember("testing"))      
		println(docModel.findMember("testing.ParentDoc"))*/      
			
	}

	def run: Unit = {
		val fakeArgs = ( "-d" :: outputLocation :: files ).toArray
		val command = new CompilerCommand(fakeArgs.toList, settings)
		/*val fakeArgs = Array(
			"-d", "scalaDoc2Root/../tmp/scaladoc2/doc-testing", 
			"scalaDoc2Root/test/testing/TestDoc.scala", 
			"scalaDoc2Root/test/testing/ParentDoc.scala"		
		) */
		if (!reporter.hasErrors) { // No need to continue if reading the command generated errors        
			document(command.files)        
		}   
	}
	
	def testWith(modelFactory:model.ModelFactory):Unit = {}
	def testWith(modelFactory:model.ModelFactory, universe:Universe):Unit = {}
	def testWith(modelFactory:model.ModelFactory, universe:Universe, htmlFactory:html.HtmlFactory):Unit = {}
	
	override def document(files: List[String]): Unit = {
		(new compiler.Run()) compile files
		compiler.addSourceless
		if (!reporter.hasErrors) {
			modelFactory = (new model.ModelFactory(compiler, settings) with CommentFactory)
			
			testWith(modelFactory);
			
			universe = modelFactory.makeModel      
			
			testWith(modelFactory,universe);
			
			htmlFactory = (new html.HtmlFactory(universe))      
			
			testWith(modelFactory,universe,htmlFactory)
			
			println("model contains " + modelFactory.templatesCount + " documentable templates")      
		}
	}
	
	def main(args:Array[String]) = {
		run		
	}
	
}