package helper

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

object Wiki {
	val settings = new scala.tools.nsc.Settings();
	val reporter = new ConsoleReporter(settings);
	val factory= new CommentFactory(reporter)
	val eot = factory.endOfText
	val page = new html.HtmlPage {
		def path = Nil
		def title = ""
		def headers = xml.NodeSeq.Empty
		def body = xml.NodeSeq.Empty
	}
	lazy val texts = "" :: 
		//sampleStuff :::	Nil
		sampleBullets ::: Nil
		//sampleLinks :::
		Nil
	 
	
	def main(args:Array[String]) = {
		//val text = sampleBullets.head
		for(text <- sampleBullets) {
			println(text.replaceAll("\n","\\\\n"));
			val parsed = factory.parseWiki(text, null)		
			println(parsed)
			//val nodes = page.bodyToHtml(parsed)
			
			//println(nodes.toString.replace("</li><li><ul>","  </li><li><ul>  "))
			println(mkString0(parsed))
		}
	}
	
	def mkString0(b:Body):String = {
		val Body(body) = b
		val r=(for(block <- body) yield mkString(block,"","")).foldLeft("")(_+_)
		r.replaceAll("\\t", "   ")
	} 
	
	def mkString(b:Block,pre:String, tpe: String):String = {
		b match {
			case Paragraph(t) => pre + tpe+ " " +t + "\n"
			case OrderedList(list,s) => {
				(for(block <- list) yield mkString(block,pre+"\t",s+" ")).foldLeft("")(_+_) 
			}
			case UnorderedList(list) => {
				(for(block <- list) yield mkString(block,pre+"\t",">")).foldLeft("")(_+_) 
			}
			case x@_ => x.toString
		}
	}
	
	def runSamples = {		
		for(text <- texts) {
			println(text.replaceAll("\n","\\\\n"));
			val parsed = factory.parseWiki(text, null)				
			//println(parse)
			println(mkString0(parsed))
		}
	}	
	
	val sampleLinks=
		"[[]]" ::
		"[[ ]]" ::
		"[[http://... Titulo]]" ::
		"[[scala.String]]" ::
		"[[http://www.google.com some where]]" ::
		Nil
	
	val sampleBullets = {
	val c1 = 
""" 
| - L1
| - L2
|    - L2.1""".stripMargin
	val c2 = c1 + 
"""  
| - L3
|   - L3.1
|     - L3.1.1""".stripMargin
	val c10 = 
""" - L1
| - L2
|    - L2.1
|      - L2.1.1
|    - L2.2
|      - L2.2.1
|      - L2.2.2
| - L3
|   - L3.1
|     - L3.1.1
| - L4""".stripMargin
	val c11 = 
""" - L1
| - L2
|    i. L2.1
|      - L2.1.1
|    i. L2.2
|      a. L2.2.1
|      a. L2.2.2
| - L3
|   i. L3.1
|     i. L3.1.1
| - L4""".stripMargin
val c12 = 
""" - L1
| i. L2
|    - L2.1""".stripMargin
val wrongMixing = 
""" - L1
| i. L2
|
|    - L2.1""".stripMargin
		
		wrongMixing :: //c11 :: c1 :: c2 :: c10 ::
		//" * LIST1 " :: 
		//" * LIST1 \n * LIST2 " ::
		//" * LIST1 \n * LIST2 \n * LIST3 " ::
		//c1 :: c2 :: err1 ::
		Nil
	}

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

	
	


