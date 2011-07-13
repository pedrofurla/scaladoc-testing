package util

import scala.tools.nsc.io._

import scala.tools.nsc.doc.model.comment._
import tools.nsc.{CompilerCommand, Global, doc}

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
	  docProcessor.makeUniverse(command.files).get
  }

  def document = {
    val docProcessor = new doc.DocFactory(reporter, docSettings)
	  docProcessor.document(command.files)
  }
      
}

class SimpleModelFactory(val g: Global, val s: doc.Settings) extends doc.model.ModelFactory(g, s) {
  thisFactory: SimpleModelFactory with CommentFactory with doc.model.TreeFactory =>

  def strip(c: Comment): Option[Inline] = {
    c.body match {
      case Body(List(Paragraph(Chain(List(Summary(inner)))))) => Some(inner)
      case _ => None
    }
  }

  def parseRaw(s:String) = parse(s, "", scala.tools.nsc.util.NoPosition)
  def parseComment(s: String): Option[Inline] =
    strip(parse(s, "", scala.tools.nsc.util.NoPosition))
}

object Run {
	def main(args:Array[String]) = {
		val factory = modelFactory;
		val doc = """
				|/**
				| * blabla
				| *
				| * @version bla
				| * @author Fulano
				| * @author Bleh
				| */
				""".stripMargin
		val doc2 = "/** @author Fulano */"
		println(factory.parseRaw(doc))
		println(factory.parseRaw(doc).authors)
	}
}