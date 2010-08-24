package scala.tools.nsc
package doc
package model

import scala.collection._

class IndexModelFactory {
	
  // HashMap[symbol name's first letter, SortedMap[symbol name, SortedSet[owner template]]
  type IndexModel = mutable.HashMap[Char, immutable.SortedMap[String,SortedSet[model.TemplateEntity]]] 
  
  def indexModel(universe:Universe)={
      import model._
            
      val index = new IndexModel { 
      	/** Owner template ordering */
    	implicit def orderingSet = math.Ordering.String.on { x:TemplateEntity => x.name.toLowerCase }
    	/** symbol name ordering */
    	implicit def orderingMap = math.Ordering.String.on { x:String => x.toLowerCase }
    	
    	def addMember(d:MemberEntity) = {
    		val firstLetter = { 
    			val ch = d.name.head.toLower
    			if(ch.isLetterOrDigit) ch else '#'
    		}
    		this(firstLetter) =
	    		if(this.contains(firstLetter)) {
	    			val letter = this(firstLetter)
	    			val value = this(firstLetter).get(d.name).getOrElse(SortedSet.empty[TemplateEntity]) + d.inDefinitionTemplates.head
	    			letter + ((d.name, value))    			
	    		} else {
	    			immutable.SortedMap( (d.name, SortedSet(d.inDefinitionTemplates.head)) )
	    		}
    	  }  
      }      
	              
      //@scala.annotation.tailrec // TODO
	  def gather(owner:DocTemplateEntity):Unit = 	 	  
		for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) 
			m match {
				case tpl:DocTemplateEntity => {    					
					index.addMember(tpl)
					gather(tpl)
				}
				case alias:AliasType => index.addMember(alias)
				case absType:AbstractType => index.addMember(absType)
				case non:NonTemplateMemberEntity if !non.isConstructor => index.addMember(non)
				case x @ _ => 
			}
		
      
      gather(universe.rootPackage)
      
      index
  }
}