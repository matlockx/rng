package core

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import org.specs2.matcher.JsonMatchers

class RngRouteSpec extends Specification with Specs2RouteTest with RngRoute {
  def actorRefFactory = system // connect the DSL to the test ActorSystem
  import RngJsonProtocol.RngFormat
  "The service" should {
    "return a random number for GET request to the rns path without an amount" in {
      Get("/rns") ~> addHeader("Remote-Address", "127.0.0.1") ~> rngRoute ~> check {
        println(responseAs[String])
        responseAs[String] must contain("number")
      }
    }
    "return 10 random numbers for GET request to the rns path with amount of 10" in {
      Get("/rns?amount=10") ~> addHeader("Remote-Address", "127.0.0.1") ~> rngRoute ~> check {
        println(responseAs[String])
        responseAs[String] must contain("number")
      }
    }
  }
}