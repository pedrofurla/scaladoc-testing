package helper

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

abstract class Wiki {
	val settings = new scala.tools.nsc.Settings();
	val reporter = new ConsoleReporter(settings);
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
		
	lazy val samples:List[String] = Nil	
	
	val sampleStuff = 
		"===AAAA===" ::
		"----" ::
		"[[AAAA]]" :: 
		" * AAAA " ::
		" {{{ code }}} " ::
		"{{{ code2 }}}" :: 			
		" blablabla bla bla \n blabla bla\n {{{ code }}} = lala ="+eot ::
		" blablabla bla bla \n blabla bla\n{{{ code }}} \n= lala ="+eot ::
		Nil
 
}

	
	


