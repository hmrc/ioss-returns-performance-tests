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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.returns.ReturnsRequests._
import uk.gov.hmrc.perftests.returns.GatlingCompat.chainToAction

class ReturnsSimulation extends PerformanceTestRunner {

  val returnsBaseUrl: String = baseUrlFor("ioss-returns-frontend")

  setup("returns", "Returns Journey") withRequests (
    goToAuthLoginPage,
    upFrontAuthLogin("IM9001478521"),
    getHomePage,
    getStartReturn("IM9001478521"),
    postStartReturn("IM9001478521"),
    getWantToUploadFile("IM9001478521"),
    postWantToUploadFile("IM9001478521", answer = false),
    getSoldGoods("IM9001478521"),
    postSoldGoods("IM9001478521", answer = true),
    getSoldToCountry("IM9001478521", "1"),
    postSoldToCountry("IM9001478521", "1", "AT"),
    getVatRatesFromCountry("IM9001478521", "1"),
    postVatRatesFromCountry("IM9001478521", "1", "20"),
    getSalesToCountry("IM9001478521", "1", "1"),
    postSalesToCountry("IM9001478521", "1", "1", "5555"),
    getVatOnSales("IM9001478521", "1", "1"),
    postVatOnSales("IM9001478521", "1", "1"),
    getCheckSales("IM9001478521", "1"),
    postCheckSales("IM9001478521", "1"),
    getAddSalesCountryList("IM9001478521"),
    postAddSalesCountryList("IM9001478521", answer = true, Some("2")),
    getSoldToCountry("IM9001478521", "2"),
    postSoldToCountry("IM9001478521", "2", "FR"),
    getVatRatesFromCountry("IM9001478521", "2"),
    postVatRatesFromCountry("IM9001478521", "2", "20"),
    getSalesToCountry("IM9001478521", "2", "1"),
    postSalesToCountry("IM9001478521", "2", "1", "1234"),
    getVatOnSales("IM9001478521", "2", "1"),
    postVatOnSales("IM9001478521", "2", "1"),
    getCheckSales("IM9001478521", "2"),
    postCheckSales("IM9001478521", "2"),
    getAddSalesCountryList("IM9001478521"),
    postAddSalesCountryList("IM9001478521", answer = false, None),
    getCorrectPreviousReturn("IM9001478521"),
    postCorrectPreviousReturn("IM9001478521", answer = true),
    getCorrectionYear("IM9001478521"),
    postCorrectionYear("IM9001478521"),
    getCorrectionMonth("IM9001478521"),
    postCorrectionMonth("IM9001478521"),
    getCorrectionCountry("IM9001478521", "1", "1"),
    postCorrectionCountry("IM9001478521", "HU", "1", "1"),
    getAddNewCountry("IM9001478521", "1", "1"),
    postAddNewCountry("IM9001478521", "1", "1"),
    getCountryVatCorrectionAmount("IM9001478521", "1", "1"),
    postCountryVatCorrectionAmount("IM9001478521", "555.55", "1", "1"),
    getVatPayableConfirm("IM9001478521", "1", "1"),
    postVatPayableConfirm("IM9001478521", "1", "1"),
    getVatPayableCheck("IM9001478521", "1", "1"),
    postVatPayableCheck("IM9001478521", "1", "1"),
    getCorrectionCountriesList("IM9001478521", "1"),
    postCorrectionCountriesList("IM9001478521", "1"),
    getCheckYourAnswers("IM9001478521"),
    postCheckYourAnswers("IM9001478521"),
    getReturnSubmitted("IM9001478521")
  )

  setup("submittedReturnsForMultipleIOSSNumbers", "Submitted Returns for Multiple IOSS Numbers Journey") withRequests (
    goToAuthLoginPage,
    upFrontAuthLoginMultipleIOSSNumbers,
    getHomePage,
    getPastReturns("IM9007230001"),
    getReturnRegistrationSelection("IM9007230001"),
    postReturnRegistrationSelection("IM9007230001", "IM9007230001"),
    getPastReturnsPreviousRegistration(9, "IM9007230001"),
    getHomePage,
    getPastReturns("IM9007230001"),
    getReturnRegistrationSelection("IM9007230001"),
    postReturnRegistrationSelection("IM9007230001", "IM9007230002"),
    getPastReturnsPreviousRegistration(6, "IM9007230002")
  )

  setup("intermediaryReturns", "Returns Journey as an Intermediary") withRequests (
    goToAuthLoginPage,
    upFrontAuthLoginIntermediary,
    getIntermediaryStart("IM9001144771"),
    getStartReturnIntermediary("IM9001144771"),
    postStartReturnIntermediary("IM9001144771"),
    getWantToUploadFile("IM9001144771"),
    postWantToUploadFile("IM9001144771", answer = false),
    getSoldGoods("IM9001144771"),
    postSoldGoods("IM9001144771", answer = false),
    getCorrectPreviousReturn("IM9001144771"),
    postCorrectPreviousReturn("IM9001144771", answer = false),
    getCheckYourAnswers("IM9001144771"),
    postCheckYourAnswers("IM9001144771"),
    getReturnSubmitted("IM9001144771")
  )

  setup("fileUpload", "Returns File Upload Journey") withActions (
    goToAuthLoginPage,
    upFrontAuthLogin("IM9001478522"),
    getHomePage,
    getStartReturn("IM9001478522"),
    postStartReturn("IM9001478522"),
    getWantToUploadFile("IM9001478522"),
    postWantToUploadFile("IM9001478522", answer = true),
    getFileUpload("IM9001478522"),
    postFileUpload("IM9001478522"), pause(10),
    getFileUploaded,
    postFileUploaded("IM9001478522"),
    getCorrectPreviousReturn("IM9001478522"),
    postCorrectPreviousReturn("IM9001478522", answer = false),
    getCheckYourAnswers("IM9001478522"),
    postCheckYourAnswers("IM9001478522"),
    getReturnSubmitted("IM9001478522")
  )

  runSimulation()
}
