/* NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */

package scala.tools.nsc
package doc

import reporters.Reporter

/** Copied from scala.tools.nsc.doc.DocFactory
  * TODO Rename or put the important part somewhere that make sense
  */
class TempFactory(val reporter: Reporter, val settings: doc.Settings) { processor =>

  /** The unique compiler instance used by this processor and constructed from its `settings`. */
  object compiler extends Global(settings, reporter) {
    override protected def computeInternalPhases() {
      phasesSet += syntaxAnalyzer
      phasesSet += analyzer.namerFactory
      phasesSet += analyzer.packageObjects
      phasesSet += analyzer.typerFactory
      phasesSet += superAccessors
      phasesSet += pickler
      phasesSet += refchecks
    }
    override def onlyPresentation = true
    lazy val addSourceless = {
      val sless = new SourcelessComments { val global = compiler }
      docComments ++= sless.comments
    }
  }
  import scala.collection.mutable._
  type IndexModel = HashMap[Char, Set[model.MemberEntity]]
  type IndexModel2 = HashMap[Char, collection.immutable.SortedSet[model.MemberEntity]]
  
  class IndexFactory(universe:Universe, indexModel:IndexModel) extends html.HtmlFactory(universe) {
	import io.{ Streamable, Directory }
	
	/**
	 * Borrowed from HtmlFactory generateMethod
	 */
	def copyResource(subPath: String) {
      val bytes = new Streamable.Bytes {
        val inputStream = getClass.getResourceAsStream("/scala/tools/nsc/doc/html/resource/" + subPath)
        assert(inputStream != null)
      }.toByteArray
      val dest = Directory(siteRoot) / subPath
      dest.parent.createDirectory()
      val out = dest.toFile.bufferedOutput()
      try out.write(bytes, 0, bytes.length)
      finally out.close()
    }  
	
    override def generate:Unit = {
      copyResource("lib/refIndex.css")
      for(letter <- indexModel) {
    	 new html.page.ReferenceIndex(letter._1,indexModel, universe) writeFor this
      }
    }
  }
  
  def indexModel(universe:Universe)={
	  
      val index = new IndexModel()
      
      
	  class MapHelper[D <: model.MemberEntity](m:HashMap[Char, Set[D]]) {
    	def addDoc(d:D) = {
    		val firstLetter = { 
    			val ch = d.name.head.toLower
    			if(ch.isLetterOrDigit) ch else '#'
    		}
    	 	m(firstLetter) = if(m.contains(firstLetter)) m(firstLetter) += d else Set(d)
    	  }  
      }
      
      implicit def mapHelper[D <: model.MemberEntity](m:HashMap[Char, Set[D]]) = new MapHelper(m)
      /*implicit val ordering = new math.Ordering[model.Entity] {
    	  def compare(x:model.Entity, y:model.Entity) = math.Ordering
      }*/
      implicit val ordering:math.Ordering[model.Entity] = math.Ordering.String.on{ x:model.Entity => x.name }
      val index2 = new IndexModel()
      
      def packView(packages:List[model.Package], tab:Int = 0):Unit = {
    	for(pack <- packages sortBy(_.name)) {
    	  // +" "+pack.isDocTemplate
    	  println((" " * tab) + nature2string(pack) + " " + pack.qualifiedName)
    	  templateView(pack, pack.templates, tab+2)
    	  packView(pack.packages,tab+2)
    	  
    	  nonTemplateView(pack, pack.methods,tab+2)
    	  nonTemplateView(pack, pack.values,tab+2)
    	  
    	  typeView(pack, pack.aliasTypes, tab+2)
    	  typeView(pack, pack.abstractTypes, tab+2)
    	}
      }
      def templateView(owner:model.DocTemplateEntity, templates:List[model.DocTemplateEntity], tab:Int = 0):Unit = {
    	//if(t.inDefinitionTemplates.size==0) println("EMPTY: "+t) 
    	for(t <- templates sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {
    	  // + " = " + t.inTemplate.name +" "+t.isDocTemplate+" "+" inTemplate " + t.inDefinitionTemplates)
    	  println((" " * tab) + nature2string(t) + " " + t.name);
    	  index.addDoc(t)
    	  typeView(t, t.aliasTypes, tab+2)
    	  typeView(t, t.abstractTypes, tab+2)
    	  templateView(t, t.templates, tab+2)
    	  nonTemplateView(t, t.methods,tab+2)
    	  nonTemplateView(t, t.values,tab+2)
    	}
      }
      
      def nonTemplateView(owner:model.DocTemplateEntity, nonTemplates:List[model.NonTemplateMemberEntity], tab:Int) = {
    	val filtered = nonTemplates.filter( _.inDefinitionTemplates.head == owner )
    	//if(!filtered.isEmpty) print(" " * tab)
        for(member <- nonTemplates) {
        	//print(member.name+", ")
        	index.addDoc(member)
        }
    	//if(!filtered.isEmpty) println()
      }
      
      def typeView(owner:model.DocTemplateEntity, types:List[model.NonTemplateMemberEntity], tab:Int = 0) = {
    	for(t <- types sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {    	  
    	  println((" " * tab) +  " type " + t.name);
    	  index.addDoc(t)
    	}
      }
      import model._
      def nature2string(e : model.TemplateEntity) = {    	  
    	  if(e.isTrait) "trait" else
    	  if(e.isObject) "object" else
    	  if(e.isPackage) "package" else
    	  if(e.isClass) "class" else ""
      }
      
      //packView(universe.rootPackage.packages)
      
      def gather(owner:DocTemplateEntity, templates:List[DocTemplateEntity], tab:Int = 0):Unit = {
    	def gather0(owner:DocTemplateEntity, members:List[MemberEntity], tab:Int=0):Unit = {
    		for(m <- members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) {
    			m match {
    				case tpl:DocTemplateEntity => {
    					//println((" " * tab) + nature2string(tpl) + " " + tpl.name)
    					index.addDoc(tpl)
    					gather0(tpl, tpl.members,tab+2)
    				}
    				case alias:AliasType =>
    				 // println((" " * tab) + " type " + alias.name)
    				  index.addDoc(alias)
    				case absType:AbstractType =>
    				  index.addDoc(absType)
    				 // println((" " * tab) + " type " + absType.name) 
    				case non:NonTemplateMemberEntity if !non.isConstructor => index.addDoc(non)
    				case _ => println("Something wrong with: " + m.qualifiedName + " -> "+m.getClass)
    			}
    		}
    	}
        for(t <- templates if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {
            println((" " * tab) + nature2string(t) + " " + t.name)
            gather0(t,t.members,tab+2)
        }
      }
      
      gather(universe.rootPackage, universe.rootPackage.packages)
      
      for(entry <- index) {
    	  println("i1. " + entry._1 + " - "+entry._2.size)
      }
      for(entry <- index2) {
    	  println("i2. " + entry._1 + " - "+entry._2.size)
      }
      
      index
  }
  
  /** Creates a scaladoc site for all symbols defined in this call's `files`, as well as those defined in `files` of
    * previous calls to the same processor.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def document(files: List[String]): Unit = {
	
    (new compiler.Run()) compile files
    compiler.addSourceless
    assert(settings.docformat.value == "html")
    if (!reporter.hasErrors) {
      val modelFactory = (new model.ModelFactory(compiler, settings) with model.comment.CommentFactory)
      val docModel = modelFactory.makeModel
      println("model contains " + modelFactory.templatesCount + " documentable templates")
            
      val index = indexModel(docModel)  
      new IndexFactory(docModel,index).generate
    }
  }
  
}
