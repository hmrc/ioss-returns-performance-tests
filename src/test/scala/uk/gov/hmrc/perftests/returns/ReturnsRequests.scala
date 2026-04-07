/*
 * Copyright 2026 HM Revenue & Customs
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
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration
import uk.gov.hmrc.perftests.returns.Utils.FileUploadChecks._

import java.time.LocalDate
import scala.concurrent.duration.DurationInt

object GatlingCompat {
  implicit def chainToAction(chain: ChainBuilder): ActionBuilder =
    chain.actionBuilders.head
}

object ReturnsRequests extends ServicesConfiguration {

  val baseUrl: String  = baseUrlFor("ioss-returns-frontend")
  val route: String    = "/pay-vat-on-goods-sold-to-eu/import-one-stop-shop-returns-payments"
  val homePage: String = s"$baseUrl$route/your-account"
  val fullUrl: String  = baseUrl + route
  val intermediaryUrl  = s"$baseUrl$route/start-return-as-intermediary/IM9001144771"

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def pause(duration: Int = 3): ChainBuilder =
    io.gatling.core.Predef.pause(duration.seconds)

  def goToAuthLoginPage: HttpRequestBuilder =
    http("Go to Auth login page")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200, 303))

  def upFrontAuthLogin(iossNumber: String): HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "#{csrfToken}")
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
      .formParam("enrolment[0].taxIdentifier[0].value", "#{vrn}")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-IOSS-ORG")
      .formParam("enrolment[1].taxIdentifier[0].name", "IOSSNumber")
      .formParam("enrolment[1].taxIdentifier[0].value", iossNumber)
      .formParam("enrolment[1].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def upFrontAuthLoginMultipleIOSSNumbers: HttpRequestBuilder =
    http("Enter Auth login credentials for multiple IOSS Numbers")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "#{csrfToken}")
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
      .formParam("enrolment[0].taxIdentifier[0].value", "#{vrn}")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-IOSS-ORG")
      .formParam("enrolment[1].taxIdentifier[0].name", "IOSSNumber")
      .formParam("enrolment[1].taxIdentifier[0].value", "IM9007230003")
      .formParam("enrolment[1].state", "Activated")
      .formParam("enrolment[2].name", "HMRC-IOSS-ORG")
      .formParam("enrolment[2].taxIdentifier[0].name", "IOSSNumber")
      .formParam("enrolment[2].taxIdentifier[0].value", "IM9007230002")
      .formParam("enrolment[2].state", "Activated")
      .formParam("enrolment[3].name", "HMRC-IOSS-ORG")
      .formParam("enrolment[3].taxIdentifier[0].name", "IOSSNumber")
      .formParam("enrolment[3].taxIdentifier[0].value", "IM9007230001")
      .formParam("enrolment[3].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def upFrontAuthLoginIntermediary: HttpRequestBuilder =
    http("Enter Auth login credentials for intermediary")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("redirectionUrl", intermediaryUrl)
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "#{vrn}")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-IOSS-INT")
      .formParam("enrolment[1].taxIdentifier[0].name", "IntNumber")
      .formParam("enrolment[1].taxIdentifier[0].value", "IN9001234567")
      .formParam("enrolment[1].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def getHomePage: HttpRequestBuilder =
    http("Get Home Page")
      .get(homePage)
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200))

  def getIntermediaryStart(iossNumber: String): HttpRequestBuilder =
    http("Get Intermediary endpoint")
      .get(intermediaryUrl)
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/2025-M3/start-return"))

  def getStartReturn(iossNumber: String): HttpRequestBuilder =
    http("Get Start Return page")
      .get(fullUrl + s"/$iossNumber/2023-M12/start-return")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def getStartReturnIntermediary(iossNumber: String): HttpRequestBuilder =
    http("Get Start Return page for intermediary")
      .get(fullUrl + s"/$iossNumber/2025-M3/start-return")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postStartReturn(iossNumber: String): HttpRequestBuilder =
    http("Post Start Returns")
      .post(fullUrl + s"/$iossNumber/2023-M12/start-return")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/want-to-upload-file"))

  def postStartReturnIntermediary(iossNumber: String): HttpRequestBuilder =
    http("Post Start Returns for intermediary")
      .post(fullUrl + s"/$iossNumber/2025-M3/start-return")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/want-to-upload-file"))

  def getWantToUploadFile(iossNumber: String): HttpRequestBuilder =
    http("Get Want To Upload File page")
      .get(fullUrl + s"/$iossNumber/want-to-upload-file")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testWantToUploadFile(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    http("Post Want To Upload File")
      .post(fullUrl + s"/$iossNumber/want-to-upload-file")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postWantToUploadFile(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    if (answer) {
      testWantToUploadFile(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/file-upload"))
    } else {
      testWantToUploadFile(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/sold-goods"))
    }

  def getFileUpload(iossNumber: String): HttpRequestBuilder =
    http("Get File Upload page")
      .get(fullUrl + s"/$iossNumber/file-upload")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200))
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanInitiateResponse)
      .check(saveUpscanInitiateReceived)
      .check(saveAmazonMetaOriginalFileName)
      .check(saveAMZMetaRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAcl)
      .check(saveAMZMetaSessionId)
      .check(saveConsumingService)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(savePolicy)

  def postFileUpload(iossNumber: String): HttpRequestBuilder =
    http("Post File Upload page")
      .post(s => s("fileUploadAmazonUrl").as[String])
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "#{callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "#{amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "#{successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "#{amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "#{upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "#{upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "#{amazonMetaRequestID}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "#{amazonMetaOriginalFileName}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "#{amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "#{key}"))
      .bodyPart(StringBodyPart("acl", "#{acl}"))
      .bodyPart(StringBodyPart("x-amz-signature", "#{amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "#{errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "#{amazonMetaSessionID}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "#{consumingService}"))
      .bodyPart(StringBodyPart("policy", "#{policy}"))
      .bodyPart(RawFileBodyPart("file", "data/fileUpload.csv"))
      .check(status.in(200, 303))
      .check(currentLocation.saveAs("bulkUploadSuccessUrl"))
      .check(header("Location").transform(_.contains(s"$route/$iossNumber/file-uploaded")).is(true))
      .check(header("Location").saveAs("fileUpload"))

  def getFileUploaded: HttpRequestBuilder =
    http("Get File Uploaded page")
      .get("#{fileUpload}")
      .check(status.in(200))

  def postFileUploaded(iossNumber: String): HttpRequestBuilder =
    http("Post File Uploaded page")
      .post("#{fileUpload}")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/correct-previous-return"))

  def getSoldGoods(iossNumber: String): HttpRequestBuilder =
    http("Get Sold Goods page")
      .get(fullUrl + s"/$iossNumber/sold-goods")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testSoldGoods(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    http("Post Add Sales To EU")
      .post(fullUrl + s"/$iossNumber/sold-goods")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postSoldGoods(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    if (answer) {
      testSoldGoods(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/sold-to-country/1"))
    } else {
      testSoldGoods(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/correct-previous-return"))
    }

  def getSoldToCountry(iossNumber: String, index: String): HttpRequestBuilder =
    http("Get Sold To Country page")
      .get(fullUrl + s"/$iossNumber/sold-to-country/$index")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSoldToCountry(iossNumber: String, index: String, countryCode: String): HttpRequestBuilder =
    http("Post Sold To Country")
      .post(fullUrl + s"/$iossNumber/sold-to-country/$index")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/vat-rates-from-country/$index"))

  def getVatRatesFromCountry(iossNumber: String, index: String): HttpRequestBuilder =
    http("Get Vat Rates From Country page")
      .get(fullUrl + s"/$iossNumber/vat-rates-from-country/$index")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRatesFromCountry(iossNumber: String, index: String, vatRate: String): HttpRequestBuilder =
    http("Post Vat Rates From Country")
      .post(fullUrl + s"/$iossNumber/vat-rates-from-country/$index")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value[0]", vatRate)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/sales-to-country/$index/1"))

  def getSalesToCountry(iossNumber: String, countryIndex: String, vatRatesIndex: String): HttpRequestBuilder =
    http("Get Sales To Country page")
      .get(fullUrl + s"/$iossNumber/sales-to-country/$countryIndex/$vatRatesIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSalesToCountry(
    iossNumber: String,
    countryIndex: String,
    vatRatesIndex: String,
    amount: String
  ): HttpRequestBuilder =
    http("Post Sales To Country")
      .post(fullUrl + s"/$iossNumber/sales-to-country/$countryIndex/$vatRatesIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", amount)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/vat-on-sales/$countryIndex/$vatRatesIndex"))

  def getVatOnSales(iossNumber: String, countryIndex: String, vatRatesIndex: String): HttpRequestBuilder =
    http("Get VAT on Sales page")
      .get(fullUrl + s"/$iossNumber/vat-on-sales/$countryIndex/$vatRatesIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatOnSales(iossNumber: String, countryIndex: String, vatRatesIndex: String): HttpRequestBuilder =
    http("Post VAT on Sales")
      .post(fullUrl + s"/$iossNumber/vat-on-sales/$countryIndex/$vatRatesIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choice", "option1")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/check-sales/$countryIndex"))

  def getCheckSales(iossNumber: String, countryIndex: String): HttpRequestBuilder =
    http("Get Check Sales page")
      .get(fullUrl + s"/$iossNumber/check-sales/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckSales(iossNumber: String, countryIndex: String): HttpRequestBuilder =
    http("Post Check Sales")
      .post(fullUrl + s"/$iossNumber/check-sales/$countryIndex?incompletePromptShown=false")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/add-sales-country-list"))

  def getAddSalesCountryList(iossNumber: String): HttpRequestBuilder =
    http("Get Add Sales Country List page")
      .get(fullUrl + s"/$iossNumber/add-sales-country-list")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAddSalesCountryList(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    http("Post Add Sales Country List page")
      .post(s"$baseUrl$route/$iossNumber/add-sales-country-list?incompletePromptShown=false")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAddSalesCountryList(iossNumber: String, answer: Boolean, index: Option[String]): HttpRequestBuilder =
    if (answer) {
      testAddSalesCountryList(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/sold-to-country/${index.get}"))
    } else {
      testAddSalesCountryList(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/correct-previous-return"))
    }

  def getCorrectPreviousReturn(iossNumber: String): HttpRequestBuilder =
    http("Get Correct Previous Return page")
      .get(fullUrl + s"/$iossNumber/correct-previous-return")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testCorrectPreviousCountry(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    http("Post Correct Previous Country")
      .post(s"$baseUrl$route/$iossNumber/correct-previous-return")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postCorrectPreviousReturn(iossNumber: String, answer: Boolean): HttpRequestBuilder =
    if (answer) {
      testCorrectPreviousCountry(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/correction-return-year/1"))
    } else {
      testCorrectPreviousCountry(iossNumber, answer)
        .check(header("Location").is(s"$route/$iossNumber/check-your-answers"))
    }

  def getCorrectionYear(iossNumber: String): HttpRequestBuilder =
    http("Get Correction Year page")
      .get(fullUrl + s"/$iossNumber/correction-return-year/1")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionYear(iossNumber: String): HttpRequestBuilder =
    http("Post Correction Year")
      .post(fullUrl + s"/$iossNumber/correction-return-year/1")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "2023")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/correction-return-month/1"))

  def getCorrectionMonth(iossNumber: String): HttpRequestBuilder =
    http("Get Correction Month page")
      .get(fullUrl + s"/$iossNumber/correction-return-month/1")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionMonth(iossNumber: String): HttpRequestBuilder =
    http("Post Correction Month")
      .post(fullUrl + s"/$iossNumber/correction-return-month/1")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "2023-M10")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/correction-country/1/1"))

  def getCorrectionCountry(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Get Correction Country page")
      .get(fullUrl + s"/$iossNumber/correction-country/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionCountry(
    iossNumber: String,
    countryCode: String,
    countryIndex: String,
    correctionIndex: String
  ): HttpRequestBuilder =
    http("Post Correction Country")
      .post(fullUrl + s"/$iossNumber/correction-country/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/add-new-country/$correctionIndex/$countryIndex"))

  def getAddNewCountry(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Get Add New Country page")
      .get(fullUrl + s"/$iossNumber/add-new-country/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddNewCountry(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Post Add New Country")
      .post(fullUrl + s"/$iossNumber/add-new-country/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/country-vat-correction-amount/$correctionIndex/$countryIndex"))

  def getCountryVatCorrectionAmount(
    iossNumber: String,
    countryIndex: String,
    correctionIndex: String
  ): HttpRequestBuilder =
    http("Get Country Vat Correction Amount page")
      .get(fullUrl + s"/$iossNumber/country-vat-correction-amount/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCountryVatCorrectionAmount(
    iossNumber: String,
    amount: String,
    countryIndex: String,
    correctionIndex: String
  ): HttpRequestBuilder =
    http("Post Country Vat Correction Amount")
      .post(fullUrl + s"/$iossNumber/country-vat-correction-amount/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", amount)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/vat-payable-confirm/$correctionIndex/$countryIndex"))

  def getVatPayableConfirm(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Get Vat Payable Confirm page")
      .get(fullUrl + s"/$iossNumber/vat-payable-confirm/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatPayableConfirm(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Post Vat Payable Confirm")
      .post(fullUrl + s"/$iossNumber/vat-payable-confirm/$correctionIndex/$countryIndex")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", true)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/vat-payable-check/$correctionIndex/$countryIndex"))

  def getVatPayableCheck(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Get Vat Payable Check page")
      .get(fullUrl + s"/$iossNumber/vat-payable-check/$correctionIndex/$countryIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatPayableCheck(iossNumber: String, countryIndex: String, correctionIndex: String): HttpRequestBuilder =
    http("Post Vat Payable Check")
      .post(fullUrl + s"/$iossNumber/vat-payable-check/$correctionIndex/$countryIndex?incompletePromptShown=false")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/correction-list-countries/$correctionIndex"))

  def getCorrectionCountriesList(iossNumber: String, correctionIndex: String): HttpRequestBuilder =
    http("Get Correction Countries List page")
      .get(fullUrl + s"/$iossNumber/correction-list-countries/$correctionIndex")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionCountriesList(iossNumber: String, correctionIndex: String): HttpRequestBuilder =
    http("Post Correction Countries List")
      .post(fullUrl + s"/$iossNumber/correction-list-countries/$correctionIndex?incompletePromptShown=false")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/2023-M12/vat-correction-months-add"))

  def getCorrectionPeriods(): HttpRequestBuilder =
    http("Get Correction Periods page")
      .get(fullUrl + s"/2023-M12/vat-correction-months-add")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCorrectionPeriods(): HttpRequestBuilder =
    http("Post Correction Periods")
      .post(fullUrl + s"/2023-M12/vat-correction-months-add")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", false)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/check-your-answers"))

  def getCheckYourAnswers(iossNumber: String): HttpRequestBuilder =
    http("Get Check Your Answers page")
      .get(fullUrl + s"/$iossNumber/check-your-answers")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckYourAnswers(iossNumber: String): HttpRequestBuilder =
    http("Post Check Your Answers page")
      .post(fullUrl + s"/$iossNumber/check-your-answers?incompletePromptShown=false")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.in(200, 303))

  def getReturnSubmitted(iossNumber: String): HttpRequestBuilder =
    http("Get Return Submitted page")
      .get(fullUrl + s"/$iossNumber/return-successfully-submitted")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200))

  def getPastReturns(iossNumber: String) =
    http("Get Past Returns page")
      .get(fullUrl + s"/$iossNumber/past-returns")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200))

  def getReturnRegistrationSelection(iossNumber: String) =
    http("Get Return Registration Selection page")
      .get(fullUrl + s"/$iossNumber/return-registration-selection")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postReturnRegistrationSelection(iossNumber: String, selection: String) =
    http("Answer Return Registration Selection Page")
      .post(fullUrl + s"/$iossNumber/return-registration-selection")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", selection)
      .check(status.in(200, 303))
      .check(header("Location").is(s"$route/$iossNumber/view-returns-multiple-reg"))

  def testPastReturnsPreviousRegistration(period: String, iossNumber: String) =
    http("Get Past Returns Previous Registration page")
      .get(fullUrl + s"/past-returns/$period/$iossNumber")
      .header("Cookie", "mdtp=#{mdtpCookie}")
      .check(status.in(200))

  def getPastReturnsPreviousRegistration(month: Int, iossNumber: String) = {
    val returnMonth  = LocalDate.now().minusMonths(month).getMonthValue
    val returnYear   = LocalDate.now().minusMonths(month).getYear
    val periodString = s"$returnYear-M$returnMonth"
    testPastReturnsPreviousRegistration(periodString, iossNumber)
  }
}
