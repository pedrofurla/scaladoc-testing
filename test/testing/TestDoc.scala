/**
 * Doc de pacote
 * @author AutorDoPacote
 */
package testing

/** 
* Teste de docs class TestDoc
* 
*    - SPC 
*    - SPC2
* 
*  - T1
*  - T2
*   - T2.1
*     a. T2.1.1
*     a. T2.1.2
*       i. T2.1.2.1
*       i. T2.1.2.1
*     a. T2.1.3
*   - T2.2
*  - T3
* 
* Some time, [[http://www.google.com some where]]
* 
* I was [[http://www.google.com]]-ing
* 
* {{{
* 	some code here
* 			more code
* }}}
* 
* @version 2.8
* @since   1.0
* @author Pedro
* @author ''fulano''
* @see [[testing.ParentDoc the parent]]
* @see [[testing.TestDoc]]
*/
final class TestDoc(val p2:String="AAA", val p3:Int=5, var p4:Double = 1.0) extends ParentDoc(1)(p2,1) {
	
	override val a = 1
	
	implicit val b = 3

	/**
	 * Doc val c
	 * 
	 * @version 4
	 */
	@deprecated
	implicit val c = 4
	
	/**
Doc val d
	  
	  @version 4
	 */
	var d = 1
	
	implicit var e = 3

	/**
	 * Doc val f 
	 * @version 1
	 * @deprecated abc
	 */
	implicit var f = 4
	
	/**
	*  Method
	*               
	*  @version 3
	*  @since   4
	*  
	* @author Pedro2
	* @author ''fulano2''
	* @see ParentDoc
	* @see TestDoc
	*/
	def method:this.type = this
	
	def other(start:Int=1) = new TestDoc(p2 = "A")
	
	/**
	 * Other method
	 * @param xs     the array of elements
	 * @param start  the start index
	 * @tparam A	nada mesmo
	 */
	@deprecated("anotado...") 
	def otherMethod[A](xs:A,start2:Int=10) = 5
	
	/**
	 * Simple 
	 * @deprecated Taglet depre. msg
	 * @author alalal
	 */
	def anotherMethod = 0;

  def implTest(x:Int)(implicit p:List[String],z:Int) = p
  def implTest2(implicit p:List[String]) = p
	
}

/** 
* Teste de docs obj TestDoc
* @author Pedro
* @deprecated use TestDoc
*/object TestDoc {
	/** Constroi...
	 * @return um TestDoc
	 * @deprecated Taglet Deprecated 
	*/
	def apply = new TestDoc;

  def main = {
    //new TestDoc()
  }

}


