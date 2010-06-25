/* NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */


package scala.tools.nsc
package doc

import reporters.Reporter

/** Copied from scala.tools.nsc.doc.DocFactory
  * 
  * @author Gilles Dubochet */
class IndexFactory(val reporter: Reporter, val settings: doc.Settings) { processor =>

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
      
      def packView(packages:List[model.Package], tab:Int = 0) {    	
    	for(pack <- packages sortBy(_.name)) {
    	  // +" "+pack.isDocTemplate
    	  println((" " * tab) + "[]" + pack.qualifiedName)
    	  templateView(pack, pack.templates, tab+2)
    	  packView(pack.packages,tab+2)
    	}
      }
      def templateView(owner:model.DocTemplateEntity, templates:List[model.DocTemplateEntity], tab:Int = 0) {
    	for(t <- templates sortBy(_.name) if t.inDefinitionTemplates.head == owner) {
    	  // + " = " + t.inTemplate.name +" "+t.isDocTemplate+" "+" inTemplate " + t.inDefinitionTemplates)
    	  println((" " * tab) + "TPL " + t.name);
    	  templateView(t, t.templates, tab+2)
    	}
      }
      
      packView(docModel.rootPackage.packages)
      
      //(new html.HtmlFactory(docModel)).generate
    }
  }
  
}
