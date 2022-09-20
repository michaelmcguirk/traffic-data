import scala.io.Source
import models.{Intersection, Measurement, Road, Route, TrafficGrid}
import io.circe.parser.decode
import io.circe.syntax._
import models.codecs._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.Map


object BestRoute extends App {

  def getKey(m: Measurement): String = s"${m.startAvenue}${m.startStreet}${m.endAvenue}${m.endStreet}"

  def getOriginName(m: Measurement): String = s"${m.startAvenue}${m.startStreet}"

  def getDestinationName(m: Measurement): String = s"${m.endAvenue}${m.endStreet}"

  def getRoad(m: Measurement, i: Intersection): Road = Road(i.name, m.transitTime)

  def loadAndParseFile(file: String): Either[String, List[Measurement]] = {
    val fileSource = Source.fromFile(file)
    val fileStr = fileSource.mkString
    fileSource.close
    val measurements: Map[String, Measurement] = mutable.Map()
    decode[TrafficGrid](fileStr) match {
      case Left(error) => Left(s"Error: $error")
      case Right(grid) =>
        grid.trafficMeasurements.foreach(_.measurements.foreach { tm =>
          val key = getKey(tm)
          measurements.get(key) match {
            case Some(measurement) =>
              val count = measurement.count.getOrElse(1)
              val newCount = count + 1
              val rollingMean = ((measurement.transitTime * count) + tm.transitTime) / newCount
              measurements.put(key, measurement.copy(count = Some(newCount), transitTime = rollingMean))
            case None =>
              measurements.put(key, Measurement(tm.startAvenue, tm.startStreet, tm.transitTime, tm.endAvenue, tm.endStreet, Some(1), Some(key)))
          }
        })
        Right(measurements.values.toList)
    }
  }

  def buildGraph(measurements: List[Measurement]): mutable.Map[String, Intersection] = {
    val intersections: Map[String, Intersection] = mutable.Map()
    measurements.foreach { m =>
      val originKey = getOriginName(m)
      val destKey = getDestinationName(m)
      val destination = intersections.get(destKey) match {
        case Some(destIntersection) => destIntersection
        case None =>
          val dest = Intersection(destKey, List.empty)
          intersections.put(destKey, dest)
          dest
      }
      intersections.get(originKey) match {
        case Some(i) => intersections.put(originKey, i.copy(adjRoads = i.adjRoads :+ getRoad(m, destination)))
        case None => intersections.put(originKey, Intersection(getOriginName(m), List(getRoad(m, destination))))
      }
    }
    intersections
  }

  def getStartingIntersection(intersections: mutable.Map[String, Intersection], start: String): Either[String, Intersection] = {
    intersections.get(start) match {
      case Some(intersection) =>
        val startIntersection = intersection.copy(distanceFromStart = 0)
        intersections.put(start, startIntersection)
        Right(startIntersection)
      case None => Left("Not a valid intersection")
    }
  }

  def calcPath(start: Intersection, intersections: mutable.Map[String, Intersection]): Unit = {
    val distSort: Ordering[Intersection] = (a, b) => a.distanceFromStart.compareTo(b.distanceFromStart)
    val queue = mutable.PriorityQueue[Intersection](start)(distSort)
    while (queue.nonEmpty) {
      val u = queue.dequeue()
      u.adjRoads.foreach { e =>
        intersections.get(e.destination).map { v =>
          if ((u.distanceFromStart + e.transitTime) < v.distanceFromStart) {
            val newV = v.copy(distanceFromStart = u.distanceFromStart + e.transitTime, previous = Some(u.name))
            intersections.put(e.destination, newV)
            queue.enqueue(intersections(e.destination))
          }
        }
      }
    }
  }

  def buildRoute(start: String, end: String, intersections: mutable.Map[String, Intersection]): List[String] = {

    @tailrec
    def traverse(l: List[String], intersectionRef: String): List[String] = {
      intersections.get(intersectionRef) match {
        case Some(i) =>
          i.previous match {
            case Some(prev) => traverse(l :+ i.name, prev)
            case None => l
          }
        case None => l
      }
    }
    (traverse(List(), end) :+ start).reverse
  }

  def findBestPath(start: String, end: String, file: String): Either[String, Route] = {
    // starting intersection, ending intersection, sequence of road segments, the total transit time
    for {
      measurements <- loadAndParseFile(file)
      graph = buildGraph(measurements)
      startIntersection <- getStartingIntersection(graph, start)
      _ = calcPath(startIntersection, graph)
    } yield {
      val endIntersection = graph(end)
      Route(start, end, endIntersection.distanceFromStart, buildRoute(start, end, graph))
    }

  }

  findBestPath(args(0), args(1), args(2)) match {
    case Left(error) => println(s"Oh no! An error occurred... - $error" )
    case Right(route) => println(route.asJson)
  }

}
