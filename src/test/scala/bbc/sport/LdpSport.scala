package bbc.sport

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bbc.utils._

class LdpSport extends Simulation {

  val palUrls = csv("sport/pal-client.txt").random 
  val apiKey = None

  // SBT_OPTS="-Denv=test" sbt
  val httpProtocol = http
    .baseURL(BaseUrls.url("data"))
    .acceptHeader("application/json-ld")
    .disableCaching

  val ldpSport = scenario("ldpSport")
    .feed(palUrls)
    .exec(http("Pal Client")
    .get("${palUrl}" + apiKey)
    .check(status.in(200, 201, 404)))
  
  setUp(ldpSport.inject(
    rampUsersPerSec(10) to 200 during(5 minutes),
    constantUsersPerSec(200) during(20 minutes),
    rampUsersPerSec(200) to 300 during(2 minutes),
    constantUsersPerSec(300) during(6 minutes),
    rampUsersPerSec(300) to 400 during(2 minutes),
    constantUsersPerSec(400) during(6 minutes),
    rampUsersPerSec(400) to 500 during(2 minutes),
    constantUsersPerSec(500) during(6 minutes),
    rampUsersPerSec(500) to 600 during(2 minutes),
    constantUsersPerSec(600) during(6 minutes),
    rampUsersPerSec(600) to 700 during(2 minutes),
    constantUsersPerSec(700) during(6 minutes),
    rampUsersPerSec(700) to 800 during(2 minutes),
    constantUsersPerSec(800) during(10 minutes)
  ).protocols(httpProtocol))
}
