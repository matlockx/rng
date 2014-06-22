package core

import akka.actor.{Props, ActorSystem}
import spray.can.Http
import akka.io.IO
import scala.concurrent.duration._
import spray.can.Http.GetStats
import akka.pattern._
                   import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import akka.routing.RoundRobinPool
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import spray.can.server.Stats
import spray.routing.HttpService

object Main extends App {



  implicit val system = ActorSystem("rng")

  val cores = Runtime.getRuntime().availableProcessors()
  system.log.info(s"Found $cores cores -> Creating a round robin pool of $cores")
  val rngActor = system.actorOf(RoundRobinPool(cores).props(Props(new RngService)), name = "rngService")
  val adminActor = system.actorOf(Props(new AdminService), name = "adminService")

  system.registerOnTermination {
    system.log.info("Actor per request demo shutdown.")
  }
  val config = system.settings.config
  IO(Http) ! Http.Bind(adminActor, interface = config.getString("rng.host.public_dns"), port = config.getInt("rng.host.port") + 1)
  IO(Http) ! Http.Bind(rngActor, interface = config.getString("rng.host.public_dns"), port = config.getInt("rng.host.port"))

}
