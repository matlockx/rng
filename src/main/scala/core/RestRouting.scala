package core

import akka.actor.{Props, ActorLogging, Actor}
import spray.routing.HttpService
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import org.json4s.{Formats, DefaultFormats}
import spray.httpx.Json4sSupport
import spray.http._
import org.apache.commons.math3.random.MersenneTwister
import spray.json.{JsonFormat, DefaultJsonProtocol}
import reflect.ClassTag
import spray.routing.directives.LogEntry
import akka.event.Logging.LogLevel
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.Some
import org.omg.CosNaming.NamingContextPackage.NotFound

case class GetNumber()

case class RandomNumber(createdAt: String, number: Float)

class RandomNumberGenerator extends Actor with ActorLogging {
  val rng = new MersenneTwister(util.Random.nextLong())

  def receive = {
    case GetNumber() =>
      sender ! RandomNumber(DateTime.now.toIsoDateTimeString, rng.nextFloat())
    case msg => s"Cannot map $msg"
  }
}

object MyJsonProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats

  implicit val statsMarshaller = json4sMarshaller[RandomNumber]
}

class RestRouting extends HttpService with Actor with ActorLogging {

  implicit def actorRefFactory = context

  implicit val timeout = Timeout(5 seconds)

  def receive = runRoute(route)

  val rng = context.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")


  import context.dispatcher
  import MyJsonProtocol.statsMarshaller

  val route = {

    path("rn") {
      clientIP {  ip =>
        // x =>
        get {
          logRequest(showRequest _) {
            complete {

              rng.ask(GetNumber()).mapTo[core.RandomNumber].map {
                logRequestResponse(ip.toString())
                value => value
              }
            }
          }

        }
      }
    }
  }

  import akka.event.Logging._

  def showRequest(request: HttpRequest) = LogEntry(request.toString, InfoLevel)
}