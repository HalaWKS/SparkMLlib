// $example on$
import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
// $example off$
import org.apache.spark.SparkConf;

public class ClfTest {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("JavaNaiveBayesExample");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        // $example on$
        String path = "data/mllib/sample_libsvm_data.txt";
        JavaRDD<LabeledPoint> inputData = MLUtils.loadLibSVMFile(jsc.sc(), path).toJavaRDD();
        JavaRDD<LabeledPoint>[] tmp = inputData.randomSplit(new double[]{0.6, 0.4});
        JavaRDD<LabeledPoint> training = tmp[0]; // training set
        JavaRDD<LabeledPoint> test = tmp[1]; // test set
        NaiveBayesModel model = NaiveBayes.train(training.rdd(), 1.0);
        JavaPairRDD<Double, Double> predictionAndLabel =
                test.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        double accuracy =
                predictionAndLabel.filter(pl -> pl._1().equals(pl._2())).count() / (double) test.count();

        System.out.println("nb accuracy = " + accuracy);

        // Save and load model
//        model.save(jsc.sc(), "data/model/myNaiveBayesModel");
//        NaiveBayesModel sameModel = NaiveBayesModel.load(jsc.sc(), "data/model/myNaiveBayesModel");
        // $example off$

        jsc.stop();
    }

}
