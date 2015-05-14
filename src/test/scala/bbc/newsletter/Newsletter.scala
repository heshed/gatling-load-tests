package bbc.newsletter

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._    
import scala.util.Random
import java.util.concurrent.atomic._

object Newsletter {

  def subscribe(requestName: String, email: String) = {
    http(requestName)
      .post("https://bbcsignups.external.bbc.co.uk/inxmail4/subscription/servlet")
        .formParam("INXMAIL_HTTP_REDIRECT", "http://www.stage.bbc.co.uk/newsletters/thenolanshow/subscribe?ptrt=http://www.stage.bbc.co.uk")
        .formParam("INXMAIL_HTTP_REDIRECT_ERROR", "http://www.stage.bbc.co.uk/newsletters/thenolanshow/error?ptrt=http://www.stage.bbc.co.uk")
        .formParam("INXMAIL_SUBSCRIPTION", "the nolan show")
        .formParam("tandc_06", "true")
        .formParam("email", email)
        .formParam("u13-confirmation", "0")
        .formParam("tandc_06", "TRUE")
      .check(substring("Check your inbox to confirm your email"))
  }

  def signInPost(requestName: String, email: String) = {
    http(requestName) 
      .post("/id/signin?ptrt=https%3A%2F%2Fssl.stage.bbc.co.uk%2Fnewsletters%2Fthenolanshow")
        .formParam("unique", email) 
        .formParam("password", "loadtest")
        .formParam("bbcid_submit_button", "Sign in")
      .check(substring("Please confirm your age"))
  }

  def registerPost(email: String) = {
    http("registerPost") 
      .post("/id/register?ptrt=https%3A%2F%2Fssl.stage.bbc.co.uk%2Fnewsletters%2Fthenolanshow")
        .formParam("email", email)
        .formParam("confirmpassword", "loadtest")
        .formParam("confirmpassword_confirm", "loadtest")
        .formParam("bbcid_submit_button", "Register")
      .check(substring("registration is complete"))
  }

  val newsletterPage = http("newsLetterPage")
    .get("/newsletters/thenolanshow")
    .check(substring("To subscribe you need to be 13 or over"))
} 

import Newsletter._

class Newsletter extends Simulation {

  val httpProtocol = http
    .baseURL("https://ssl.stage.bbc.co.uk")
    .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    .acceptLanguageHeader("en-GB,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36")

  val value = new AtomicInteger(1)

  // journey A
  val signedInUserSubscribe = scenario("signedInUserSubscribe")
    
    .exec(http("jumpToAgeConfirm").get("/newsletters/thenolanshow").check(substring("Please confirm your age")))
  
    .exec(_.set("newEmail", s"adrian@loadtest${value.getAndIncrement}.com"))

    .exec(subscribe("signedInSubscribe", "${newEmail}"))

  // journey B
  val signInSubscribe = scenario("signInSubscribe")
    .exec(newsletterPage)
    
    .exec(http("signInGet") 
      .get("/id/signin?ptrt=https%3A%2F%2Fssl.stage.bbc.co.uk%2Fnewsletters%2Fthenolanshow"))

    .exec(signInPost("signInPost", "loadtest@loadtest.com"))
      
    .exec(subscribe("signInSubscribe", "loadtest@loadtest.com"))

  val random = new Random()
  // Journey C
  val registerSubscribe = scenario("registerSubscribe")
    .exec(_.set("randomDomain", s"random@${random.nextInt(Int.MaxValue)}.com"))

    .exec(newsletterPage)

    .exec(http("registerGet") 
      .get("/id/register?ptrt=https%3A%2F%2Fssl.stage.bbc.co.uk%2Fnewsletters%2Fthenolanshow")
      .check(substring("Register")))
    
    .exec(registerPost("${randomDomain}"))

    .exec(subscribe("registerSubscribe", "${randomDomain}"))

    //journey D
  val nonSignInUserVisitsPage = scenario("nonSignInUserVisitsPage")
    .exec(newsletterPage)

  setUp(
    signedInUserSubscribe.inject(
      rampUsersPerSec(1) to(50) during(5 minutes),
      constantUsersPerSec(50) during(15 minutes)
    ),
    signInSubscribe.inject(
      rampUsersPerSec(1) to(13) during(5 minutes),
      constantUsersPerSec(13) during(15 minutes)
    ),
    registerSubscribe.inject(
      rampUsersPerSec(1) to(12) during(5 minutes),
      constantUsersPerSec(12) during(15 minutes)
    ),
    nonSignInUserVisitsPage.inject(
      rampUsersPerSec(1) to(25) during(5 minutes),
      constantUsersPerSec(25) during(15 minutes)
    )
  ).protocols(httpProtocol)
}
