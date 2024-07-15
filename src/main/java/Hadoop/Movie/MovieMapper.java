package Hadoop.Movie;

import java.io.IOException;
import java.util.Formatter;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class MovieMapper extends MapReduceBase 
implements Mapper<LongWritable, Text, Text, DoubleWritable>{
	static String comboBox;
	String filterName, filterGenre , filterActor, filterDuration;
	Formatter formatter = new Formatter();
	
	@Override
	public void configure(JobConf job) {
		filterName = job.get("filterName", "all").toLowerCase();
		filterGenre = job.get("filterGenre", "all").toLowerCase();
		filterActor = job.get("filterActor", "all").toLowerCase();
		filterDuration = job.get("filterDuration", "all").toLowerCase();
	}
	
	public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter)
			throws IOException {
				
		String[] array = value.toString().split(";");

		if(comboBox.contains("списък филми")) {
			
			double realFilterDuration;
			
			try {
				realFilterDuration =  Double.valueOf(filterDuration);
	        } catch (NumberFormatException e) {
	        	realFilterDuration = 0.0;
	        }
			
			double time;
			
			try {
	            time = Double.valueOf(array[1]);
	        } catch (NumberFormatException e) {
	            time = 0;
	        }
			
			
			String name = array[2];
			String genre = array[3];
			String actor = array[4];
			String actress = array[5];
			
			if((filterName.equalsIgnoreCase("all") || name.toLowerCase().contains(filterName))
					&& (filterGenre.equalsIgnoreCase("all") || genre.toLowerCase().contains(filterGenre))
					&& (filterActor.equalsIgnoreCase("all") || actor.toLowerCase().contains(filterActor) || actress.toLowerCase().contains(filterActor))
					&& (filterDuration.equalsIgnoreCase("all") || time <= realFilterDuration)) {
				
				try {
					
					String result = String.format("%s %s %s %s %s",name, genre, actor, actress, time);
					output.collect(new Text(result), new DoubleWritable(time));
				
				} catch(NumberFormatException e) {
					System.err.println(value.toString());
				}
			}
		}
		
		if(comboBox.contains("средна продължителност по жарн")) {
			if(filterGenre.equalsIgnoreCase("all") || array[3].toLowerCase().contains(filterGenre)) {
			
				try {
				
					String genreMap = array[3];
					double time;
					
					try {
			            time =  Double.valueOf(array[1]);
			        } catch (NumberFormatException e) {
			            time = 0;
			        }
					
					formatter.format("%.2f", time);
				
					output.collect(new Text(genreMap), new DoubleWritable(time));
			
				} 
				catch(NumberFormatException e) {
						System.err.println(value.toString());
				}
			
			}
		}		
	}
}
