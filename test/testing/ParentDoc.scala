package testing

/**
 Parent do teste de docs ''será?'', '''mas é'''
 
 Ruler
 ---
 
 `mono` , __Important__ , N^21^ , K,,21,,
 
    
    - ABC
    - EFG
    - HIJ
 
 = TIT 1 = AAA
 == TIT 2 ==
 === TIT 3 ===
 */
@deprecated("mensagem") 
class ParentDoc(x:Int)(implicit p1:String, p2: Int) {
	/**
	 * Parent method
	 * @usecase def aaa
	 */
	def parentMethod = 0
	val a = 1
	implicit val iPval = 5
	implicit var iPvar = 5
	implicit def iPdef = 5
	def this(nonSense:String) = this(1)("",5)
}
