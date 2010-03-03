package helper
package wiki


import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

object Links extends Wiki {
	
	override lazy val samples:List[String] = 
		"[[]]" ::
		"[[ ]]" ::
		"[[  ]]" ::
		"[[   ]]" ::
		"[[http://... Titulo]]" ::
		"[[scala.String]]" ::
		"[[http://www.google.com some where]]" ::
		Nil	
		
 
}

	
	


