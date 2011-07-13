object Run {


	def main(args:Array[String]):Unit = {
		import util._

		//val u = docUniverse(scalaFiles("C:/dev/langs/scala/projects/scaladoc-testing/samples"))
		//TreeView.packView(u.rootPackage.packages)
		//println(TreeView.gather2(u.rootPackage))
    document(scalaFiles("C:/dev/langs/scala/projects/scaladoc-testing/samples"))
	}
}