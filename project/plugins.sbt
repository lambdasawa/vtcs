addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.8")

addSbtPlugin("com.vmunier"        % "sbt-web-scalajs"          % "1.0.8-0.6")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.26")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.5.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"      % "0.14.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-web-scalajs-bundler"  % "0.14.0")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.2.0")

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.46"
)
