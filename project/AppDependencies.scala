import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"           % "0.18.6-play26",
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.1.0",
    "uk.gov.hmrc"       %% "play-health"                   % "3.16.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.9.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-26"    % "5.12.0",
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.0.0-play-26",
    "uk.gov.hmrc"       %% "play-nunjucks"                 % "0.29.0-play-26",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"       % "0.14.0-play-26",
    "org.webjars.npm"   % "govuk-frontend"                 % "3.10.1",
    "org.webjars.npm"   % "hmrc-frontend"                  % "1.22.0",
    "com.lucidchart"    %% "xtract"                        % "2.2.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"             %% "scalatest"              % "3.0.7",
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "3.1.2",
    "org.pegdown"               % "pegdown"                 % "1.6.0",
    "org.jsoup"                 % "jsoup"                   % "1.14.2",
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current,
    "org.mockito"               % "mockito-all"             % "1.10.19",
    "org.scalacheck"            %% "scalacheck"             % "1.14.0",
    "com.github.tomakehurst"    %  "wiremock-standalone"    % "2.27.2",
    "wolfendale"                %% "scalacheck-gen-regexp"  % "0.1.1"

    ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.0.15"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}
