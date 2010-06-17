package testing

import scala.collection.Seq
import scala.collection.mutable.ArrayStack
abstract class AbstractParentDoc[F[A,B] <: Seq[F[A,B]] ] {	
	
	type absType
	type crtType = String
	
	abstract class AbsInnerClass
	
	val valAbsString:String
	var varAbsString:String
	def defAbsMethod:String
}

/**
 Parent do teste de docs ''será?'', '''mas é'''
 
 Ruler
 ---
 
 `mono` , __Important__ , N^21^ , K,,21,,
 
    
    - ABC
    - EFG
      - HIJ
    - HIJ
 
 = TIT 1 = AAA
 == TIT 2 ==
 === TIT 3 ===
 */
@deprecated("mensagem") 
abstract class ParentDoc(x:Int)(implicit p1:String, p2: Int) extends AbstractParentDoc {
	/**
	 * Parent method
	 * @usecase def aaa
	 */
	def parentMethod = 0
	val a = 1
	implicit val iPval = 5
	
	/**
	 * For real
	 * @usecase def realUseCase
	 */
	def realUseCase = 0
	
	/** XX
	*/
	implicit var iPvar = 5
	
		/** YY
		*/
	implicit def iPdef = 5
	def this(nonSense:String) = this(1)("",5)
	def typeBounds[F[_],Seq[A] >: F[A] <: ArrayStack[A] ]:Unit = {}
	
}
