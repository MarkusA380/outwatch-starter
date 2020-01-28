name := "outwatch-starter"
version := "0.1"
scalaVersion := "2.12.10"

val jsBundleName = "frontend-fastopt-bundle.js"

resolvers in ThisBuild += "jitpack" at "https://jitpack.io"

// ---------------------------------------------------------------------------------------------------------------------
// ### Task keys ### //
lazy val assemble =
  taskKey[Unit]("Assembles the frontend in target")
lazy val copyAssetsToTarget = taskKey[Unit](
  "Copies the assets - directory to the target directory"
)
lazy val copyBundleToAssets = taskKey[Unit](
  "Copies the JavaScript - bundle to the assets folder in the target directory"
)

lazy val root = (project in file("."))
  .aggregate(frontend)

// ---------------------------------------------------------------------------------------------------------------------
// ### Projects ### //

lazy val frontend = (project in file("./frontend"))
  .settings(
    scalaVersion := "2.12.10",
    libraryDependencies ++= Seq(),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-unchecked",
      "-deprecation",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-language:postfixOps",
      "-Xfuture",
      "-Xlint",
      "-Ypartial-unification",
      "-Yno-adapted-args",
      "-Ywarn-extra-implicit",
      "-Ywarn-infer-any",
      "-Ywarn-value-discard",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit"
    )
  )
  .enablePlugins(
    ScalaJSPlugin,
    ScalaJSBundlerPlugin
  )
  .settings(
    scalaVersion := "2.12.10",
    // Dependencies
    libraryDependencies ++= Seq(
      "io.monix"                    %% "monix"          % "3.1.0",
      "org.typelevel"               %% "cats-core"      % "2.0.0",
      "org.typelevel"               %% "cats-effect"    % "2.0.0",
      "io.github.outwatch.outwatch" %%% "outwatch"      % "584f3f2c32",
      "io.circe"                    %%% "circe-core"    % "0.11.1",
      "io.circe"                    %%% "circe-generic" % "0.11.1",
      "io.circe"                    %%% "circe-parser"  % "0.11.1",
      "com.beachape"                %%% "enumeratum"    % "1.5.15"
    ).map(_ withSources () withJavadoc ()),
    // Config
    scalaJSUseMainModuleInitializer := true,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    version in webpack := "4.41.5",
    emitSourceMaps := false,
    useYarn := true,
    copyAssetsToTarget := {
      println("Copying assets folder to target...")
      val mainVersion = scalaVersion.value
        .split("""\.""")
        .take(2)
        .mkString(".")
      val from = baseDirectory.value / "assets"
      val to   = target.value / ("scala-" + mainVersion) / "assets"
      to.mkdirs()
      IO.copyDirectory(from, to)
      println(
        "Open the following file in the web browser: " + (to / "index.html")
      )
    },
    copyBundleToAssets := {
      println(
        "Copying JavaScript - bundle to target assets folder..."
      )
      val mainVersion = scalaVersion.value
        .split("""\.""")
        .take(2)
        .mkString(".")
      val from = target.value / ("scala-" + mainVersion) / "scalajs-bundler" / "main" / jsBundleName
      val to   = target.value / ("scala-" + mainVersion) / "assets" / "js" / jsBundleName
      IO.copyFile(from, to)
    },
    // Tasks
    assemble := {
      Def
        .sequential(
          Compile / fastOptJS / webpack,
          copyAssetsToTarget,
          copyBundleToAssets
        )
        .value
    }
  )
