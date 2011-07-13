package helper

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._
import scala.tools.nsc.doc.model._

class MockModelFactory(global: scala.tools.nsc.Global, settings: Settings) extends ModelFactory(global,settings) { 
	thisFactory: MockModelFactory with CommentFactory =>
	def parsePublic(comment: String, pos: Position): Comment = thisFactory.parse(comment,pos)
}

abstract class CommentTest extends DocFactoryMock {
	
	override val files =  
		"test/testing/TestDoc.scala" ::
		"test/testing/ParentDoc.scala" ::
		Nil
	
	override val outputLocation = "../tmp/scaladoc2/doc-testing"
		
	var factory : MockModelFactory with CommentFactory 	= _
	val eot = '\u0003'
	val samples:List[String]
	
	val page = new html.HtmlPage {
		def path = Nil
		def title = ""
		def headers = xml.NodeSeq.Empty
		def body = xml.NodeSeq.Empty
	}		
	
	def runSample(text:String):Comment = {
		println("Sample: "+text.replaceAll("\n","\\\\n"));
		factory.parsePublic(text, null)
	}
	def runSamples = {		
		for(text <- samples) {			
			println("["+runSample(text)+"]")
		}
	}
	
	override def testWith(modelFactory:model.ModelFactory, universe:Universe):Unit = {
		factory = new MockModelFactory(compiler, settings) with CommentFactory
		//runSamples
		//println(modelFactory.templatesCache) 
		exit(0)
	}
	
}

	
	


