import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._

object Build extends Build {
  val Organization = "uk.co.vivalogic"
  val Name = "ScreenServer"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.6"

  def sharedSettings = /*graphSettings ++*/ Seq(
    scalaVersion in ThisBuild := "2.11.6",
    scalacOptions += "-deprecation",
    resolvers += "Sonatype Repo" at "http://oss.sonatype.org/content/groups/public/",
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Spray repo" at "http://repo.spray.io",
    resolvers += "Teleal (Cling) repo" at "http://teleal.org/m2",

    libraryDependencies ++= {
      Seq(
        "org.scala-lang" % "scala-reflect" % ScalaVersion,  // These two required in order to pull in correct version for Scalate
        "org.scala-lang" % "scala-compiler" % ScalaVersion,
        "org.slf4j" % "slf4j-simple" % "1.6.4",
        "org.mongodb" %% "casbah" % "2.8.0",
        "junit" % "junit" % "4.8" % "test->default",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test",
        "org.scalatra" %% "scalatra-scalatest" % "2.3.0" % "test",
        "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
        "org.jmock" % "jmock-junit4" % "2.5.1",
        "org.jmock" % "jmock-legacy" % "2.5.1",
        "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
        "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.9",
        "io.spray" %% "spray-can" % "1.3.3",
        "io.spray" %% "spray-routing" % "1.3.3",
        "org.teleal.cling" % "cling-core" % "1.0.5",
        "org.teleal.cling" % "cling-support" % "1.0.5",
        "org.scalafx" %% "scalafx" % "8.0.20-R6",
        "org.apache.commons" % "commons-io" % "1.3.2"
      )
    }
  )

  def screenServerSettings = Seq(
    mainClass := Some("uk.co.vivalogic.ScreenServer"),
    unmanagedResourceDirectories in Compile <+= baseDirectory / "src/test/webapp",
    test in assembly := {},
    testOptions in FxTest := Seq(Tests.Filter(fxFilter)),
    testOptions in Test := Seq(Tests.Filter(testFilter)),
    testOptions in WebTest := Seq(Tests.Filter(webFilter)),
    testOptions in NonGuiTest := Seq(Tests.Filter(nonGuiFilter)),
    unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))
  )

  lazy val screenServer = Project(id = "screenServer", base = file("."))
    .settings(sharedSettings: _*)
    .settings(screenServerSettings: _*)
    .configs(WebTest)
    .configs(FxTest)
    .configs(NonGuiTest)
    .settings(inConfig(WebTest)(Defaults.testTasks) : _*)
    .settings(inConfig(FxTest)(Defaults.testTasks) : _*)
    .settings(inConfig(NonGuiTest)(Defaults.testTasks) : _*)

  def testFilter(name: String): Boolean = name endsWith "Test"
  def webFilter(name: String): Boolean = name endsWith "WebTest"
  def fxFilter(name: String): Boolean = name endsWith "FxTest"
  def nonGuiFilter(name: String): Boolean = testFilter(name) && !webFilter(name) && !fxFilter(name)

  lazy val WebTest = config("web") extend Test
  lazy val FxTest = config("fx") extend Test
  lazy val NonGuiTest = config("nonGui") extend Test
}
