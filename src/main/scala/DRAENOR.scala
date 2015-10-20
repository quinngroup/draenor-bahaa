import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib._
import org.apache.spark.graphx._

object DRAENOR {

	def main(args: Array[String]){
		val sc = new SparkContext()
		val lines = sc.textFile("data/inputData.csv")


	}

}
