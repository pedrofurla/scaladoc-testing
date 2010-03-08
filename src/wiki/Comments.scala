package helper
package wiki

import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

import scala.tools.nsc.doc.model.comment.{ Comment => DocComment }

object Comments extends CommentTest {
	
	override lazy val samples:List[String] = 
		"/** \n*/" :: 
		"/** \n* @version 1 */" :: 
		"/** \n @version 1 */" ::
		"""
		|/**
		| * Doc val f 
		| * @version 1
		| * @deprecated abc
		| */
		""".stripMargin::
		"""
		|/**
		| * Doc val f 
		| * 
		| * @version 1
		| * @deprecated abc
		| */
		""".stripMargin::
		"""
		|/**
		|  Doc val f 
		|  
		|  @version 1
		|  @deprecated abc
		| */
		""".stripMargin::
		Nil	
		
 
}

	
	


