package helper

import scala.tools.nsc.doc.model._

object EntityHelper {
	def debug(tpl:DocTemplateEntity)={
		    println("Ca estou: "+tpl+" class:"+tpl.isClass+" obj:"+tpl.isObject)
	        println("root:"+tpl.toRoot)
	        println("members: "+tpl.members)	        
	        println("subClasses: "+tpl.subClasses)
	        println("templates: "+tpl.templates)
	        println("source: "+tpl.inSource)	        	        
	        println("inTemplate: "+tpl.inTemplate)	        	        
	        println("parentType: "+tpl.parentType)
	       // println("hasCompanion: "+getCompanion(tpl))
	        println()
	}
	
	def debugComment(mbr:MemberEntity) = {
		val a=for(comm <- mbr.comment.toList ;
				  author <- comm.authors) yield author
		
		()
	}
	
	 def main(args: Array[String]): Unit = {
		 println()
	 }
}





