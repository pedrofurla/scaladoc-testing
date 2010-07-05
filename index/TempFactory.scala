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
  
  // HashMap[symbol name's first letter, SortedMap[symbol name, SortedSet[owner template]]
  type IndexModel = mutable.HashMap[Char, immutable.SortedMap[String,SortedSet[model.TemplateEntity]]]
  
  class IndexFactory(universe:Universe, indexModel:IndexModel) extends html.HtmlFactory(universe) {
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
            
      val index = new IndexModel with IndexMap
      
	  
      trait IndexMap { self : IndexModel => 
    	implicit def orderingSet = math.Ordering.String.on { x:TemplateEntity => x.name.toLowerCase }
    	implicit def orderingMap = math.Ordering.String.on { x:String => x.toLowerCase }
    	
    	def addMember(d:MemberEntity) = {
    		val firstLetter = { 
    			val ch = d.name.head.toLower
    			if(ch.isLetterOrDigit) ch else '#'
    		}
    		
    		if(this.contains(firstLetter)) {
    			val letter = this(firstLetter)
    			val value = this(firstLetter).get(d.name).getOrElse(SortedSet.empty[TemplateEntity]) + d.inDefinitionTemplates.head
    			this(firstLetter) = letter + ((d.name, value))    			
    		} else {
    			this(firstLetter) = immutable.SortedMap( (d.name, SortedSet(d.inDefinitionTemplates.head)) )
    		}
    	  }  
      }
      
      //implicit def mapHelper(m:IndexModel) = new MapHelper(m)     
                 
      //@scala.annotation.tailrec // TODO
	  def gather(owner:DocTemplateEntity):Unit = 	 	  
		for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) 
			m match {
				case tpl:DocTemplateEntity => {    					
					index.addMember(tpl)
					gather(tpl)
				}
				case alias:AliasType => index.addMember(alias)
				case absType:AbstractType => index.addMember(absType)
				case non:NonTemplateMemberEntity if !non.isConstructor => index.addMember(non)
				case non:NonTemplateMemberEntity if non.isConstructor => 
				case x @ _ => 
			}
		
      
      gather(universe.rootPackage)
      
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
      
      val time1 = new java.util.Date().getTime
      val docModel = modelFactory.makeModel
      println("model contains " + modelFactory.templatesCount + " documentable templates")
      
      val time2 = new java.util.Date().getTime
      val index = indexModel(docModel)  
      val time3 = new java.util.Date().getTime 
      val indexFactory = new IndexFactory(docModel,index)      
      indexFactory.generate
      val time4 = new java.util.Date().getTime
      
      printf("compilation: %,d ms\n", (time2-time1))
      printf("index structure generation: %,d ms\n", (time3-time2))
      printf("writing files: %,d ms\n", (time4-time3))
      
      //oldHierarchyBuilder(docModel)
    }
  }
      
}
