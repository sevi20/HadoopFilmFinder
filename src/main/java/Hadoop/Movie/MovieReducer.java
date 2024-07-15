package Hadoop.Movie;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class MovieReducer extends MapReduceBase 
implements Reducer<Text,DoubleWritable,Text,DoubleWritable>{

	String comboBox = MovieMapper.comboBox;
	
	public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output,
			Reporter reporter) throws IOException {
		// TODO Auto-generated method stub
		
		if(key == null || !StringUtils.isNotBlank(key.toString())) {
			return;
		}
		if (comboBox.contains("средна продължителност по жарн")) {
			double sum = 0;
			double count = 0;
			
			while(values.hasNext()) {
				count++;
				sum += values.next().get();
			}
			
			output.collect(key, new DoubleWritable(sum / count));
		} 
		else {
			output.collect(key, null);
		}
	}
}
