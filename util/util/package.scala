import tools.nsc.doc.model.{AbstractType, MemberEntity, TemplateEntity, Entity}
import tools.nsc.io.{File, Directory}
import tools.nsc.{Global, doc, CompilerCommand}
import doc.model.comment.CommentFactory

/**
 * Created by IntelliJ IDEA.
 * User: Pedro
 * Date: 11/07/11
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */

package object util {
  import scala.tools.nsc.io._
/* Given a directory recursively returns the list of files ending with `.scala`.
	 *  Directories named `.git` or `.svn` are ignored. */
	def scalaFiles(directory:Directory) =
	  directory.walkFilter( x => x.name != ".git" && x.name != ".svn")
	  .filter(f => f.isFile && f.name.endsWith(".scala")) map(_.toFile) toList

	def scalaFiles(dir:String):List[File] = scalaFiles(Path(dir).toDirectory)

	def docUniverse(files:List[File]) = new DocCompiler(files).docUniverse

  def document(files:List[File]) = {
    val docCompiler = new DocCompiler(files) {
      override val command = new CompilerCommand(
        "-d" :: """C:\dev\langs\scala\projects\tmp\scaladoc2\doc-testing""" :: (files.map {_.path}), docSettings)
    }
    docCompiler.document
  }
	//def scriptRun(args:String*) = MainGenericRunner.main(args.toArray[String])

	lazy val modelFactory = {
    val settings = new doc.Settings((str: String) => {})
    val reporter = new scala.tools.nsc.reporters.ConsoleReporter(settings)
    val g = new Global(settings, reporter)
    (new SimpleModelFactory(g, settings) with CommentFactory with doc.model.TreeFactory)
  }

	/*def runTests(testRoot:String) { // not working, it's using scala.home classpath
		System.setProperty("scala.home","C:/dev/langs/scala/scala-2.8.0")
		scalaFiles(testRoot).foreach { f =>
			try {
				val file = f.toAbsolute.toString
				println("Running "+file) // "-howtorun:object"
				scriptRun("-nocompdaemon","-i",file,"-e","Test.main(Array.empty)")
				//scriptRun("-nocompdaemon","-i",file,"-e","Test.main(Array.empty)")
			} catch {
				case x : Error => println(x)
			}
		}
	}*/

def nature2string(e : Entity) =
		e match {
			case t : TemplateEntity if(t.isTrait) => "trait "
			case t : TemplateEntity if(t.isObject) => "object "
			case t : TemplateEntity if(t.isPackage) => "package "
			case t : TemplateEntity if(t.isClass) => "class "
			case e : MemberEntity if(e.isDef) => "def "
			case e : MemberEntity if(e.isVal) => "val "
			case e : MemberEntity if(e.isLazyVal) => "lazy val "
			case e : MemberEntity if(e.isVar) => "var "
			case e : MemberEntity if(e.isAliasType) => "type "
			case a : AbstractType => "type "
			case u @ _ =>
				println("unknown "+ u+ " " +u.getClass); ""
		}
 def definitionName(e:Entity) = nature2string(e) + " " + e.name
 def qualifiedDefinitionName(e:Entity) = nature2string(e) + " " + e.qualifiedName
}