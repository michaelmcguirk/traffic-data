package models

case class TrafficGrid(trafficMeasurements: List[TrafficMeasurement])

case class TrafficMeasurement(measurementTime: Long, measurements: List[Measurement])

case class Measurement(startAvenue: String, startStreet: String, transitTime: Double, endAvenue: String, endStreet: String, count: Option[Int] = None, intersection: Option[String] = None)

case class Intersection(name: String, adjRoads: List[Road], previous: Option[String] = None, distanceFromStart: Double = Double.MaxValue)

case class Road(destination: String, transitTime: Double)

case class Route(start: String, end: String, distance: Double, route: List[String])



