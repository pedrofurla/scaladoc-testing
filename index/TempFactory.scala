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
   
  /** Creates a scaladoc site for all symbols defined in this call's `files`, as well as those defined in `files` of
    * previous calls to the same processor.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def document(files: List[String]): Unit = {
	
    (new compiler.Run()) compile files
    compiler.addSourceless
    assert(settings.docformat.value == "html")
    if (!reporter.hasErrors) {
      val modelFactory = (new model.ModelFactory(compiler, settings) with model.comment.CommentFactory)
      val indexFactory = new model.IndexModelFactory
      
      val time1 = new java.util.Date().getTime
      val docModel = modelFactory.makeModel
      println("model contains " + modelFactory.templatesCount + " documentable templates")
      
      val time2 = new java.util.Date().getTime
      val index = indexFactory.indexModel(docModel)  
      val time3 = new java.util.Date().getTime 
      val indexHtmlFactory = new html.IndexHtmlFactory(docModel,index)
      indexHtmlFactory.generate
      val time4 = new java.util.Date().getTime
      
      printf("compilation: %,d ms\n", (time2-time1))
      printf("index structure generation: %,d ms\n", (time3-time2))
      printf("writing files: %,d ms\n", (time4-time3))
      
    }
  }
      
}
