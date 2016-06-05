// This Scala dojo is directly inspired by the
// F# Dojo-Digits-Recognizer project on Github,
// which in turn was directly inspired by the
// Digit Recognizer competition from Kaggle.com:
// http://www.kaggle.com/c/digit-recognizer
// The datasets below are simply shorter versions of
// the training dataset from Kaggle.

// The goal of the dojo will be to
// create a classifier that uses training data
// to recognize hand-written digits, and
// evaluate the quality of our classifier
// by looking at predictions on the validation data.

// This file provides some guidance through the problem:
// each section is numbered, and
// solves one piece you will need. Sections contain
// general instructions,
// [ YOUR CODE GOES HERE! ] tags where you should
// make the magic happen, and
// <SCALA QUICK-STARTER> blocks. These are small
// Scala tutorials illustrating aspects of the
// syntax which could come in handy. Run them,
// see what happens, and tweak them to fit your goals!

// 0. GETTING READY

// Open this file in IntelliJ IDEA as a worksheet, or
// create a new worksheet and copy the contents of this
// file into it

// <SCALA QUICK-STARTER>
// With IDEA's Scala worksheets  you can "live code"
// and see what happens.
// Scala IDE has similar functionality

// Try typing val x = 42 in the script file, the right
// side should update and show a: Int = 4

val myVal = 42

// val assigns the value on the right to a name

// Try now typing x + 3;; in the worksheet
// On the right, you should see "res0: Int = 45"
// res0 means the first result in the console. If
// you write another statement, you wil get res1, res2
// and so on. You can reuse these variables in other
// statements, like res0 + 5
myVal + 3

// Now right-click the following 2 lines and execute:
def greet(name: String) = println(s"Hello, $name")

// def defines a function
// greet is a function with one argument, name.
// You can call it:

greet("World")

// For this workbook, I suggest turning off "Interactive mode"
// Otherwise, it will be slow, since you will be loading a
// big file into memory each time. You can (by default)
// use Ctrl+Alt+W to evaluate the worksheet
// </SCALA QUICK-STARTER>

// Two data files are included in the same place you
// found this script:
// trainingsample.csv, a file that contains 5,000 examples, and
// validationsample.csv, a file that contains 500 examples.
// The first file will be used to train your model, and the
// second one to validate the quality of the model.

// 1. GETTING SOME DATA

// First let's read the contents of "trainingsample.csv"

// We will need scala.io.Source to load the file

import scala.io.Source

// the following might come in handy:
//Source.fromFile(path).getLines.toList
// returns a list of strings
// Each line in the file ends up being an element in the list

// [ YOUR CODE GOES HERE! ]
val importedLines = Source.fromFile("/home/thomas/code/scratch/Dojo-Digits-Recognizer/scala/trainingsample.csv").getLines.toList

// 2. EXTRACTING COLUMNS

// Break each line of the file into an array of string,
// separating by commas, using Array.split

// <SCALA QUICK-STARTER>
// map quick-starter:
// map can be called on collections and iterators, and transforms them
// into another collection by applying a function to each element.
// Example: starting from an array of strings:

val strings = List("Machine", "Learning", "with", "Scala", "is", "fun")

// We can transform it into a new array,
// containing the length of each string:

val lengths = strings.map(word => word.length)

// </SCALA QUICK-STARTER>

// The following function might help
val csvToSplit = "1,2,3,4,5"
val splitResult = csvToSplit.split(',')


// [ YOUR CODE GOES HERE! ]
val lines = importedLines.map(_.split(','))


// 3. CLEANING UP HEADERS

// Did you note that the file has headers? We want to get rid of them.

// <SCALA QUICK-STARTER>
// On collections, you can use .head and .tail to get the first element
// and the rest of the elements, respectively
strings
strings.head
strings.tail
// </SCALA QUICK-STARTER>


// [ YOUR CODE GOES HERE! ]
val tail = lines.tail

// 4. CONVERTING FROM STRINGS TO INTS

// Now that we have an array containing arrays of strings,
// and the headers are gone, we need to transform it
// into an array of arrays of integers.
// Array.map seems like a good idea again :)

// The following might help:
val castedInt = "42".toInt

// You can use nested maps to map nested lists
val nestedList = List(List(1,2,3), List(4,5,6)) // A list of lists
nestedList.map(list => list.map(number => number + 1))

// [ YOUR CODE GOES HERE! ]
val parsed = tail.map(_.map(_.toInt))

// 5. CONVERTING ARRAYS TO RECORDS

// Rather than dealing with a raw array of ints,
// for convenience let's store these into an array of Records

// <SCALA QUICK-STARTER>
// Case class quick starter: we can declare a
// case class (a lightweight, immutable class) type that way:

case class Example(label: Int, pixels: List[Int])

// and instantiate one this way:

val example = Example(1, List(1,2,3))

// </SCALA QUICK-STARTER>

// Tip: you can convert most collections to a List using .toList
val seq = Seq(1,2,3)
seq.toList

// [ YOUR CODE GOES HERE! ]
val trainingExamples = parsed.map(x => Example(x.head, x.tail.toList))

// 6. COMPUTING DISTANCES

// We need to compute the distance between images
// Math reminder: the euclidean distance is
// distance [ x1; y1; z1 ] [ x2; y2; z2 ] =
// sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2))

// <SCALA QUICK-STARTER>
// zip could come in handy here.
// Suppose we have 2 Lists:
val point1 = List(0, 1, 2)
val point2 = List(3, 4, 5)
//  zipping two collections yields a new collection of tuples
val zipped = point1 zip point2
val zipExample = zipped.map { case (x1, x2) => x1 + x2 }
// This simply computes the sums for point1 and point2,
// but we can easily turn this into a function now:
def zipPointsExample (p1: List[Int]) (p2: List[Int]) = {
  p1.zip(p2).map { case(x1, x2) => x1 + x2 }
}
// </SCALA QUICK-STARTER>


// Having a function like

def distance (p1: List[Int]) (p2: List[Int]) = 42

// would come in very handy right now,
// except that in this case,
// 42 is likely not the right answer

// [ YOUR CODE GOES HERE! ]
def distance(p1: List[Int], p2: List[Int]): Double = {
  p1.zip(p2).map { case (x, y) => (x - y) * (x - y) }.sum
}


// 7. WRITING THE CLASSIFIER FUNCTION

// We are now ready to write a classifier function!
// The classifier should take a set of pixels
// (an array of ints) as an input, search for the
// closest example in our sample, and predict
// the value of that closest element.

// <SCALA QUICK-STARTER>
// Array.minBy can be handy here, to find
// the closest element in the Array of examples.
// Suppose we have an Array of Example:
val someData = List(
  Example(0, List(1,2,3)),
  Example(1, List(4,5,6)),
  Example(2, List(7,8,9))
)
// We can find for instance
// the element with largest first pixel
val findThatGuy = someData.minBy(x => x.pixels(0))
// </SCALA QUICK-STARTER>


// <SCALA QUICK-STARTER>
// Scala and closures work very well together
val immutableValue = 42
def functionWithClosure(x: Int) = {
  if(x > immutableValue) // using outside value
  true else false
}
// </SCALA QUICK-STARTER>

// The classifier function should probably
// look like this - except that this one will
// classify everything as a 0:

def classifyExample(unknown: List[Int]) =
  // do something smart here
  // like find the Example with
  // the shortest distance to
  // the unknown element...
  // and use the training examples
  // in a closure...
  0

// [ YOUR CODE GOES HERE! ]

def classify(unknown: List[Int]): Int = trainingExamples
  .map(observation => (observation.label, distance(observation.pixels, unknown)))
  .minBy(_._2)
  ._1


// 8. EVALUATING THE MODEL AGAINST VALIDATION DATA

// Now that we have a classifier, we need to check
// how good it is.
// This is where the 2nd file, validationsample.csv,
// comes in handy.
// For each Example in the 2nd file,
// we know what the true Label is, so we can compare
// that value with what the classifier says.
// You could now check for each 500 example in that file
// whether your classifier returns the correct answer,
// and compute the % correctly predicted.


// [ YOUR CODE GOES HERE! ]

val validation = Source.fromFile("/home/thomas/code/scratch/Dojo-Digits-Recognizer/scala/validationsample.csv").getLines.toList.map(_.split(',')).tail.map(_.map(_.toInt)).map(x => Example(x.head, x.tail.toList))

case class ClassifyResult(predicted: Int, actual: Int)

def resultsStats(results: List[ClassifyResult]) = {
  val tuple = results.foldLeft((0,0)) { case ((numSuccess, numFailure), classifyResult) =>
    if(classifyResult.predicted == classifyResult.actual) { (numSuccess + 1, numFailure) }
    else (numSuccess, numFailure + 1)
  }
  val total = tuple._1 + tuple._2
  val ratio = tuple._1.toDouble / (tuple._1.toDouble + tuple._2.toDouble)
  s"Went over $total results. Got ${tuple._1} right and ${tuple._2} wrong. That means ${ratio * 100}% got classified correctly!"
}

val validationResults = validation.par.map(validationObservation => ClassifyResult(classify(validationObservation.pixels), validationObservation.label))

resultsStats(validationResults.toList)
