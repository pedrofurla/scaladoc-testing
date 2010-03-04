package helper
package wiki

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

object AdHoc extends Wiki {
	
	override lazy val samples:List[String] = 
		"===AAAA===" ::
		"----" ::
		"[[AAAA]]" :: 
		" * AAAA " ::
		" {{{ code }}} " ::
		"{{{ code2 }}}" :: 			
		" blablabla bla bla \n blabla bla\n {{{ code }}} = lala ="+eot ::
		" blablabla bla bla \n blabla bla\n{{{ code }}} \n= lala ="+eot ::
		Nil
		
 
}

	
	


