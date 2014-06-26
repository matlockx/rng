package core

import spray.json._
import scala.concurrent.duration.FiniteDuration
import spray.can.server.{Stats => SprayStats}

/**
 *
 */
object RngJsonProtocol extends DefaultJsonProtocol {
  implicit val finiteDurationFormat: RootJsonFormat[FiniteDuration] = new RootJsonFormat[FiniteDuration]{
    def write(f: FiniteDuration): JsObject = JsObject("length" -> JsNumber(10))

    def read(j: JsValue): FiniteDuration = throw new UnsupportedOperationException
  }

  implicit val statsFormat = jsonFormat8(SprayStats)
  implicit val RngFormatFloat = jsonFormat2(RandomNumbers[Float])
  implicit val RngFormatLong = jsonFormat2(RandomNumbers[Long])
}
