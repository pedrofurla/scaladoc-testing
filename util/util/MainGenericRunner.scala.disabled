package util

/** A copy from scala.tools.nsc.MainGenericRunner replacing System.exit
  */
object MainGenericRunner {
	  
import java.io.IOException
import java.lang.{ClassNotFoundException, NoSuchMethodException}
import java.lang.reflect.InvocationTargetException
import java.net.{ URL, MalformedURLException }
import scala.tools.util.PathResolver

import scala.tools.nsc.io.{ File, Process }
import scala.tools.nsc.util.{ ClassPath, ScalaClassLoader, waitingForThreads }
import scala.tools.nsc.Properties.{ versionString, copyrightString }
	  

  def main(args: Array[String]) {    
    def errorFn(str: String) = Console println str
    def exitSuccess: Nothing = { 
    		throw new Error("Sucesso")    		
    	}
    def exitFailure(msg: Any = null): Nothing = {
      if (msg != null) errorFn(msg.toString)
      throw new Error("Falha")
    }
    def exitCond(b: Boolean): Nothing = if (b) exitSuccess else exitFailure(null)
    
    val command = new scala.tools.nsc.GenericRunnerCommand(args.toList, errorFn _)
    import command.settings
    def sampleCompiler = new scala.tools.nsc.Global(settings)   // def so its not created unless needed
    
    if (!command.ok)                      return errorFn("%s\n%s".format(command.usageMsg, sampleCompiler.pluginOptionsHelp))
    else if (settings.version.value)      return errorFn("Scala code runner %s -- %s".format(versionString, copyrightString))
    else if (command.shouldStopWithInfo)  return errorFn(command getInfoMessage sampleCompiler)

    def isE   = !settings.execute.isDefault
    def dashe = settings.execute.value

    def isI   = !settings.loadfiles.isDefault
    def dashi = settings.loadfiles.value
    
    def combinedCode  = {
      val files   = if (isI) dashi map (file => File(file).slurp()) else Nil
      val str     = if (isE) List(dashe) else Nil
      
      files ++ str mkString "\n\n"
    }

    val classpath: List[URL] = new PathResolver(settings) asURLs
    
    /** Was code given in a -e argument? */
    if (isE) {
      /** If a -i argument was also given, we want to execute the code after the
       *  files have been included, so they are read into strings and prepended to
       *  the code given in -e.  The -i option is documented to only make sense
       *  interactively so this is a pretty reasonable assumption.
       *
       *  This all needs a rewrite though.
       */
      val fullArgs = command.thingToRun.toList ::: command.arguments

      exitCond(scala.tools.nsc.ScriptRunner.runCommand(settings, combinedCode, fullArgs))
    }
    else command.thingToRun match {
      case None             =>
        // Questionably, we start the interpreter when there are no arguments.
        new scala.tools.nsc.InterpreterLoop main settings

      case Some(thingToRun) =>
        val isObjectName =
          settings.howtorun.value match {
            case "object" => true
            case "script" => false
            case "guess"  => ScalaClassLoader.classExists(classpath, thingToRun)
          }

        if (isObjectName)
          try scala.tools.nsc.ObjectRunner.run(classpath, thingToRun, command.arguments)
          catch {
            case e @ (_: ClassNotFoundException | _: NoSuchMethodException) => exitFailure(e)
            case e: InvocationTargetException =>
              e.getCause.printStackTrace
              exitFailure()
          }
        else
          try exitCond(scala.tools.nsc.ScriptRunner.runScript(settings, thingToRun, command.arguments))
          catch {
            case e: IOException       => exitFailure(e.getMessage)
            case e: SecurityException => exitFailure(e)
          }
    }
  }
}