
import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "currency-converter-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.edulify" % "currency-converter_2.10" % "1.1.2"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    // resolvers += Resolver.url("edulify repository", url("http://blabluble.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
    resolvers += "Local Play Repository" at "file://home/ranieri/workspace/play/repository/local"
  )

}
