import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib._
import org.apache.spark.graphx._
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.spark.rdd._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.DenseVector

object DRAENOR {
/*		def myWithScope[U] (sc:SparkContext) (body: => U): U = RDDOperationScope.withScope(sc)(body)
		def assertNotStopped(sc:SparkContext) {
			if(sc.stopped.get()) throw new IllegalStateException("Cannot call methods on a stopped SparkContext")
		}
		def textFileWithLineNums(sc: SparkContext, path: String): RDD[(Long,String)] =  {
   			 assertNotStopped(sc)
    			sc.hadoopFile(path, classOf[TextInputFormat], classOf[LongWritable], classOf[Text], sc.defaultMinPartitions)
			.map(pair => (pair._1.get,pair._2.toString))
			///            ^^^^^ need this for lexographical order of verticies
		}

*/
	def main(args: Array[String]){
		val sc = new SparkContext()
		//val lines = textFileWithLineNums(sc,"data/inputData.csv")
		val vertices= sc.textFile("data/inputData.csv")
			.map(line => Vectors.dense(line.split(",").map(_.toDouble)))
				.zipWithIndex.map(_.swap)
			

		//val indices = vertices.map(_._1)
		
		def kernel(v1:Vector, v2:Vector): Double = Vectors.sqdist(v1,v2)
		val edges = vertices.cartesian(vertices).filter({case ((l1,v1),(l2,v2)) => l1!=l2}).map({case ((l1,v1),(l2,v2)) => Edge(l1,l2,kernel(v1,v2))}) 
		val graph = Graph(vertices, edges) 
		


	}

}
