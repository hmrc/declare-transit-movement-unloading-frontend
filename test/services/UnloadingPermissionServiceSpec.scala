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

package services
import base.SpecBase
import connectors.UnloadingConnector
import generators.Generators
import models.{ArrivalId, MessagesLocation, MessagesSummary, Movement, MovementMessage, Seals, UnloadingPermission, UserAnswers}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import org.scalacheck.Arbitrary.arbitrary

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnloadingPermissionServiceSpec extends SpecBase with MustMatchers with Generators with MockitoSugar with ScalaFutures with IntegrationPatience {

  private val mockConnector = mock[UnloadingConnector]

  private val service = new UnloadingPermissionServiceImpl(mockConnector)

  override def beforeEach = {
    super.beforeEach()
    reset(mockConnector)
  }

//  import UnloadingPermissionServiceSpec._

  "UnloadingPermissionService" - {

    "getUnloadingPermission" - {

      "must return UnloadingPermission when IE0043 message exists" in {

        val unloadingPermissionMessage = arbitrary[UnloadingPermission].sample.value

        val messagesSummary =
          MessagesSummary(arrivalId, MessagesLocation(s"/movements/arrivals/${arrivalId.value}/messages/3", Some("/movements/arrivals/1234/messages/5")))

        when(mockConnector.getSummary(arrivalId)).thenReturn(Future.successful(Some(messagesSummary)))

        when(mockConnector.getUnloadingPermission(any())(any())).thenReturn(Future.successful(Some(unloadingPermissionMessage)))

        service.getUnloadingPermission(arrivalId).futureValue mustBe a[Some[_]]
      }

      "must return None when IE0043 message is not returned" in {

        val messagesSummary =
          MessagesSummary(arrivalId, MessagesLocation(s"/movements/arrivals/${arrivalId.value}/messages/3", Some("/movements/arrivals/1234/messages/5")))

        when(mockConnector.getSummary(arrivalId)).thenReturn(Future.successful(Some(messagesSummary)))

        when(mockConnector.getUnloadingPermission(any())(any())).thenReturn(Future.successful(None))

        service.getUnloadingPermission(arrivalId).futureValue mustBe None
      }

      "must return None when getSummary returns None" in {

        when(mockConnector.getSummary(arrivalId)).thenReturn(Future.successful(None))

        verify(mockConnector, times(0)).getUnloadingPermission(any())(any())

        service.getUnloadingPermission(arrivalId).futureValue mustBe None
      }

    }

    "convertSeals" - {

      "return the same userAnswers when given an ID with no Seals" in {

        val unloadingPermissionMessage = arbitrary[UnloadingPermission].sample.value

        val messagesSummary =
          MessagesSummary(arrivalId, MessagesLocation(s"/movements/arrivals/${arrivalId.value}/messages/3", Some("/movements/arrivals/1234/messages/5")))

        when(mockConnector.getSummary(arrivalId)).thenReturn(Future.successful(Some(messagesSummary)))

        when(mockConnector.getUnloadingPermission(any())(any()))
          .thenReturn(Future.successful(Some(unloadingPermissionMessage.copy(seals = None))))

        val userAnswers = UserAnswers(arrivalId, mrn, eoriNumber, Json.obj())
        service.convertSeals(userAnswers).futureValue mustBe Some(userAnswers)
      }

      "return updated userAnswers when given an ID with seals" in {

        val unloadingPermissionMessage = arbitrary[UnloadingPermission].sample.value

        val messagesSummary =
          MessagesSummary(arrivalId, MessagesLocation(s"/movements/arrivals/${arrivalId.value}/messages/3", Some("/movements/arrivals/1234/messages/5")))

        when(mockConnector.getSummary(arrivalId)).thenReturn(Future.successful(Some(messagesSummary)))

        when(mockConnector.getUnloadingPermission(any())(any()))
          .thenReturn(Future.successful(Some(unloadingPermissionMessage.copy(seals = Some(Seals(2, Seq("Seals01", "Seals02")))))))
        val userAnswers          = UserAnswers(arrivalId, mrn, eoriNumber, Json.obj())
        val userAnswersWithSeals = UserAnswers(arrivalId, mrn, eoriNumber, Json.obj("seals" -> Seq("Seals01", "Seals02")), userAnswers.lastUpdated)
        service.convertSeals(userAnswers).futureValue mustBe Some(userAnswersWithSeals)
      }
    }
  }
}

//object UnloadingPermissionServiceSpec {
//
//  val ie043MessageSeals = """<CC043A><SynIdeMES1>UNOC</SynIdeMES1>
//                            |<SynVerNumMES2>3</SynVerNumMES2>
//                            |<MesSenMES3>NTA.GB</MesSenMES3>
//                            |<MesRecMES6>SYST17B-NCTS_EU_EXIT</MesRecMES6>
//                            |<DatOfPreMES9>20190912</DatOfPreMES9>
//                            |<TimOfPreMES10>1448</TimOfPreMES10>
//                            |<IntConRefMES11>66390912144854</IntConRefMES11>
//                            |<AppRefMES14>NCTS</AppRefMES14>
//                            |<TesIndMES18>0</TesIndMES18>
//                            |<MesIdeMES19>66390912144854</MesIdeMES19>
//                            |<MesTypMES20>GB043A</MesTypMES20>
//                            |<HEAHEA><DocNumHEA5>19IT02110010007827</DocNumHEA5>
//                            |    <TypOfDecHEA24>T1</TypOfDecHEA24>
//                            |    <CouOfDesCodHEA30>GB</CouOfDesCodHEA30>
//                            |    <CouOfDisCodHEA55>IT</CouOfDisCodHEA55>
//                            |    <IdeOfMeaOfTraAtDHEA78>abcd</IdeOfMeaOfTraAtDHEA78>
//                            |    <NatOfMeaOfTraAtDHEA80>IT</NatOfMeaOfTraAtDHEA80>
//                            |    <ConIndHEA96>0</ConIndHEA96>
//                            |    <AccDatHEA158>20190912</AccDatHEA158>
//                            |    <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
//                            |    <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
//                            |    <TotGroMasHEA307>1000</TotGroMasHEA307>
//                            |</HEAHEA>
//                            |<TRAPRIPC1><NamPC17>Mancini Carriers</NamPC17>
//                            |    <StrAndNumPC122>90 Desio Way</StrAndNumPC122>
//                            |    <PosCodPC123>MOD 5JJ</PosCodPC123>
//                            |    <CitPC124>Modena</CitPC124>
//                            |    <CouPC125>IT</CouPC125>
//                            |    <TINPC159>IT444100201000</TINPC159>
//                            |</TRAPRIPC1>
//                            |<TRACONCO1><NamCO17>Mancini Carriers</NamCO17>
//                            |    <StrAndNumCO122>90 Desio Way</StrAndNumCO122>
//                            |    <PosCodCO123>MOD 5JJ</PosCodCO123>
//                            |    <CitCO124>Modena</CitCO124>
//                            |    <CouCO125>IT</CouCO125>
//                            |    <TINCO159>IT444100201000</TINCO159>
//                            |</TRACONCO1>
//                            |<TRACONCE1><NamCE17>Mancini Carriers</NamCE17>
//                            |    <StrAndNumCE122>90 Desio Way</StrAndNumCE122>
//                            |    <PosCodCE123>MOD 5JJ</PosCodCE123>
//                            |    <CitCE124>Modena</CitCE124>
//                            |    <CouCE125>IT</CouCE125>
//                            |    <TINCE159>IT444100201000</TINCE159>
//                            |</TRACONCE1>
//                            |<TRADESTRD><NamTRD7>The Luggage Carriers</NamTRD7>
//                            |    <StrAndNumTRD22>225 Suedopolish Yard,</StrAndNumTRD22>
//                            |    <PosCodTRD23>SS8 2BB</PosCodTRD23>
//                            |    <CitTRD24>,</CitTRD24>
//                            |    <CouTRD25>GB</CouTRD25>
//                            |    <TINTRD59>GB163910077000</TINTRD59>
//                            |</TRADESTRD>
//                            |<CUSOFFDEPEPT><RefNumEPT1>IT021100</RefNumEPT1>
//                            |</CUSOFFDEPEPT>
//                            |<CUSOFFPREOFFRES><RefNumRES1>GB000060</RefNumRES1>
//                            |</CUSOFFPREOFFRES>
//                            |<SEAINFSLI><SeaNumSLI2>2</SeaNumSLI2>
//                            |    <SEAIDSID><SeaIdeSID1>Seals01</SeaIdeSID1></SEAIDSID>
//                            |    <SEAIDSID><SeaIdeSID1>Seals02</SeaIdeSID1></SEAIDSID>
//                            |</SEAINFSLI>
//                            |<GOOITEGDS><IteNumGDS7>1</IteNumGDS7>
//                            |    <GooDesGDS23>Flowers</GooDesGDS23>
//                            |    <GroMasGDS46>1000</GroMasGDS46>
//                            |    <NetMasGDS48>999</NetMasGDS48>
//                            |    <PRODOCDC2><DocTypDC21>235</DocTypDC21>
//                            |        <DocRefDC23>Ref.</DocRefDC23>
//                            |    </PRODOCDC2>
//                            |    <CONNR2>
//                            |        <ConNumNR21>container 1</ConNumNR21>
//                            |    </CONNR2>
//                            |    <CONNR2>
//                            |        <ConNumNR21>container 2</ConNumNR21>
//                            |    </CONNR2>
//                            |    <PACGS2>
//                            |        <MarNumOfPacGS21>Ref.</MarNumOfPacGS21>
//                            |        <KinOfPacGS23>BX</KinOfPacGS23>
//                            |        <NumOfPacGS24>1</NumOfPacGS24>
//                            |    </PACGS2>
//                            |    <SGICODSD2>
//                            |        <SenGooCodSD22>1</SenGooCodSD22>
//                            |        <SenQuaSD23>1</SenQuaSD23>
//                            |    </SGICODSD2>
//                            |</GOOITEGDS>
//                            |</CC043A>""".stripMargin
//
//  val ie043Message = """<CC043A><SynIdeMES1>UNOC</SynIdeMES1>
//               |    <SynVerNumMES2>3</SynVerNumMES2>
//               |    <MesSenMES3>NTA.GB</MesSenMES3>
//               |    <MesRecMES6>SYST17B-NCTS_EU_EXIT</MesRecMES6>
//               |    <DatOfPreMES9>20190912</DatOfPreMES9>
//               |    <TimOfPreMES10>1448</TimOfPreMES10>
//               |    <IntConRefMES11>66390912144854</IntConRefMES11>
//               |    <AppRefMES14>NCTS</AppRefMES14>
//               |    <TesIndMES18>0</TesIndMES18>
//               |    <MesIdeMES19>66390912144854</MesIdeMES19>
//               |    <MesTypMES20>GB043A</MesTypMES20>
//               |    <HEAHEA><DocNumHEA5>19IT02110010007827</DocNumHEA5>
//               |        <TypOfDecHEA24>T1</TypOfDecHEA24>
//               |        <CouOfDesCodHEA30>GB</CouOfDesCodHEA30>
//               |        <CouOfDisCodHEA55>IT</CouOfDisCodHEA55>
//               |        <ConIndHEA96>0</ConIndHEA96>
//               |        <AccDatHEA158>20190912</AccDatHEA158>
//               |        <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
//               |        <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
//               |        <TotGroMasHEA307>1000</TotGroMasHEA307>
//               |    </HEAHEA>
//               |    <TRAPRIPC1><NamPC17>Mancini Carriers</NamPC17>
//               |        <StrAndNumPC122>90 Desio Way</StrAndNumPC122>
//               |        <PosCodPC123>MOD 5JJ</PosCodPC123>
//               |        <CitPC124>Modena</CitPC124>
//               |        <CouPC125>IT</CouPC125>
//               |        <TINPC159>IT444100201000</TINPC159>
//               |    </TRAPRIPC1>
//               |    <TRACONCO1><NamCO17>Mancini Carriers</NamCO17>
//               |        <StrAndNumCO122>90 Desio Way</StrAndNumCO122>
//               |        <PosCodCO123>MOD 5JJ</PosCodCO123>
//               |        <CitCO124>Modena</CitCO124>
//               |        <CouCO125>IT</CouCO125>
//               |        <TINCO159>IT444100201000</TINCO159>
//               |    </TRACONCO1>
//               |    <TRACONCE1><NamCE17>Mancini Carriers</NamCE17>
//               |        <StrAndNumCE122>90 Desio Way</StrAndNumCE122>
//               |        <PosCodCE123>MOD 5JJ</PosCodCE123>
//               |        <CitCE124>Modena</CitCE124>
//               |        <CouCE125>IT</CouCE125>
//               |        <TINCE159>IT444100201000</TINCE159>
//               |    </TRACONCE1>
//               |    <TRADESTRD><NamTRD7>The Luggage Carriers</NamTRD7>
//               |        <StrAndNumTRD22>225 Suedopolish Yard,</StrAndNumTRD22>
//               |        <PosCodTRD23>SS8 2BB</PosCodTRD23>
//               |        <CitTRD24>,</CitTRD24>
//               |        <CouTRD25>GB</CouTRD25>
//               |    </TRADESTRD>
//               |    <CUSOFFDEPEPT><RefNumEPT1>IT021100</RefNumEPT1>
//               |    </CUSOFFDEPEPT>
//               |    <CUSOFFPREOFFRES><RefNumRES1>GB000060</RefNumRES1>
//               |    </CUSOFFPREOFFRES>
//               |    <GOOITEGDS><IteNumGDS7>1</IteNumGDS7>
//               |        <GooDesGDS23>Flowers</GooDesGDS23>
//               |        <GroMasGDS46>1000</GroMasGDS46>
//               |        <NetMasGDS48>999</NetMasGDS48>
//               |        <PRODOCDC2><DocTypDC21>235</DocTypDC21>
//               |            <DocRefDC23>Ref.</DocRefDC23>
//               |        </PRODOCDC2>
//               |        <PACGS2>
//               |            <MarNumOfPacGS21>Ref.</MarNumOfPacGS21>
//               |            <KinOfPacGS23>BX</KinOfPacGS23>
//               |            <NumOfPacGS24>1</NumOfPacGS24>
//               |        </PACGS2>
//               |    </GOOITEGDS>
//               |</CC043A>
//               |""".stripMargin
//
//  val goodsReleasedMessage = """<CC025A><SynIdeMES1>UNOC</SynIdeMES1>
//               |    <SynVerNumMES2>3</SynVerNumMES2>
//               |    <MesRecMES6>SYST17B-NCTS_EU_EXIT</MesRecMES6>
//               |    <DatOfPreMES9>20190912</DatOfPreMES9>
//               |    <TimOfPreMES10>1510</TimOfPreMES10>
//               |    <IntConRefMES11>70390912151020</IntConRefMES11>
//               |    <AppRefMES14>NCTS</AppRefMES14>
//               |    <TesIndMES18>0</TesIndMES18>
//               |    <MesIdeMES19>70390912151020</MesIdeMES19>
//               |    <MesTypMES20>GB025A</MesTypMES20>
//               |    <HEAHEA><DocNumHEA5>19IT02110010007827</DocNumHEA5>
//               |      <GooRelDatHEA176>20190912</GooRelDatHEA176>
//               |    </HEAHEA>
//               |    <TRADESTRD><NamTRD7>The Luggage Carriers</NamTRD7>
//               |      <StrAndNumTRD22>225 Suedopolish Yard,</StrAndNumTRD22>
//               |      <PosCodTRD23>SS8 2BB</PosCodTRD23>
//               |      <CitTRD24>,</CitTRD24>
//               |      <CouTRD25>GB</CouTRD25>
//               |      <TINTRD59>GB163910077000</TINTRD59>
//               |    </TRADESTRD>
//               |    <CUSOFFPREOFFRES><RefNumRES1>GB000060</RefNumRES1>
//               |    </CUSOFFPREOFFRES>
//               |  </CC025A>""".stripMargin
//}
