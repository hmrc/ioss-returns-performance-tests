/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.returns

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object ReturnsRequests extends ServicesConfiguration {

  val baseUrl: String  = baseUrlFor("ioss-returns-frontend")
  val route: String    = "/pay-vat-on-goods-sold-to-eu/import-one-stop-shop-returns-payments"
  val homePage: String = s"$baseUrl$route/your-account"
  val fullUrl: String  = baseUrl + route

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def goToAuthLoginPage =
    http("Go to Auth login page")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .check(status.in(200, 303))

  def upFrontAuthLogin =
    http("Enter Auth login credentials ")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("redirectionUrl", route)
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "${vrn}")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-IOSS-ORG")
      .formParam("enrolment[1].taxIdentifier[0].name", "IOSSNumber")
      .formParam("enrolment[1].taxIdentifier[0].value", "IM9001234567")
      .formParam("enrolment[1].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def getHomePage =
    http("Get Home Page")
      .get(homePage)
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))

  def getStartReturn =
    http("Get Start Return page")
      .get(fullUrl + "/2023-M12/start")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postStartReturn =
    http("Post Start Returns")
      .post(fullUrl + "/2023-M12/start")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/soldGoods"))

  def getSoldGoods =
    http("Get Sold Goods page")
      .get(fullUrl + "/soldGoods")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSoldGoods =
    http("Post Sold Goods")
      .post(fullUrl + "/soldGoods")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/soldToCountry/1"))

  def getSoldToCountry(index: String) =
    http("Get Sold To Country page")
      .get(fullUrl + s"/soldToCountry/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSoldToCountry(index: String, countryCode: String) =
    http("Post Sold To Country")
      .post(fullUrl + s"/soldToCountry/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/vatRatesFromCountry/$index"))

  def getVatRatesFromCountry(index: String) =
    http("Get Vat Rates From Country page")
      .get(fullUrl + s"/vatRatesFromCountry/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRatesFromCountry(index: String, vatRate: String) =
    http("Post Vat Rates From Country")
      .post(fullUrl + s"/vatRatesFromCountry/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value[0]", vatRate)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/sales-to-country/$index/1"))

  def getSalesToCountry(countryIndex: String, vatRatesIndex: String) =
    http("Get Sales To Country page")
      .get(fullUrl + s"/sales-to-country/$countryIndex/$vatRatesIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSalesToCountry(countryIndex: String, vatRatesIndex: String, amount: String) =
    http("Post Sales To Country")
      .post(fullUrl + s"/sales-to-country/$countryIndex/$vatRatesIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", amount)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/vatOnSales/$countryIndex/$vatRatesIndex"))

  def getVatOnSales(countryIndex: String, vatRatesIndex: String) =
    http("Get VAT on Sales page")
      .get(fullUrl + s"/vatOnSales/$countryIndex/$vatRatesIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatOnSales(countryIndex: String, vatRatesIndex: String) =
    http("Post VAT on Sales")
      .post(fullUrl + s"/vatOnSales/$countryIndex/$vatRatesIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choice", "option1")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/check-sales/$countryIndex"))

  def getCheckSales(countryIndex: String) =
    http("Get Check Sales page")
      .get(fullUrl + s"/check-sales/$countryIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckSales(countryIndex: String) =
    http("Post Check Sales")
      .post(fullUrl + s"/check-sales/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/add-sales-country-list"))

  def getAddSalesCountryList =
    http("Get Add Sales Country List page")
      .get(fullUrl + s"/add-sales-country-list")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAddSalesCountryList(answer: Boolean) =
    http("Post Add Sales To EU")
      .post(s"$baseUrl$route/add-sales-country-list")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAddSalesCountryList(answer: Boolean, index: Option[String]) =
    if (answer) {
      testAddSalesCountryList(answer)
        .check(header("Location").is(s"$route/soldToCountry/${index.get}"))
    } else {
      testAddSalesCountryList(answer)
//        Temporarily adding Correction page until logic is in to check if it is the first return
//        .check(header("Location").is(s"$route/check-your-answers"))
        .check(header("Location").is(s"$route/correct-previous-return"))
    }

  def getCorrectPreviousReturn =
    http("Get Correct Previous Return page")
      .get(fullUrl + "/correct-previous-return")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testCorrectPreviousCountry(answer: Boolean) =
    http("Post Correct Previous Country")
      .post(s"$baseUrl$route/correct-previous-return")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postCorrectPreviousReturn(answer: Boolean) =
    if (answer) {
      testCorrectPreviousCountry(answer)
        .check(header("Location").is(s"$route/correction-return-year/1"))
    } else {
      testCorrectPreviousCountry(answer)
        .check(header("Location").is(s"$route/check-your-answers"))
    }

  def getCorrectionYear =
    http("Get Correction Year page")
      .get(fullUrl + "/correction-return-year/1")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionYear =
    http("Post Correction Year")
      .post(fullUrl + "/correction-return-year/1")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "2023")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/correction-return-period/1"))

  def getCorrectionMonth =
    http("Get Correction Month page")
      .get(fullUrl + "/correction-return-period/1")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionMonth =
    http("Post Correction Month")
      .post(fullUrl + "/correction-return-period/1")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "2023-M10")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/correction-country/1/1"))

  def getCorrectionCountry(countryIndex: String, correctionIndex: String) =
    http("Get Correction Country page")
      .get(fullUrl + s"/correction-country/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionCountry(countryCode: String, countryIndex: String, correctionIndex: String) =
    http("Post Correction Country")
      .post(fullUrl + s"/correction-country/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/add-new-country/$correctionIndex/$countryIndex"))

  def getAddNewCountry(countryIndex: String, correctionIndex: String) =
    http("Get Add New Country page")
      .get(fullUrl + s"/add-new-country/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddNewCountry(countryIndex: String, correctionIndex: String) =
    http("Post Add New Country")
      .post(fullUrl + s"/add-new-country/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/country-vat-correction-amount/$correctionIndex/$countryIndex"))

  def getCountryVatCorrectionAmount(countryIndex: String, correctionIndex: String) =
    http("Get Country Vat Correction Amount page")
      .get(fullUrl + s"/country-vat-correction-amount/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCountryVatCorrectionAmount(amount: String, countryIndex: String, correctionIndex: String) =
    http("Post Country Vat Correction Amount")
      .post(fullUrl + s"/country-vat-correction-amount/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", amount)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/vat-payable-confirm/$correctionIndex/$countryIndex"))

  def getVatPayableConfirm(countryIndex: String, correctionIndex: String) =
    http("Get Vat Payable Confirm page")
      .get(fullUrl + s"/vat-payable-confirm/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatPayableConfirm(countryIndex: String, correctionIndex: String) =
    http("Post Vat Payable Confirm")
      .post(fullUrl + s"/vat-payable-confirm/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/correction-list-countries/$correctionIndex"))

  def getCorrectionCountriesList(correctionIndex: String) =
    http("Get Correction Countries List page")
      .get(fullUrl + s"/correction-list-countries/$correctionIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionCountriesList(correctionIndex: String) =
    http("Post Correction Countries List")
      .post(fullUrl + s"/correction-list-countries/$correctionIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/2023-M12/vat-correction-periods-add"))

  def getCorrectionPeriods() =
    http("Get Correction Periods page")
      .get(fullUrl + s"/2023-M12/vat-correction-periods-add")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionPeriods() =
    http("Post Correction Periods")
      .post(fullUrl + s"/2023-M12/vat-correction-periods-add")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/check-your-answers"))

  def getCheckYourAnswers =
    http("Get Check Your Answers page")
      .get(fullUrl + s"/check-your-answers")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckYourAnswers =
    http("Post Check Your Answers page")
      .post(fullUrl + s"/check-your-answers?incompletePromptShown=false")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))

  def getReturnSubmitted =
    http("Get Return Submitted page")
      .get(fullUrl + s"/successfully-submitted")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))

}
