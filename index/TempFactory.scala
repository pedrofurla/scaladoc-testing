/* NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */

package scala.tools.nsc
package doc

import reporters.Reporter
import scala.collection._

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
  // TODO Found that IndexModel has to use a Set, otherwise overloaded methods get repeated.
  type IndexModel = mutable.HashMap[Char, SortedSet[model.MemberEntity]] // apparently sortedset is keeping only one element
  
  // HashMap[Name's First Letter, HashMap[Name, SortedSet[Owner Template]]
  type IndexModel2 = mutable.HashMap[Char, mutable.HashMap[String,SortedSet[model.TemplateEntity]]]
  
  class IndexFactory(universe:Universe, indexModel:IndexModel2) extends html.HtmlFactory(universe) {
	import io.{ Streamable, Directory }
	
	/**
	 * Borrowed from HtmlFactory generate method
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
      import model._
            
      val index = new IndexModel2()      
	  
      class MapHelper(m:IndexModel2) {
    	implicit def ordering = math.Ordering.String.on { x:TemplateEntity => x.name.toLowerCase }
    	
    	def addDoc(d:MemberEntity) = {
    		val firstLetter = { 
    			val ch = d.name.head.toLower
    			if(ch.isLetterOrDigit) ch else '#'
    		}
    		if(m.contains(firstLetter)) {
    			m(firstLetter)(d.name) = 
    				if(m(firstLetter).contains(d.name)) 
    					m(firstLetter)(d.name) + d.inDefinitionTemplates.head 
    				else SortedSet(d.inDefinitionTemplates.head)
    		} else {
    			m(firstLetter) = mutable.HashMap( (d.name, SortedSet(d.inDefinitionTemplates.head)) )
    		}
    	 	//if(d.name=="toString") println(firstLetter + ": "+d.name+": " + m(firstLetter)(d.name))
    	  }  
      }
      
      implicit def mapHelper(m:IndexModel2) = new MapHelper(m)     
                 
      //@scala.annotation.tailrec // TODO
	  def gather(owner:DocTemplateEntity):Unit = {	 	  
		for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) {
			computedMembers = computedMembers + 1
			m match {
				case tpl:DocTemplateEntity => {    					
					index.addDoc(tpl)
					gather(tpl)
				}
				case alias:AliasType => index.addDoc(alias)
				case absType:AbstractType => index.addDoc(absType)
				case non:NonTemplateMemberEntity if !non.isConstructor => index.addDoc(non)
				case non:NonTemplateMemberEntity if non.isConstructor => 
				case x @ _ => {    					
					println(m.qualifiedName)
				}
			}    				
		}
      }
      gather(universe.rootPackage)
      
      index
  }
   var computedMembers = 0L
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
      val time1 = System.nanoTime
      val indexFactory = new IndexFactory(docModel,index)
      val time2 = System.nanoTime
      indexFactory.generate
      val time3 = System.nanoTime
      
      printf("Structure generation: %,d\n", (time2-time1))
      printf("Writing files: %,d\n", (time3-time2))
      printf("computedMembers: %,d\n", (computedMembers))
      
      //oldHierarchyBuilder(docModel)
    }
  }
  
  // Will be used to figure out why 'java' elements were being added
  def oldHierarchyBuilder(uni:Universe):Unit = {
	  import model._
	  
      type IndexModel = mutable.HashMap[Char, mutable.Set[MemberEntity]]
	  class MapHelper(m:IndexModel) {
    	def addDoc(d:MemberEntity) = {
    		val firstLetter = { 
    			val ch = d.name.head.toLower
    			if(ch.isLetterOrDigit) ch else '#'
    		}
    	 	m(firstLetter) = if(m.contains(firstLetter)) m(firstLetter) + d else mutable.Set(d)
    	  }  
      }
      
      implicit def mapHelper[D <: MemberEntity](m:IndexModel) = new MapHelper(m)  
	  
	  val index = new IndexModel()      
      def packView(packages:List[Package], tab:Int = 0):Unit = {
    	for(pack <- packages sortBy(_.name)) {
    	  // +" "+pack.isDocTemplate
    	  //println((" " * tab) + nature2string(pack) + " " + pack.qualifiedName)
    	  templateView(pack, pack.templates, tab+2)
    	  packView(pack.packages,tab+2)
    	  
    	  nonTemplateView(pack, pack.methods,tab+2)
    	  nonTemplateView(pack, pack.values,tab+2)
    	  
    	  typeView(pack, pack.aliasTypes, tab+2)
    	  typeView(pack, pack.abstractTypes, tab+2)
    	}
      }
      def templateView(owner:DocTemplateEntity, templates:List[DocTemplateEntity], tab:Int = 0):Unit = {
    	
    	for(t <- templates sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {
    	  // + " = " + t.inTemplate.name +" "+t.isDocTemplate+" "+" inTemplate " + t.inDefinitionTemplates)
    	  //println((" " * tab) + nature2string(t) + " " + t.name);
    	  //index.addDoc(t)
    	  typeView(t, t.aliasTypes, tab+2)
    	  typeView(t, t.abstractTypes, tab+2)
    	  templateView(t, t.templates, tab+2)
    	  nonTemplateView(t, t.methods,tab+2)
    	  nonTemplateView(t, t.values,tab+2)
    	}
      }
      
      def nonTemplateView(owner:DocTemplateEntity, nonTemplates:List[NonTemplateMemberEntity], tab:Int) = {
    	val filtered = nonTemplates.filter( _.inDefinitionTemplates.head == owner )
    	//if(!filtered.isEmpty) print(" " * tab)
        for(member <- nonTemplates) {
        	//print(member.name+", ")
        	//index.addDoc(member)
        }
    	//if(!filtered.isEmpty) println()
      }
      
      def typeView(owner:DocTemplateEntity, types:List[NonTemplateMemberEntity], tab:Int = 0) = {
    	for(t <- types sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {    	  
    	  //println((" " * tab) +  " type " + t.name);
    	  //index.addDoc(t)
    	}
      }
      
      def nature2string(e : TemplateEntity) = {    	  
    	  if(e.isTrait) "trait" else
    	  if(e.isObject) "object" else
    	  if(e.isPackage) "package" else
    	  if(e.isClass) "class" else ""
      }
      
      packView(uni.rootPackage.packages)
  }
  
  
  
}
