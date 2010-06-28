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

class LetterIndex(letter:Char, members:Set[model.MemberEntity], universe:Universe) extends HtmlPage {
  
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
    def nature2string(e : model.TemplateEntity) = {    	  
    	  if(e.isTrait) "t " else
    	  if(e.isObject) "o " else
    	  if(e.isPackage) "p " else
    	  if(e.isClass) "c " else ""
      }
  def body =
    <body>
      { for(groups <- members.groupBy({_.name})) yield {
    	<div class="name">
    		Name: { groups._1 }
    		into: <div class="ocurances">
    		{ for(member <- groups._2) yield {
    			val owner = member.inDefinitionTemplates.head
    			templateToHtml(owner) ++ xml.Text(" ")
    		} } 
    		</div>
    	</div>
      } }
    </body>

}
