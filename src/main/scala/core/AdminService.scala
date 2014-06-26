package core

import spray.routing.HttpService
import spray.can.Http
import spray.can.server.Stats
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
import RngJsonProtocol.statsFormat
import spray.httpx.SprayJsonSupport._
import akka.actor.Actor

trait AdminRoute extends HttpService {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)

  def httpListener = actorRefFactory.actorSelection("/user/IO-HTTP/listener-0")

  val adminRoute =
    path("admin") {
      getFromResource("html/index.html")
    } ~
      pathPrefix("admin") {
        get {
          pathSuffix("ping") {
            complete {
              "pong"
            }
          } ~
            pathSuffix("stats") {
              complete {
                (httpListener ? Http.GetStats).mapTo[Stats]
              }
            }
        }

      }

}


class AdminService extends Actor with AdminRoute {

  implicit def actorRefFactory = context

  def receive = runRoute(adminRoute)


}
