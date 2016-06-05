import scala.io.Source
import scala.math.sqrt

object Main {
  case class Observation(label: Int, pixels: List[Int])
  case class ClassifyResult(predicted: Int, actual: Int)
  
  def distance(p1: List[Int], p2: List[Int]): Double = {
    p1.zip(p2).map { case (x, y) => (x - y) * (x - y) }.sum
  }
  
  def classify(observations: List[Observation], unknown: List[Int]): Int = observations
    .map(observation => (observation.label, distance(observation.pixels, unknown)))
    .minBy(_._2)
    ._1
  
  def resultsStats(results: List[ClassifyResult]) = {
    val tuple = results.foldLeft((0,0)) { case ((numSuccess, numFailure), classifyResult) =>
      if(classifyResult.predicted == classifyResult.actual) { (numSuccess + 1, numFailure) }
      else (numSuccess, numFailure + 1)
    }
    val total = tuple._1 + tuple._2
    val ratio = tuple._1.toDouble / (tuple._1.toDouble + tuple._2.toDouble)
    s"Went over $total results. Got ${tuple._1} right and ${tuple._2} wrong. That means ${ratio * 100}% got classified correctly!"
  }
  
  // to draw the numbers
  def shade(value: Int): String = value match {
    case 0                => " "
    case _ if value < 64  => "░"
    case _ if value < 128 => "▒"
    case _ if value < 192 => "▓"
    case _                => "█"
  }


  def main( args: Array[String] ): Unit = {
    val trainingObservations = Source.fromFile("trainingsample.csv").getLines
            .drop(1)
            .map(_.split(',').map(_.toInt).toList)
            .toList
            .map(list => Observation(list.head, list.tail))

    val validationObservations = Source.fromFile("validationsample.csv").getLines
      .drop(1)
      .map(_.split(',').map(_.toInt).toList)
      .toList
      .map(list => Observation(list.head, list.tail))

    val validationResults = validationObservations.par.map(validationObservation => ClassifyResult(classify(trainingObservations, validationObservation.pixels), validationObservation.label))

    println(resultsStats(validationResults.toList))

    //validationObservations.tail.map(_.pixels.map(shade).grouped(28).toList.map(_.mkString).mkString("\n")).foreach(println)
  }
}

