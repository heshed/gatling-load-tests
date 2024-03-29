package bbc.bbcid

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._

class SignInOAuth extends Simulation {

  val httpProtocol = http
    .baseURL("https://ssl.stage.bbc.co.uk")
    .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    .acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36")

  val signIn = http("signIn")
    .get("/oauth/authorize?response_type=token&client_id=iplayer-ios&redirect_uri=http://www.bbc.co.uk/iPlayer&scope=plays.any.r")
    .check(css("#bbcid-signin-form", "action").saveAs("signInPostUrl"))
    .check(status.is(200))

  val emailFeeder = csv("bbcid/registered-email.csv")

  val signInPost = http("signInPost")
    .post("${signInPostUrl}")
    .formParam("unique", "${email}")
    .formParam("password", "loadtest")
    .formParam("bbcid_submit_button", "Sign in")
    .check(css("#confirmationForm input[name=authorizationRequest]", "value").saveAs("authorizationRequest"),
      css("#confirmationForm input[name=authenticationToken]", "value").saveAs("authenticationToken"),
      css("#confirmationForm input[name=_csrf_signature]", "value").saveAs("_csrf_signature"),
      status.is(200))

  val oauth = http("oauth")
    .post("/oauth/authorize")
    .formParam("authorizationRequest", "${authorizationRequest}")
    .formParam("authenticationToken", "${authenticationToken}")
    .formParam("user_oauth_approval", "true")
    .formParam("authorize", "Allow")
    .formParam("_csrf_signature", "${_csrf_signature}")
    .check(status.is(200))

  val iPlayerOAuthLoginScn = scenario("iPlayerOAuthLogin")
    .feed(emailFeeder)
    .exec(signIn)
    .exec(signInPost)
    .exec(oauth)
    .pause(3)

  setUp(iPlayerOAuthLoginScn.inject(
    rampUsersPerSec(1) to(120) during(20 minutes) 
  ).protocols(httpProtocol))
}
