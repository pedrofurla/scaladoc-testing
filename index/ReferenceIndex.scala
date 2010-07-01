/* NSC -- new Scala compiler
 * Copyright 2007-2010 LAMP/EPFL
 * @author  David Bernard, Manohar Jonnalagedda
 */
 
package scala.tools.nsc
package doc
package html
package page

import model._

import scala.collection._
import scala.xml._

class ReferenceIndex(letter:Char, indexModel:doc.TempFactory#IndexModel, universe:Universe) extends HtmlPage {
  
  def path = List("index-"+letter+".html","index")

  def title = {
    val s = universe.settings
    ( if (!s.doctitle.isDefault) s.doctitle.value else "" ) +
    ( if (!s.docversion.isDefault) (" " + s.docversion.value) else "" ) 
  }

  def headers =
    <xml:group>
      <link href={ relativeLinkTo(List("refIndex.css", "lib")) }  media="screen" type="text/css" rel="stylesheet"/>
	  <script type="text/javascript" src={ relativeLinkTo{List("jquery.js", "lib")} }></script>
    </xml:group>
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
      case u @ _ => { 
 	      println("unknown "+ u+ " " +u.getClass); "" 
	  }
  }
  val groupedMembers = indexModel(letter).groupBy({_.name})  
  def indexLinks = 
	  <div class="letters">
  		{ for(l <- indexModel.keySet.toList.sortBy( _.toString )) yield { // TODO This line is extremelly stupid
  		  val ch = if(l=='#') "%23" else l // url encoding if needed
  		  ( if(letter != l) 
  		 	  <span><a href={"index-"+ch+".html"}>{l.toUpper}</a></span> 
  		    else
  		  	  xml.Text(l.toUpper.toString) ) ++ xml.Text(" | ")
  		} }
      </div>
  
  def body =
    <body>
      { indexLinks }
      { for(groups <- groupedMembers) yield {
    	<div class="entry">
    		<div class="name">{ groups._1 }</div> 
    		<div class="occurrences">
    		  { for(member <- groups._2) yield { 
    			  val owner = member.inDefinitionTemplates.head
    			  <span>{xml.Text(nature2string(member)+" in ")}</span> ++ 
    			    templateToHtml(owner) ++ xml.Text(" ")
    		  } } 
    		</div>
    	</div>
       } }
    </body>

}
