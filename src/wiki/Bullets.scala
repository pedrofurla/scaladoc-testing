package helper
package wiki


import scala.tools.nsc.reporters._
import scala.tools.nsc.util._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model.comment._

object Bullets extends Wiki {
	
	override def runSamples = {		
		for(text <- samples) {			
			println(mkString0(runSample(text)))
		}
	}

	def mkString0(b:Body):String = {
		val Body(body) = b
		val r=(for(block <- body) yield mkString(block,"","")).foldLeft("")(_+_)
		r.replaceAll("\\t", "   ")
	} 
	
	def mkString(b:Block,pre:String, tpe: String):String = {
		b match {
			case Paragraph(t) => pre + tpe+ " " +t + "\n"
			case OrderedList(list,s) => {
				(for(block <- list) yield mkString(block,pre+"\t",s+" ")).foldLeft("")(_+_) 
			}
			case UnorderedList(list) => {
				(for(block <- list) yield mkString(block,pre+"\t",">")).foldLeft("")(_+_) 
			}
			case x@_ => x.toString
		}
	}
	
	override lazy val samples:List[String] = 		
		wrongMixing :: //c11 :: c1 :: c2 :: c10 ::
		//" * LIST1 " :: 
		//" * LIST1 \n * LIST2 " ::
		//" * LIST1 \n * LIST2 \n * LIST3 " ::
		//c1 :: c2 :: err1 ::
		Nil
	
	
	val c1 = 
	""" 
	| - L1
	| - L2
	|    - L2.1""".stripMargin
	val c2 = c1 + 
	"""  
	| - L3
	|   - L3.1
	|     - L3.1.1""".stripMargin
	val c10 = 
	""" - L1
	| - L2
	|    - L2.1
	|      - L2.1.1
	|    - L2.2
	|      - L2.2.1
	|      - L2.2.2
	| - L3
	|   - L3.1
	|     - L3.1.1
	| - L4""".stripMargin
	val c11 = 
	""" - L1
	| - L2
	|    i. L2.1
	|      - L2.1.1
	|    i. L2.2
	|      a. L2.2.1
	|      a. L2.2.2
	| - L3
	|   i. L3.1
	|     i. L3.1.1
	| - L4""".stripMargin
	val c12 = 
	""" - L1
	| i. L2
	|    - L2.1""".stripMargin
	val wrongMixing = 
	""" - L1
	| i. L2
	|
	|    - L2.1""".stripMargin
		
		
	

	
 
}

	
	


