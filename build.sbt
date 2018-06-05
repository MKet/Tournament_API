name := "Tournament_API"
 
version := "1.0" 
      
lazy val `tournament_api` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice, "org.mindrot" % "jbcrypt" % "0.3m", "com.pauldijou" %% "jwt-play" % "0.16.0")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )