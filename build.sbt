import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

val commonSettings = Seq(
  name := "vtuber-chat-stats",
  organization := "io.lambdasawa",
  version := "0.1",
  scalaVersion := "2.12.7",
)

lazy val root =
  project
    .in(file("."))
    .settings(commonSettings)

lazy val command =
  project
    .in(file("command"))
    .settings(commonSettings)
    .settings(
      name := "vtcs-command",
      libraryDependencies ++= Seq(
        "org.rogach" %% "scallop" % "3.1.5"
      ),
      assemblyJarName in assembly := "vtcs.jar",
      npmAssets ++= NpmAssets
        .ofProject(browser) { modules =>
          Seq(
            modules / "bootstrap" / "dist" / "js",
            modules / "bootstrap" / "dist" / "css",
          ).map(_.allPaths).reduce(_ +++ _)
        }
        .value,
      scalaJSProjects := Seq(browser),
      pipelineStages in Assets := Seq(scalaJSPipeline),
      compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
      WebKeys.packagePrefix in Assets := "public/",
      managedClasspath in Runtime += (packageBin in Assets).value,
    )
    .dependsOn(web, job, core)
    .enablePlugins(AssemblyPlugin, WebScalaJSBundlerPlugin)

lazy val web =
  project
    .in(file("web"))
    .settings(commonSettings)
    .settings(
      name := "vtcs-web",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http"       % "10.1.5",
        "com.typesafe.akka" %% "akka-stream"     % "2.5.18",
        "com.typesafe.akka" %% "akka-actor"      % "2.5.18",
        "de.heikoseeberger" %% "akka-http-circe" % "1.22.0",
      ),
    )
    .dependsOn(core, sharedJvm)

lazy val browser =
  project
    .in(file("browser"))
    .settings(commonSettings)
    .settings(
      name := "vtcs-browser",
      scalaJSUseMainModuleInitializer := true,
      libraryDependencies ++= Seq(
        "org.scala-js"                      %%% "scalajs-dom" % "0.9.5",
        "com.github.japgolly.scalajs-react" %%% "core"        % "1.3.1",
        "com.github.japgolly.scalajs-react" %%% "extra"       % "1.3.1",
      ),
      npmDependencies in Compile ++= Seq(
        "react"     -> "16.5.1",
        "react-dom" -> "16.5.1",
        "bootstrap" -> "4.1.3",
      ),
      webpackBundlingMode := BundlingMode.LibraryOnly(),
      emitSourceMaps := false,
    )
    .dependsOn(sharedJs)
    .enablePlugins(ScalaJSWeb, ScalaJSBundlerPlugin)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core"           % "0.10.0",
      "io.circe" %%% "circe-parser"         % "0.10.0",
      "io.circe" %%% "circe-generic"        % "0.10.0",
      "io.circe" %%% "circe-generic-extras" % "0.10.0",
    ),
  )
lazy val sharedJvm = shared.jvm
lazy val sharedJs  = shared.js

lazy val job =
  project
    .in(file("job"))
    .settings(commonSettings)
    .settings(
      name := "vtcs-job",
      libraryDependencies ++= Seq(
        ),
      addCompilerPlugin(
        "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
      )
    )
    .dependsOn(core)

lazy val core =
  project
    .in(file("core"))
    .settings(commonSettings)
    .settings(
      name := "vtcs-core",
      libraryDependencies ++= Seq(
        "com.github.pureconfig"   %% "pureconfig"                 % "0.10.0",
        "ch.qos.logback"          % "logback-classic"             % "1.2.3",
        "com.typesafe.akka"       %% "akka-actor"                 % "2.5.18",
        "com.typesafe.akka"       %% "akka-stream"                % "2.5.18",
        "com.typesafe.akka"       %% "akka-http"                  % "10.1.5",
        "com.typesafe.akka"       %% "akka-slf4j"                 % "2.5.18",
        "com.typesafe.slick"      %% "slick"                      % "3.2.3",
        "com.typesafe.slick"      %% "slick-codegen"              % "3.2.3",
        "com.typesafe.slick"      %% "slick"                      % "3.2.3",
        "com.typesafe.slick"      %% "slick-hikaricp"             % "3.2.3",
        "mysql"                   % "mysql-connector-java"        % "5.1.46",
        "net.debasishg"           %% "redisclient"                % "3.8",
        "io.circe"                %% "circe-core"                 % "0.10.0",
        "io.circe"                %% "circe-generic"              % "0.10.0",
        "io.circe"                %% "circe-generic-extras"       % "0.10.0",
        "io.circe"                %% "circe-parser"               % "0.10.0",
        "com.google.api-client"   % "google-api-client"           % "1.27.0",
        "com.google.oauth-client" % "google-oauth-client-jetty"   % "1.23.0",
        "com.google.apis"         % "google-api-services-youtube" % "v3-rev198-1.23.0",
        "com.rometools"           % "rome"                        % "1.7.0",
        "org.scalatest"           %% "scalatest"                  % "3.0.5" % Test,
        "com.typesafe.akka"       %% "akka-testkit"               % "2.5.18" % Test,
      ),
      flywayDriver := "com.mysql.jdbc.Driver",
      flywayUrl := "jdbc:mysql://localhost:3306/vtcs?useSSL=false",
      flywayUser := "root",
      flywayPassword := "root",
      flywayLocations += "db/migration",
    )
    .enablePlugins(FlywayPlugin)
