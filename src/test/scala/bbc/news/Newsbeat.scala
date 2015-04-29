package bbc.news

import scala.concurrent.duration._
 
import io.gatling.core.Predef._
import io.gatling.http.Predef._
 
class Newsbeat extends Simulation {
 
  val httpProtocol = http
      .baseURL("http://www.stage.bbc.co.uk")
      .inferHtmlResources(BlackList(""".*\.js.*""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*171""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
      .acceptHeader("""text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")
      .acceptEncodingHeader("""gzip, deflate""")
      .acceptLanguageHeader("""en-gb,en;q=0.5""")
      .userAgentHeader("""Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0""")
 
  val scn = scenario("Newsbeat Test")
      .exec(http("/newsbeat")
      .get("/newsbeat")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/popular")
      .get("/newsbeat/popular")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/topics")
      .get("/newsbeat/topics")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/topics/music")
      .get("/newsbeat/topics/music")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/topics/entertainment")
      .get("/newsbeat/topics/entertainment")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/article")
      .get("/newsbeat/article/32129276/pilot-shares-thank-you-letter-from-airline-passenger")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/article2")
      .get("/newsbeat/article/32284686/labour-manifesto-whats-in-it-for-young-people-across-the-uk")
      .check(status.is(200)))

      .exec(http("/newsbeat/article3")
      .get("/newsbeat/article/32288347/madonnas-drake-snog-and-the-memes")
      .check(status.is(200)))
 
      .exec(http("/newsbeat/article4")
      .get("/newsbeat/article/32297013/olivia-munn-joins-x-men-apocalypse-cast-as-betsy-braddock")
      .check(status.is(200)))
 
  setUp(scn.inject(
    rampUsersPerSec(1) to(25) during(2 minutes),
    constantUsersPerSec(25) during(8 minutes),
    rampUsersPerSec(25) to(50) during(2 minutes),
    constantUsersPerSec(50) during(8 minutes),
    rampUsersPerSec(50) to(125) during(2 minutes),
    constantUsersPerSec(125) during(8 minutes),
    rampUsersPerSec(125) to(225) during(5 minutes),
    constantUsersPerSec(225) during(15 minutes)
  ).protocols(httpProtocol))
}
