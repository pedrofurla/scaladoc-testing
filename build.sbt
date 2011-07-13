import java.io.File

name := "catalog-helper"

version := "1.0"

organization := "none"

scalaVersion := "2.9.0-1"

scalaHome := Some(file("""C:\dev\langs\scala\scala-svn\scala-auth\local"""))

//retrieveManaged := true

//mainClass  in (Compile, run) := Some("bleh")

//scalaSource := new java.io.File("bleh/bleh.scala")

sources in (Compile) := {
    val scaladocSources =  file("../scala-doc-prj-svn/src") ** "*.scala";
    val utilSources = file("util") ** "*.scala";
    (scaladocSources +++ utilSources).get
}

fork := true

unmanagedClasspath

//fullClasspath ++= compileClasspath

//sources in (Compile) := (PathFinder(new File("util")) ** "*.scala").get

//sourceDirectories := ( ((PathFinder(new File("bleh"))) +++ (PathFinder(new File("util")) ** "util")).get)