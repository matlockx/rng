package core

import akka.actor.{Props, ActorLogging, Actor}
import spray.routing.HttpService
import akka.util.Timeout

import scala.concurrent.duration._
import spray.httpx.Json4sSupport
import org.apache.commons.math3.random.{Well44497b}
import reflect.ClassTag
import spray.routing.directives.LogEntry
import spray.http.HttpRequest
import org.joda.time.DateTime
import spray.httpx.encoding.Gzip
import spray.http.HttpRequest

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
    case Reseed() =>
      log.info("Reseed")
      rng.setSeed(util.Random.nextLong())
    case msg => s"Cannot map $msg"
  }
}

trait RngService extends HttpService  {

  implicit val timeout = Timeout(5 seconds)

  val rng  = actorRefFactory.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")

  import akka.pattern._
  import spray.http.MediaTypes._
  import RngJsonProtocol.RngFormat
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.SprayJsonSupport._

  val route = compressResponse(Gzip) {
    path("rns") {
      clientIP { ip =>
        get {
          parameter("amount".as[Int] ? 1) { amount =>
            logRequest(showRequest _) {
              respondWithMediaType(`application/json`) {
                complete {
                  (rng ? GetNumbers(amount)).mapTo[RandomNumbers]
                }
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

class RestRouting extends Actor with RngService with ActorLogging {

  implicit def actorRefFactory = context
  def receive = runRoute(route)
  import context.dispatcher

  context.system.scheduler.schedule(10 seconds, 10 seconds, self, Reseed())

}

