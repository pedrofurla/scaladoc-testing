package util

import scala.tools.nsc.io._
import scala.tools.nsc.Global
import scala.tools.nsc.doc
import scala.tools.nsc.doc.model.comment._

object DocCompiler {
  import scala.tools.nsc.util.FakePos
  import scala.tools.nsc._
  import scala.tools.nsc.reporters.{ConsoleReporter}
                                                           
  def error(msg: String): Unit = {
    reporter.error(FakePos("scalac"), msg + "\n  scalac -help  gives more information")
  }

  val docSettings: doc.Settings = new doc.Settings(error)

  val reporter: ConsoleReporter = new ConsoleReporter(docSettings) {
    override def hasErrors = false // need to do this so that the Global instance doesn't trash all the symbols just because there was an error
  }


  lazy val docFactory = new doc.DocFactory(reporter, docSettings)

  def document(files:List[File]) = {
    docFactory.document(files map { _.path })
  }

  def universe(files:List[File]) = {
    val command = new CompilerCommand(files map {_.path}, docSettings)
    docFactory.universe(command.files).get
  }

}

class ModelFactoryMock(val g: Global, val s: doc.Settings) extends doc.model.ModelFactory(g, s) {
  thisFactory: ModelFactoryMock with CommentFactory with doc.model.TreeFactory =>

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

object Util {
	/** Given a directory recursivelly returns the list of files ending with `.scala`. 
	 *  Directories named `.git` or `.svn` are ignored. */
	def scalaFiles(directory:Directory) = 
	  directory.walkFilter( x => x.name != ".git" && x.name != ".svn")
	  .filter(f => f.isFile && f.name.endsWith(".scala")) map(_.toFile) toList
	
	def scalaFiles(dir:String):List[File] = scalaFiles(Path(dir).toDirectory)  

  def docUniverse(files:List[File]) = DocCompiler.universe(files)

  def document(files:List[File]) = DocCompiler.document(files)
  
  lazy val modelFactory = {
    val settings = new doc.Settings((str: String) => {})
    val reporter = new scala.tools.nsc.reporters.ConsoleReporter(settings)
    val g = new Global(settings, reporter)
    (new ModelFactoryMock(g, settings) with CommentFactory with doc.model.TreeFactory)
  }

	def scriptRun(args:String*) = MainGenericRunner.main(args.toArray[String])

	def runTests(testRoot:String) { // not working, it's using scala.home classpath
		System.setProperty("scala.home","C:/dev/langs/scala/scala-2.8.0")
		scalaFiles(testRoot).foreach { f =>
			try {
				val file = f.toAbsolute.toString
				println("Running "+file) // "-howtorun:object"
				scriptRun("-nocompdaemon","-i",file,"-e","Test.main(Array.empty)")
			} catch {
				case x : Error => println(x)
			}
		}
	}
}

object Run {
	def main(args:Array[String]) = {
		val factory = Util.modelFactory;
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