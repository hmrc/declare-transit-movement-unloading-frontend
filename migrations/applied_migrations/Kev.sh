#!/bin/bash

echo ""
echo "Applying migration Kev"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:arrivalId/kev                  controllers.KevController.onPageLoad(arrivalId: ArrivalId, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:arrivalId/kev                  controllers.KevController.onSubmit(arrivalId: ArrivalId, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:arrivalId/changeKev                        controllers.KevController.onPageLoad(arrivalId: ArrivalId, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:arrivalId/changeKev                        controllers.KevController.onSubmit(arrivalId: ArrivalId, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "kev.title = Kev" >> ../conf/messages.en
echo "kev.heading = Kev" >> ../conf/messages.en
echo "kev.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "kev.checkYourAnswersLabel = Kev" >> ../conf/messages.en
echo "kev.error.required.all = Enter the kev" >> ../conf/messages.en
echo "kev.error.required.two = The kev" must include {0} and {1} >> ../conf/messages.en
echo "kev.error.required = The kev must include {0}" >> ../conf/messages.en
echo "kev.error.invalid = Enter a real Kev" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryKevUserAnswersEntry: Arbitrary[(KevPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[KevPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryKevPage: Arbitrary[KevPage.type] =";\
    print "    Arbitrary(KevPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(KevPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def kev: Option[Row] = userAnswers.get(KevPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"kev.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Literal(answer.format(dateFormatter))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.KevController.onPageLoad(userAnswers.id, CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"kev.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration Kev completed"
