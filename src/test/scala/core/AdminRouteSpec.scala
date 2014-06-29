import core.{RngRoute, AdminRoute, RngService}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.testkit.{RouteTest, Specs2RouteTest}
import spray.routing.HttpService
import spray.http.StatusCodes._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class AdminRouteSpec extends Specification with Specs2RouteTest with AdminRoute {
  def actorRefFactory = system // connect the DSL to the test ActorSystem

  "The service" should {
    "return a pong for GET request to the ping path" in {
      Get("/admin/ping") ~> adminRoute ~> check {
        println(responseAs[String])
        responseAs[String] must contain("pong")
      }
    }
    "return radnom check index page from admin root path" in {
      Get("/admin/index.html") ~> adminRoute ~> check {
        println(responseAs[String])
        responseAs[String] must contain("RNG Test")
      }
    }

  }
}