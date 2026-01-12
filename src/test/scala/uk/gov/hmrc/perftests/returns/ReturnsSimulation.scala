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

class ReturnsSimulation extends PerformanceTestRunner {

  val returnsBaseUrl: String = baseUrlFor("ioss-returns-frontend")

  setup("returns", "Returns Journey") withRequests (
    goToAuthLoginPage,
    upFrontAuthLogin,
    getHomePage,
    getStartReturn,
    postStartReturn,
    getSoldGoods,
    postSoldGoods(true),
    getSoldToCountry("1"),
    postSoldToCountry("1", "AT"),
    getVatRatesFromCountry("1"),
    postVatRatesFromCountry("1", "20"),
    getSalesToCountry("1", "1"),
    postSalesToCountry("1", "1", "5555"),
    getVatOnSales("1", "1"),
    postVatOnSales("1", "1"),
    getCheckSales("1"),
    postCheckSales("1"),
    getAddSalesCountryList,
    postAddSalesCountryList(true, Some("2")),
    getSoldToCountry("2"),
    postSoldToCountry("2", "FR"),
    getVatRatesFromCountry("2"),
    postVatRatesFromCountry("2", "20"),
    getSalesToCountry("2", "1"),
    postSalesToCountry("2", "1", "1234"),
    getVatOnSales("2", "1"),
    postVatOnSales("2", "1"),
    getCheckSales("2"),
    postCheckSales("2"),
    getAddSalesCountryList,
    postAddSalesCountryList(false, None),
    getCorrectPreviousReturn,
    postCorrectPreviousReturn(true),
    getCorrectionYear,
    postCorrectionYear,
    getCorrectionMonth,
    postCorrectionMonth,
    getCorrectionCountry("1", "1"),
    postCorrectionCountry("HU", "1", "1"),
    getAddNewCountry("1", "1"),
    postAddNewCountry("1", "1"),
    getCountryVatCorrectionAmount("1", "1"),
    postCountryVatCorrectionAmount("555.55", "1", "1"),
    getVatPayableConfirm("1", "1"),
    postVatPayableConfirm("1", "1"),
    getVatPayableCheck("1", "1"),
    postVatPayableCheck("1", "1"),
    getCorrectionCountriesList("1"),
    postCorrectionCountriesList("1"),
    getCheckYourAnswers,
    postCheckYourAnswers,
    getReturnSubmitted
  )

  setup("submittedReturnsForMultipleIOSSNumbers", "Submitted Returns for Multiple IOSS Numbers Journey") withRequests (
    goToAuthLoginPage,
    upFrontAuthLoginMultipleIOSSNumbers,
    getHomePage,
    getPastReturns,
    getReturnRegistrationSelection,
    postReturnRegistrationSelection("IM9007230001"),
    getPastReturnsPreviousRegistration(9, "IM9007230001"),
    getHomePage,
    getPastReturns,
    getReturnRegistrationSelection,
    postReturnRegistrationSelection("IM9007230002"),
    getPastReturnsPreviousRegistration(6, "IM9007230002")
  )

  setup("intermediaryReturns", "Returns Journey as an Intermediary") withRequests (
    goToAuthLoginPage,
    upFrontAuthLoginIntermediary,
    getIntermediaryStart,
    getStartReturnIntermediary,
    postStartReturnIntermediary,
    getSoldGoods,
    postSoldGoods(false),
    getCorrectPreviousReturn,
    postCorrectPreviousReturn(false),
    getCheckYourAnswers,
    postCheckYourAnswers,
    getReturnSubmitted
  )

  runSimulation()
}
