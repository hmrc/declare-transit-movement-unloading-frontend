import sbt._
import play.core.PlayVersion

object AppDependencies {

  private val mongoVersion = "1.6.0"
  private val bootstrapVersion = "8.4.0"
  private val pekkoVersion = "1.0.2"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-30"                     % mongoVersion,
    "uk.gov.hmrc"          %% "play-conditional-form-mapping-play-30"  % "2.0.0",
    "uk.gov.hmrc"          %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc"          %% "play-allowlist-filter"                  % "1.2.0",
    "uk.gov.hmrc"          %% "play-nunjucks-viewmodel-play-30"        % "1.0.0",
    "org.webjars.npm"      %  "govuk-frontend"                         % "4.8.0",
    "uk.gov.hmrc.webjars"  %  "hmrc-frontend"                          % "5.60.0",
    "com.lucidchart"       %% "xtract"                                 % "2.3.0",
    "org.apache.pekko"     %% "pekko-actor"                            % pekkoVersion,
    "org.apache.pekko"     %% "pekko-stream"                           % pekkoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"         %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "uk.gov.hmrc"               %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.scalatest"             %% "scalatest"                % "3.2.17",
    "org.mockito"               %  "mockito-core"             % "5.2.0",
    "org.scalatestplus"         %% "mockito-4-11"             % "3.2.17.0",
    "org.scalacheck"            %% "scalacheck"               % "1.17.0",
    "org.scalatestplus"         %% "scalacheck-1-17"          % "3.2.17.0",
    "io.github.wolfendale"      %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.jsoup"                 %  "jsoup"                    % "1.15.4",
    "org.apache.pekko"          %% "pekko-testkit"            % pekkoVersion,
    "org.apache.pekko"          %% "pekko-stream-testkit"     % pekkoVersion
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test

  val overrides: Seq[ModuleID] = Seq(
    "org.apache.commons" % "commons-compress" % "1.25.0"
  )
}
