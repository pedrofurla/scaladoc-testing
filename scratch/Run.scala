object Run {
	def main(args:Array[String]):Unit = {
		import util.Util._
		//"C:/dev/langs/scala/projects/scaladoc-testing/samples"
		val u = docUniverse(scalaFiles("C:/java/lang/scala-experiments/scaladoc-testing/samples"))
		//TreeView.packView(u.rootPackage.packages)
		println(TreeView.gather2(u.rootPackage))
	}
}