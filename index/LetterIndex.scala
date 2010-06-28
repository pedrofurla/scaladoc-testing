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

class LetterIndex(letter:Char, indexModel:doc.TempFactory#IndexModel, universe:Universe) extends HtmlPage {
  
  def path = List("index-"+letter+".html","index")

  def title = {
    val s = universe.settings
    ( if (!s.doctitle.isDefault) s.doctitle.value else "" ) +
    ( if (!s.docversion.isDefault) (" " + s.docversion.value) else "" ) 
  }

  def headers =
    <xml:group>
      <link href={ relativeLinkTo(List("index.css", "lib")) }  media="screen" type="text/css" rel="stylesheet"/>
		  <script type="text/javascript" src={ relativeLinkTo{List("jquery.js", "lib")} }></script>
      <script type="text/javascript" src={ relativeLinkTo{List("scheduler.js", "lib")} }></script>
      <script type="text/javascript" src={ relativeLinkTo{List("index.js", "lib")} }></script>
    </xml:group>
    def nature2string(e : model.Entity) = 
    	e match {
    	  case t : model.TemplateEntity if(t.isTrait) => "t "
    	  case t : model.TemplateEntity if(t.isObject) => "o "
    	  case t : model.TemplateEntity if(t.isPackage) => "p "
    	  case t : model.TemplateEntity if(t.isClass) => "c " 
    	  case e : model.MemberEntity if(e.isDef) => "d "
    	  case e : model.MemberEntity if(e.isVal) => "vl "
    	  case e : model.MemberEntity if(e.isVar) => "vr "
    	  case e : model.MemberEntity if(e.isAliasType) => "tp "
    	  case a : model.AbstractType => "atp "

      }
  val groupedMembers = indexModel(letter).groupBy({_.name})  
  def indexLinks = 
	  <div>
  		{ for(letter <- indexModel.keySet) yield { // TODO Sorting
  		  val ch = if(letter=='#') "%23" else letter // url encoding if needed  
  		  <a href={"index-"+ch+".html"}>{letter}</a> ++ xml.Text(" | ") 	
  		} }
      </div>
  
  def body =
    <body>
      { indexLinks }
      { for(groups <- groupedMembers) yield {
    	<div class="name">
    		{ groups._1 }: 
    		 <div class="ocurances">
    		{ for(member <- groups._2) yield {
    			val owner = member.inDefinitionTemplates.head
    			templateToHtml(owner) ++ xml.Text(" as "+nature2string(member))
    		} } 
    		</div>
    	</div>
      } }
    </body>

}
