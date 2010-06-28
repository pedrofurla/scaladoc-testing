/* NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */


package scala.tools.nsc
package doc

import reporters.Reporter

/** Copied from scala.tools.nsc.doc.DocFactory
  * TODO Rename or put the important part somewhere it makes sense
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
  
  class IndexFactory(universe:Universe, indexModel:IndexModel) extends html.HtmlFactory(universe) {
	  
    override def generate:Unit = {
      for(letter <- indexModel) {
    	 new html.page.LetterIndex(letter._1,letter._2, universe) writeFor this
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
    	  
      def packView(packages:List[model.Package], tab:Int = 0):Unit = {
    	for(pack <- packages sortBy(_.name)) {
    	  // +" "+pack.isDocTemplate
    	  println((" " * tab) + nature2string(pack) + " " + pack.qualifiedName)    	  
    	  templateView(pack, pack.templates, tab+2)
    	  packView(pack.packages,tab+2)
    	}
      }
      def templateView(owner:model.DocTemplateEntity, templates:List[model.DocTemplateEntity], tab:Int = 0):Unit = {
    	//if(t.inDefinitionTemplates.size==0) println("EMPTY: "+t) 
    	for(t <- templates sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {
    	  // + " = " + t.inTemplate.name +" "+t.isDocTemplate+" "+" inTemplate " + t.inDefinitionTemplates)
    	  println((" " * tab) + nature2string(t) + " " + t.name);
    	  index.addDoc(t)
    	  typeView(t, t.aliasTypes, tab+2)
    	  templateView(t, t.templates, tab+2)
    	  nonTemplateView(t, t.methods,tab+2)
    	  nonTemplateView(t, t.values,tab+2)
    	}
      }
      
      def nonTemplateView(owner:model.DocTemplateEntity, nonTemplates:List[model.NonTemplateMemberEntity], tab:Int) = {
    	val filtered = nonTemplates.filter( _.inDefinitionTemplates.head == owner )
    	if(!filtered.isEmpty) print(" " * tab)
        for(member <- nonTemplates) {
        	print(member.name+", ")
        	index.addDoc(member)
        }
    	if(!filtered.isEmpty) println()
      }
      
      def typeView(owner:model.DocTemplateEntity, types:List[model.AliasType], tab:Int = 0) = {
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
      
      packView(universe.rootPackage.packages)
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
