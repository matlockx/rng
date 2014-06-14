package core

import akka.actor.{Props, ActorLogging, Actor}
import spray.routing.HttpService
import spray.can.Http.GetStats
import akka.pattern._
import akka.util.Timeout
import org.json4s.JsonAST.JObject


import scala.concurrent.duration._
import spray.can.server.Stats
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.{Formats, DefaultFormats}
import spray.httpx.Json4sSupport
import scala.concurrent.Future
import scala.reflect.ClassTag
import spray.http.DateTime
import org.apache.commons.math3.random.MersenneTwister

case class GetNumber()
case class RandomNumber(createdAt: DateTime, number: Float)

class RandomNumberGenerator extends Actor with ActorLogging {
  val rng = new MersenneTwister(util.Random.nextLong())
  def receive = {
    case GetNumber() => sender ! RandomNumber(DateTime.now, rng.nextFloat())
    case _ => "Cannot map msg"
  }
}

class RestRouting extends HttpService with Actor with Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats

  implicit def actorRefFactory = context

  implicit val timeout = Timeout(5 seconds)

  def receive = runRoute(route)
  val rng = context.actorOf(Props(new RandomNumberGenerator), name = "RandomNumberGenerator")


  val route = {

    path("rn") {
      get {

        complete {
          rng.ask(GetNumber()).mapTo[RandomNumber]
        }
      }
    } ~
      path("draws" / IntNumber) {
        orderId =>
          get {
            println(orderId)
            complete {
              s"get $orderId"
            }
          } ~
            delete {
              complete {
                "delete"
              }
            }
        //}
      } ~
      path("order") {
        get {
          complete("get order")
        }
      } ~
      path("stats") {
        complete {
          actorRefFactory.actorSelection("/user/IO-HTTP/listener-0").ask(GetStats).mapTo[Stats]

        }
      }
  }
}