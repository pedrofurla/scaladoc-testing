object Run {


	def main(args:Array[String]):Unit = {
		import util.Util._

		//"C:/dev/langs/scala/projects/scaladoc-testing/samples"
    //val folder = "C:/java/lang/scala-experiments/scaladoc-testing/samples";
    val folder = "C:/java/lang/scala-svn/scala-auth/src/library";
    val files = scalaFiles(folder);

    def tree = {
      val u = docUniverse(files)
      println(TreeView.gather2(u.rootPackage))
    }

    document(files)  
	}
}