package helper

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

abstract class Wiki extends DocFactoryMock {
	//val settings = new scala.tools.nsc.Settings();
	//val reporter = new ConsoleReporter(settings);
	
	override val files = 
		"scalaDoc2Root/../tmp/scaladoc2/doc-testing" :: 
		"scalaDoc2Root/test/testing/TestDoc.scala" ::
		"scalaDoc2Root/test/testing/ParentDoc.scala" ::
		Nil
	
	override val outputLocation = "scalaDoc2Root/../tmp/scaladoc2/doc-testing"
		
	val factory= new CommentFactory(reporter, null)
	val eot = factory.endOfText
	
	val page = new html.HtmlPage {
		def path = Nil
		def title = ""
		def headers = xml.NodeSeq.Empty
		def body = xml.NodeSeq.Empty
	}		
	
	def runSample(text:String):Body = {
		println(text.replaceAll("\n","\\\\n"));
		factory.parseWiki(text, null)
	}
	def runSamples = {		
		for(text <- samples) {			
			println(runSample(text))
		}
	}
	
	def main(args:Array[String]) = {
		runSamples		
	}
		
	val samples:List[String] 

 
}

	
	


