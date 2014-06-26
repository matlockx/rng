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
import java.nio.ByteOrder

case class GetFloatNumbers(amount: Int = 1)

case class GetLongNumbers(amount: Int = 1)

case class Reseed()

case class RandomNumbers[T](numbers: Seq[T], createdAt: String = DateTime.now().toString())

class RandomNumberGenerator extends Actor with ActorLogging {
  val rng = new Well44497b(util.Random.nextLong())
  val maxNumber = context.system.settings.config.getInt("rng.max-amount-random-numbers")

  def getSaveAmount(amount: Int): Int = if (amount > maxNumber) maxNumber else amount

  def getRandomNumbers[T](amount: Int, nextRandomNumber: () => T): Seq[T] = {
    for (i <- 0 until getSaveAmount(amount)) yield nextRandomNumber()
  }

  def receive = {
    case GetLongNumbers(amount) =>
      val numbers = getRandomNumbers(amount, rng.nextLong)
      val b = new akka.util.ByteStringBuilder()
      sender ! RandomNumbers(numbers)
    case GetFloatNumbers(amount) =>
      val numbers = getRandomNumbers(amount, rng.nextFloat)
      sender ! RandomNumbers(numbers)
    case Reseed() =>
      log.info("Reseed")
      rng.setSeed(util.Random.nextLong())
    case msg => s"Cannot map $msg"
  }
}

trait RngRoute extends HttpService {

  implicit val timeout = Timeout(5 seconds)

  val rng = actorRefFactory.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")

  import akka.pattern._
  import spray.http.MediaTypes._
  import RngJsonProtocol.RngFormatLong
  import RngJsonProtocol.RngFormatFloat
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.SprayJsonSupport._

  val rngRoute = pathPrefix("rns") {
    get {
      clientIP { ip =>
        parameter("amount".as[Int] ? 1) { amount =>
          logRequest(showRequest _) {
            respondWithMediaType(`application/json`) {
              pathSuffix("long") {
                complete {
                  (rng ? GetLongNumbers(amount)).mapTo[RandomNumbers[Long]]
                }
              } ~
                pathSuffix("float") {
                  complete {
                    (rng ? GetFloatNumbers(amount)).mapTo[RandomNumbers[Float]]
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

class RngService extends Actor with RngRoute with ActorLogging {

  implicit def actorRefFactory = context

  def receive = runRoute(rngRoute)

  import context.dispatcher

  context.system.scheduler.schedule(10 seconds, 10 seconds, self, Reseed())

}

