package scala.tools.nsc
package doc
package html

class IndexHtmlFactory(universe:Universe, indexModel:model.IndexModelFactory#IndexModel) extends HtmlFactory(universe) {
	import io.{ Streamable, Directory }
	
	/**
	 * Borrowed from HtmlFactory generate method
	 */
	def copyResource(subPath: String) {
      val bytes = new Streamable.Bytes {
        val inputStream = getClass.getResourceAsStream("/scala/tools/nsc/doc/html/resource/" + subPath)
        assert(inputStream != null)
      }.toByteArray
      val dest = Directory(siteRoot) / subPath
      dest.parent.createDirectory()
      val out = dest.toFile.bufferedOutput()
      try out.write(bytes, 0, bytes.length)
      finally out.close()
    }  
	
    override def generate:Unit = {
      copyResource("lib/refIndex.css")
      for(letter <- indexModel) {
    	 new html.page.ReferenceIndex(letter._1,indexModel, universe) writeFor this
      }
    }
}