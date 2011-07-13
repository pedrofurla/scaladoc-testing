import java.io.{ByteArrayInputStream}
import tools.nsc.doc.Universe

/**
 * Created by IntelliJ IDEA.
 * User: Pedro
 * Date: 11/07/11
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */

object DotView {
	import util._
  import tools.nsc.doc.model._
  import tools.nsc.doc.model.{ Object => SDObject}

  val fqnExclusions = "scala.Any" :: "scala.AnyRef" :: "scala.AnyVal" :: Nil

  implicit def memberOps(m:MemberEntity) = new {
    def isLocal = m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == m.inTemplate
  }
  implicit def docTemplOps(dte:DocTemplateEntity) = new {
    def parents:List[TemplateEntity] = dte.parentType.map { _.refEntity.map { x => x._2._1 } toList } getOrElse List.empty
  }

  def filter(e:Entity):Boolean = ! fqnExclusions.contains(e.qualifiedName)

  def makeDot(universe: Universe)(filter:Entity => Boolean): String = {

    def directedNode(from:Entity, to:Entity):String = from.name + " -> " + to.name + ";"
    def directedNodes(from:Entity, tos:List[Entity]):String =
      if (!tos.isEmpty)
        //tos.foldLeft(from.name){ (acc:String,e:Entity) => acc + "->" + e.name  } + ";"
        tos map { directedNode(from, _) } mkString("\n")
      else ""

    def gather(owner: DocTemplateEntity): String =
        (for(m <- owner.members if m.isLocal && filter(m)) yield {

          m match {
            case tpl: Package =>
              gather(tpl)
            case tpl: Class =>
              println(qualifiedDefinitionName(tpl))
              tpl.parents filter (filter) foreach { x=>println("\t"+qualifiedDefinitionName(x)) }
              directedNodes(tpl,tpl.parents filter (filter)) + gather(tpl)
            case tpl: Trait =>
              println(qualifiedDefinitionName(tpl))
              tpl.parents filter (filter) foreach { x=>println("\t"+qualifiedDefinitionName(x)) }
              directedNodes(tpl,tpl.parents filter (filter)) + gather(tpl)
            case tpl: SDObject =>
              println(qualifiedDefinitionName(tpl))
              tpl.parents filter (filter) foreach { x=>println("\t"+qualifiedDefinitionName(x)) }
              directedNodes(tpl,tpl.parents filter (filter)) + gather(tpl)
            case x @ _ => ""
          } }) filter {_ != ""} mkString("\n")

    gather(universe.rootPackage)
  }

  def digraph(name:String,contents:String)=
    "digraph "+name+""" {
      node [shape=box];
      rankdir=BT;
    """ + "\n" +
    contents + "\n"+
    "overlap=false" +
    "}"


  def main(args:Array[String]):Unit = {
    import scala.tools.nsc.io._

    val projectHome = "C:\\dev\\langs\\scala\\projects\\scaladoc-testing"
    val files = scalaFiles("C:/dev/langs/scala/projects/scala-doc-prj-svn/src") filter {_.name == "Entity.scala"}
    //println(files)
		val ret = makeDot(docUniverse(files))(filter _)
   // println(ret)
    val graph = digraph("ScaladocModel", ret);
    File(projectHome + "\\out.dot").writeAll(graph)

    import scala.sys.process._
    val p = Process("G:\\Program Files (x86)\\Graphviz2.26.3\\bin\\dot","-Tpng" :: Nil)
    //val o:String => Unit = println(_:String)
    //val io = BasicIO(true,o, Some(ProcessLogger(o)))

    val is = new ByteArrayInputStream(graph.getBytes("UTF-8"))
    val pro = (p #< is #> new JFile(projectHome + "\\out.png")).run
    println(pro.exitValue)
    //p.run(io)
	}
}