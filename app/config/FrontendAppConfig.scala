/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {

  private val contactHost                  = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "play26frontend"

  val analyticsToken: String         = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String          = configuration.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl       = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl         = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  val betaFeedbackUrl                = s"$contactHost/contact/beta-feedback"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val loginUrl: String               = configuration.get[String]("microservice.services.auth.login")
  lazy val loginContinueUrl: String       = configuration.get[String]("microservice.services.auth.loginContinue")
  lazy val enrolmentKey: String           = configuration.get[String]("microservice.services.auth.enrolmentKey")
  lazy val enrolmentIdentifierKey: String = configuration.get[String]("microservice.services.auth.enrolmentIdentifierKey")

  private val manageTransitMovementsHost = configuration.get[String]("manage-transit-movements-frontend.host")
  val manageTransitMovementsUrl          = s"$manageTransitMovementsHost/manage-transit-movements/"

  lazy val referenceDataUrl: String = configuration.get[Service]("microservice.services.reference-data").baseUrl ++
    configuration.get[String]("microservice.services.reference-data.uri")

  lazy val arrivalsBackend: String = configuration.get[Service]("microservice.services.arrivals-backend").baseUrl ++
    configuration.get[String]("microservice.services.arrivals-backend.uri")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
