package helper

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

abstract class Wiki extends DocFactoryMock {
	//val settings = new scala.tools.nsc.Settings();
	//val reporter = new ConsoleReporter(settings);
	
	override val files =  
		"test/testing/TestDoc.scala" ::
		"test/testing/ParentDoc.scala" ::
		Nil
	
	override val outputLocation = "../tmp/scaladoc2/doc-testing"
		
	var factory:CommentFactory = _ //= new CommentFactory(reporter, null)
	val eot = '\u0003'
	val samples:List[String]
	
	val page = new html.HtmlPage {
		def path = Nil
		def title = ""
		def headers = xml.NodeSeq.Empty
		def body = xml.NodeSeq.Empty
	}		
	
	def runSample(text:String):Body = {
		println("Sample: "+text.replaceAll("\n","\\\\n"));
		factory.parseWiki(text, null)
	}
	def runSamples = {		
		for(text <- samples) {			
			println(runSample(text))
		}
	}
	
	override def testWith(modelFactory:model.ModelFactory, universe:Universe):Unit = {
		factory = new CommentFactory(reporter, modelFactory)
		runSamples
		//println(modelFactory.templatesCache) 
		exit(0)
	}
	
}

	
	


