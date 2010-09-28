import scala.tools.nsc.doc._

object TreeView {

	def packView(packages:List[model.Package], tab:Int = 0):Unit = 
		for(pack <- packages sortBy(_.name)) {
			println((" " * tab) + nature2string(pack) + " " + pack.qualifiedName)
			templateView(pack, pack.templates, tab+2)
			packView(pack.packages,tab+2)
			
			nonTemplateView(pack, pack.values,tab+2)
			nonTemplateView(pack, pack.methods,tab+2)
			
			typeView(pack, pack.abstractTypes, tab+2)
			typeView(pack, pack.aliasTypes, tab+2)
		}
	
	def templateView(owner:model.DocTemplateEntity, templates:List[model.DocTemplateEntity], tab:Int = 0):Unit = 	
		for(t <- templates sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner) {
			println((" " * tab) + nature2string(t) + " " + t.name);
			
			typeView(t, t.aliasTypes, tab+2)
			typeView(t, t.abstractTypes, tab+2)
			templateView(t, t.templates, tab+2)
			nonTemplateView(t, t.methods,tab+2)
			nonTemplateView(t, t.values,tab+2)
		}
	
			
	def nonTemplateView(owner:model.DocTemplateEntity, nonTemplates:List[model.NonTemplateMemberEntity], tab:Int) = {
		val filtered = nonTemplates.filter( _.inDefinitionTemplates.head == owner )
		if(!filtered.isEmpty) println(" " * tab)
		for(member <- filtered) 
			println(" " * tab+nature2string(member) + " " +member.name+", ")		
	}
			
	def typeView(owner:model.DocTemplateEntity, types:List[model.NonTemplateMemberEntity], tab:Int = 0) = 
		for(t <- types sortBy(_.name) if t.inDefinitionTemplates.isEmpty || t.inDefinitionTemplates.head == owner)     	  
			println((" " * tab) +  " type " + t.name)
		
	
	def nature2string(e : model.Entity) = 
		e match {
			case t : model.TemplateEntity if(t.isTrait) => "trait "
			case t : model.TemplateEntity if(t.isObject) => "object "
			case t : model.TemplateEntity if(t.isPackage) => "package "
			case t : model.TemplateEntity if(t.isClass) => "class " 
			case e : model.MemberEntity if(e.isDef) => "def "
			case e : model.MemberEntity if(e.isVal) => "val "
			case e : model.MemberEntity if(e.isLazyVal) => "lazy val "
			case e : model.MemberEntity if(e.isVar) => "var "
			case e : model.MemberEntity if(e.isAliasType) => "type "
			case a : model.AbstractType => "type "
			case u @ _ =>  
				println("unknown "+ u+ " " +u.getClass); ""			
		}

	
}