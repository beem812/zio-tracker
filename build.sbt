lazy val versions = new {
  val scalaVersion = "2.13.5"
  val zio = "1.0.9"
  val zioMagic = "0.3.5"
  val zioConfig = "1.0.4"
  val zioTestIntellij = "1.0.7"
  val zioHttp = "1.0.0.0-RC17"
}

lazy val root =
  project
    .in(file("."))
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio" % versions.zio,
        "io.github.kitlangton" %% "zio-magic" % versions.zioMagic,
        "dev.zio" %% "zio-config" % versions.zioConfig,
        "io.d11" %% "zhttp" % versions.zioHttp,
        "dev.zio" %% "zio-test" % versions.zio % Test,
        "dev.zio" %% "zio-test-sbt" % versions.zio % Test,
        //In zio-test-intellij absence, you may get no logs on some failing tests when running tests with intellij
        "dev.zio" %% "zio-test-intellij"  % versions.zioTestIntellij % Test
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

lazy val settings =
  commonSettings ++
    commandAliases

lazy val commonSettings =
  Seq(
    name := "zio-tracker",
    scalaVersion := versions.scalaVersion,
    organization := "com.example"
  )

lazy val commandAliases =
  addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt") ++
    addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val stdOptions = Seq(
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-Yrangepos",
  "-feature",
  "-language:higherKinds",
  "-language:existentials",
  "-Xlint:_,-type-parameter-shadow,-byname-implicit",
  "-Xsource:2.13",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-unchecked",
  "-deprecation",
  "-Xfatal-warnings"
)

lazy val stdOpts213 = Seq(
  "-Wunused:imports",
  "-Wvalue-discard",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wunused:params"
)

scalacOptions := stdOptions ++ stdOpts213