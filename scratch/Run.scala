object Run {
	def main(args:Array[String]):Unit = {
		import util._
		
		val u = util.docUniverse(util.scalaFiles("C:/dev/langs/scala/projects/scaladoc-testing/samples"))
		TreeView.packView(u.rootPackage.packages)
	}
}