package bbc.pal

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Pal extends Simulation {

  val httpProtocol = http
    .baseURL("http://www.stage.bbc.co.uk")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-gb,en;q=0.5")
    
  val hpFeed = csv("pal/homepage_urls.txt").circular
  val iPlayerFeed = csv("pal/iplayer_urls.txt").circular
  val weatherFeed = csv("pal/weather_urls.txt").circular
  val newsFeed = csv("pal/news_urls.txt").circular
  val sportFeed = csv("pal/sport_urls.txt").circular

  val scn = scenario("Pal")
    .feed(hpFeed)
    .exec(http("Homepage").get("${homePage}").check(status.is(200)))

    .feed(iPlayerFeed)
    .exec(http("iPlayer").get("${iPlayer}").check(status.is(200)))

    .feed(weatherFeed)
    .exec(http("Weather").get("${weather}").check(status.is(200)))

    .feed(newsFeed)
    .exec(http("News").get("${news}").check(status.is(200)))

    .feed(sportFeed)
    .exec(http("Sport").get("${sport}").check(status.is(200)))

  setUp(scn.inject(
    rampUsersPerSec(10) to(100) during(5 minutes),
    constantUsersPerSec(10) during(5 minutes)
  ).protocols(httpProtocol))  
}
