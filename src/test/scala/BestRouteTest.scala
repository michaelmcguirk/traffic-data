import models.Measurement
import org.scalatest.flatspec.AnyFlatSpec

class BestRouteTest extends AnyFlatSpec {
  val measurements = List(
    Measurement("A", "1", 10, "B", "1"),
    Measurement("B", "1", 2, "A", "2"),
    Measurement("A", "1", 1, "A", "2"),
    Measurement("A", "2", 9, "B", "2")
  )
  "buildGraph" should "build graph from list of Measurements" in {
    val graph = BestRoute.buildGraph(measurements)
    assert(graph("A1").adjRoads.length == 2)
    assert(graph("A1").adjRoads.map(_.destination) == List("B1","A2"))
  }

  "getStartingIntersection" should "return the valid intersection based on key" in {
    val graph = BestRoute.buildGraph(measurements)
    val name = "A1"
    BestRoute.getStartingIntersection(graph, name).map(i =>  assert(i.name == name))
  }

  it should "return left error when based on an invalid key" in {
    val graph = BestRoute.buildGraph(measurements)
    val name = "XX"
    assert(BestRoute.getStartingIntersection(graph, name).isLeft)
  }

  "calcPath" should "calculate the best path from the start to each intersection" in {
    val graph = BestRoute.buildGraph(measurements)
    val start = BestRoute.getStartingIntersection(graph, "A1")
    BestRoute.calcPath(start.getOrElse(graph("A1")), graph)
    assert(graph("B2").distanceFromStart == 10)
  }

  "findBestPath" should "return correct route" in {
    val route = BestRoute.findBestPath("A1", "B2", sys.props.getOrElse("testData", "/Users/mmcguirk/scala-projects/Topl/src/test/resources/test-data.json"))
    println(route)
    assert(route.isRight)
    route.map{ r =>
      assert(r.distance == 7.4)
      assert(r.route == List("A1", "A2", "B2"))
    }
  }
}
