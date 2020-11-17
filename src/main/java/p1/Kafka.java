package p1;

import java.util.Properties;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;

public class Kafka {
	
	@SuppressWarnings({ "serial", "deprecation" })
	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env =  StreamExecutionEnvironment.getExecutionEnvironment();
		Properties p = new Properties();
		p.setProperty("bootstrap.servers", "localhost:9092");
		
		DataStream<String> kafkaData = env.addSource(new FlinkKafkaConsumer011<String>("test", new SimpleStringSchema(), p)); //topic test

		kafkaData.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
			public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
				String[] words = value.split(" ");
				for (String word : words)
					out.collect(new Tuple2<String, Integer>(word, 1));
			}	
		}).keyBy(words -> words.f0)
		  .sum(1)
		  .writeAsText("/home/flink/Documentos/kafka.txt");
		
		env.execute("Ejemplo Kafka");
    }

}

