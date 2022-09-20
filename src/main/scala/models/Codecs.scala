package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object codecs{
  implicit val trafficGridDecoder: Decoder[TrafficGrid] = deriveDecoder[TrafficGrid]
  implicit val trafficMeasurementDecoder: Decoder[TrafficMeasurement] = deriveDecoder[TrafficMeasurement]
  implicit val measurementDecoder: Decoder[Measurement] = deriveDecoder[Measurement]
  implicit val routeEncoder: Encoder[Route] = deriveEncoder[Route]
}
