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

package uk.gov.hmrc.perftests.returns.Utils

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.regex.RegexCheckType

object FileUploadChecks {

  private val fileUploadAmazonUrlPattern = """action="(.*?)""""
  private val callBackUrPattern          = """name="x-amz-meta-callback-url" value="(.*?)""""
  private val amzDatePattern             = """name="x-amz-date" value="(.*?)""""
  private val successRedirectPattern     = """name="success_action_redirect" value="(.*?)""""
  private val amzCredentialPattern       = """name="x-amz-credential" value="(.*?)""""
  private val initiateResponsePattern    = """name="x-amz-meta-upscan-initiate-response" value="(.*?)""""
  private val initiateReceivedPattern    = """name="x-amz-meta-upscan-initiate-received" value="(.*?)""""
  private val metaOriginalFilename       = """name="x-amz-meta-original-filename" value="(.*?)""""
  private val metaRequestIDPattern       = """name="x-amz-meta-request-id" value="(.*?)""""
  private val algorithmPattern           = """name="x-amz-algorithm" value="(.*?)""""
  private val keyPattern                 = """name="key" value="(.*?)""""
  private val metaSessionIDPattern       = """name="x-amz-meta-session-id" value="(.*?)""""
  private val signaturePattern           = """name="x-amz-signature" value="(.*?)""""
  private val errorRedirectPattern       = """name="error_action_redirect" value="(.*?)""""
  private val policyPattern              = """name="policy" value="(.*?)""""
  private val aclPattern                 = """name="acl" value="(.*?)""""
  private val consumingServicePattern    = """name="x-amz-meta-consuming-service" value="(.*?)""""

  def saveFileUploadUrl: CheckBuilder[RegexCheckType, String] =
    regex(fileUploadAmazonUrlPattern).saveAs("fileUploadAmazonUrl")

  def saveCallBack: CheckBuilder[RegexCheckType, String] =
    regex(callBackUrPattern).saveAs("callBack")

  def saveAmazonDate: CheckBuilder[RegexCheckType, String] =
    regex(amzDatePattern).saveAs("amazonDate")

  def saveSuccessRedirect: CheckBuilder[RegexCheckType, String] =
    regex(successRedirectPattern).saveAs("successRedirect")

  def saveAmazonCredential: CheckBuilder[RegexCheckType, String] =
    regex(amzCredentialPattern).saveAs("amazonCredential")

  def saveUpscanInitiateResponse: CheckBuilder[RegexCheckType, String] =
    regex(initiateResponsePattern).saveAs("upscanInitiateResponse")

  def saveUpscanInitiateReceived: CheckBuilder[RegexCheckType, String] =
    regex(initiateReceivedPattern).saveAs("upscanInitiateReceived")

  def saveAmazonMetaOriginalFileName: CheckBuilder[RegexCheckType, String] =
    regex(metaOriginalFilename).saveAs("amazonMetaOriginalFileName")

  def saveAMZMetaRequestId: CheckBuilder[RegexCheckType, String] =
    regex(metaRequestIDPattern).saveAs("amazonMetaRequestID")

  def saveAmazonAlgorithm: CheckBuilder[RegexCheckType, String] =
    regex(algorithmPattern).saveAs("amazonAlgorithm")

  def saveKey: CheckBuilder[RegexCheckType, String] =
    regex(keyPattern).saveAs("key")

  def saveAMZMetaSessionId: CheckBuilder[RegexCheckType, String] =
    regex(metaSessionIDPattern).saveAs("amazonMetaSessionID")

  def saveAmazonSignature: CheckBuilder[RegexCheckType, String] =
    regex(signaturePattern).saveAs("amazonSignature")

  def saveErrorRedirect: CheckBuilder[RegexCheckType, String] =
    regex(errorRedirectPattern).saveAs("errorRedirect")

  def savePolicy: CheckBuilder[RegexCheckType, String] =
    regex(policyPattern).saveAs("policy")

  def saveConsumingService: CheckBuilder[RegexCheckType, String] =
    regex(consumingServicePattern).saveAs("consumingService")

  def saveAcl: CheckBuilder[RegexCheckType, String] =
    regex(aclPattern).saveAs("acl")

}
