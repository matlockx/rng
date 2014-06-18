package core

import akka.actor.{Props, ActorLogging, Actor}
import spray.routing.HttpService
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import org.json4s.{Formats, DefaultFormats}
import spray.httpx.Json4sSupport
import org.apache.commons.math3.random.{Well44497b}
import spray.json.JsonFormat
import reflect.ClassTag
import spray.routing.directives.LogEntry
import akka.event.Logging.LogLevel
import spray.http.{HttpResponse, HttpRequest}
import scala.Some
import org.omg.CosNaming.NamingContextPackage.NotFound
import spray.can.Http
import spray.can.server.{Stats => SprayStats}
import org.joda.time.DateTime

case class GetNumbers(amount : Int = 1)

case class Reseed()

case class RandomNumbers(numbers: Seq[Float], createdAt: String = DateTime.now().toString())

class RandomNumberGenerator extends Actor with ActorLogging {
  val rng = new Well44497b(util.Random.nextLong())
  val maxNumber = context.system.settings.config.getInt("rng.max-amount-random-numbers")

  def receive = {
    case GetNumbers(amount) =>
      val newAmount = if(amount > maxNumber) maxNumber else amount
      val numbers = for(i <- 0 until newAmount) yield rng.nextFloat()
      sender ! RandomNumbers(numbers)
    case Reseed() => rng.setSeed(util.Random.nextLong())
    case msg => s"Cannot map $msg"
  }
}

object MyJsonProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats

  implicit val rnMarshaller = json4sMarshaller[RandomNumbers]
  implicit val statsMarshaller = json4sMarshaller[SprayStats]
}

class RestRouting extends HttpService with Actor with ActorLogging {

  implicit def actorRefFactory = context

  implicit val timeout = Timeout(5 seconds)

  def receive = runRoute(route)

  import context.dispatcher

  val rng = context.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")
  context.system.scheduler.schedule(10 seconds, 1 hour, rng, Reseed())

  import MyJsonProtocol.statsMarshaller
  import MyJsonProtocol.rnMarshaller

  def httpListener = actorRefFactory.actorSelection("/user/IO-HTTP/listener-0")

  val route = {

    path("rns") {
      clientIP { ip =>
        get {
          parameter("amount".as[Int] ? 1) { amount =>
            logRequest(showRequest _) {
              complete {
                rng.ask(GetNumbers(amount)).mapTo[core.RandomNumbers]
              }
            }
          }
        }
      }
    } ~ path("admin" / "ping") {
      complete {
        "pong"
      }
    } ~ path("admin" / "stats") {
      complete {
        (httpListener ? Http.GetStats).mapTo[SprayStats]
      }
    }
  }

  import akka.event.Logging._

  def showRequest(request: HttpRequest) = LogEntry(request.toString, InfoLevel)
}