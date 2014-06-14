package core

import akka.actor.{Props, ActorLogging, Actor}
import spray.routing.HttpService
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import org.json4s.{Formats, DefaultFormats}
import spray.httpx.Json4sSupport
import spray.http.DateTime
import org.apache.commons.math3.random.MersenneTwister
import spray.json.{JsonFormat, DefaultJsonProtocol}
import reflect.ClassTag

case class GetNumber()

case class RandomNumber(createdAt: String, number: Float)

class RandomNumberGenerator extends Actor with ActorLogging {
  val rng = new MersenneTwister(util.Random.nextLong())

  def receive = {
    case GetNumber() => sender ! RandomNumber(DateTime.now.toIsoDateTimeString, rng.nextFloat())
    case msg => s"Cannot map $msg"
  }
}

object MyJsonProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
  implicit val statsMarshaller = json4sMarshaller[RandomNumber]
}

class RestRouting extends HttpService with Actor {

  implicit def actorRefFactory = context

  implicit val timeout = Timeout(5 seconds)

  def receive = runRoute(route)

  val rng = context.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")


  import  context.dispatcher
  import MyJsonProtocol.statsMarshaller

  val route = {

    path("rn") {
      get {
        complete {
          rng.ask(GetNumber()).mapTo[core.RandomNumber].map{
            value => value
          }
        }
      }
    }
  }
}