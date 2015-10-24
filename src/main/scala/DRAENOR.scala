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
	def main(args: Array[String]){
		val sc = new SparkContext()
		val inputPath = args(0)
		val outputPath = args(1)
		val vertices= sc.textFile(inputPath)
			.map(line => Vectors.dense(line.split(",").map(_.toDouble)))
				.zipWithIndex.map(_.swap) // for tie breaking and preserving lexographical ordering
			
		
		def rbf(sigma:Double)(v1:Vector, v2:Vector): Double = Math.exp(-Vectors.sqdist(v1,v2)/(2*sigma*sigma)) 
		def inverseQuadratic(epsilon:Double) (v1:Vector,v2:Vector):Double = 1 / (1 + (epsilon*Vectors.sqdist(v1,v2)))
		val kernel = inverseQuadratic(5)_ //rbf(5)_
		val edges = vertices.cartesian(vertices)
				.filter({case ((l1,v1),(l2,v2)) => l1 <= l2}) //construct an edge only between vertices where label1 < label2
				// so that only half the the symmetric matrix is represented
				.map({case ((l1,v1),(l2,v2)) => Edge(l1,l2, kernel(v1,v2))})  // the weight of the edge is the kernel
		
		val graph = Graph(vertices, edges) 
		
		
		//computing degree
		val degreeGraph = Graph[Double,Double](graph.aggregateMessages( edgeContext =>{
									edgeContext.sendToSrc(edgeContext.attr)
									if ( edgeContext.srcId != edgeContext.dstId ) // send only once for self-connected vertex
										edgeContext.sendToDst(edgeContext.attr)
									}, _+_ ) , graph.edges)
		//computing normalized Laplacian I - D^0.5 A D^0.5	
		val normalizedLaplacianGraph:Graph[Double, Double] = degreeGraph.mapTriplets( ctx => {
											if (ctx.srcId != ctx.dstId)
												- ctx.attr / Math.sqrt(ctx.srcAttr*ctx.dstAttr) // off diagonal
											else
												1 - ctx.attr/ctx.srcAttr   // diagonal
											})
		val q= normalizedLaplacianGraph.triplets.map(t => (t.srcId,t.dstId,t.attr))

		q.saveAsTextFile(outputPath)

		//edges.collect()

		//graph.degree


	}

}
