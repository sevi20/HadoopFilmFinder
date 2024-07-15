package Hadoop.Movie;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

public class App extends JFrame {
	public static void main(String[] args) {
		App form = new App();
	}
	
	public App() {
		initialize();
	} 
	
	public void initialize() {
		JPanel panel = new JPanel();
		JButton button = new JButton("Search");
	  
	    TitledBorder border = new TitledBorder("MOVIE SEARCH SYSTEM");
	    border.setTitleJustification(TitledBorder.CENTER);
	    border.setTitlePosition(TitledBorder.TOP);
	    panel.setBorder(border);
	   
	    final JLabel nameLabel = new JLabel("Name:");
	    final JLabel genreLabel = new JLabel("Genre: ");
	    final JLabel actorLabel = new JLabel("Actor: ");
	    final JLabel durationLabel = new JLabel("Max duration: ");
	    final JLabel comboLabel = new JLabel("Result: ");   
	    
		final JTextField movieName = new JTextField();
		final JTextField genre = new JTextField();
		final JTextField actor = new JTextField();
		final JTextField maxDuration = new JTextField();
		
		String[] choices = {"Изберете", "списък филми", "средна продължителност по жарн"};
		final JComboBox<String> comboBox = new JComboBox<String>(choices);
		
		add(panel);
		//Labels
		panel.add(nameLabel);
		panel.add(genreLabel);
		panel.add(actorLabel);
		panel.add(durationLabel);
		panel.add(comboLabel);
		
		//Text fields
		panel.add(movieName);
		panel.add(genre);
		panel.add(actor);
		panel.add(maxDuration);
		
		panel.add(button);
		panel.add(comboBox);
		
		panel.setLayout(null);
		setSize(800,500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		comboBox.setBounds(350,60,200,35);
		comboLabel.setBounds(250,60,100,30);
		
		movieName.setBounds(350,120,200,35); 
		nameLabel.setBounds(250,120,100,30);
		
		genre.setBounds(350,180,200,35); 
		genreLabel.setBounds(250,180,100,30);
		
		actor.setBounds(350,240,200,35); 
		actorLabel.setBounds(250,240,100,30);
		
		maxDuration.setBounds(350,300,200,35); 
		durationLabel.setBounds(250,300,100,30);
		
		button.setBounds(300,360,200,35);
	
		comboBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (comboBox.getSelectedItem().equals("списък филми") || comboBox.getSelectedItem().equals("Изберете")) {
				      movieName.setVisible(true);
					  nameLabel.setVisible(true);
					  
				      actor.setVisible(true);
					  actorLabel.setVisible(true);
					  
					  maxDuration.setVisible(true);
					  durationLabel.setVisible(true);
					
				  }else if (comboBox.getSelectedItem().equals("средна продължителност по жарн")){
					  movieName.setVisible(false);
					  nameLabel.setVisible(false);
					  
					  actor.setVisible(false);
					  actorLabel.setVisible(false);
					  
					  maxDuration.setVisible(false);
					  durationLabel.setVisible(false);
				  }
			}
		});
		
		button.addActionListener(new ActionListener() {		
			
		
			
			public void actionPerformed(ActionEvent e) {
				
				
				String filterName = movieName.getText();
				String filterGenre = genre.getText();
				String filterActor = actor.getText();
				String filterDuration = maxDuration.getText();	
				MovieMapper.comboBox = comboBox.getSelectedItem().toString();
				
					if(filterName.isEmpty()) {
						filterName = "all";
					}
					
					if(filterGenre.isEmpty()) {
						filterGenre = "all";
					}
					
					if(filterActor.isEmpty()) {
						filterActor = "all";
					}
					
					if(filterDuration.isEmpty()) {
						filterDuration = "all";
					}
				
					try {
						runHadoop(filterName, filterGenre, filterActor, filterDuration);
					} catch (IOException e1) {
						e1.printStackTrace();
					}		
				
			}
		});
	}
	
	   public void runHadoop(String filterName, String filterGenre, String filterActor, String filterDuration) throws IOException {
	    	
	    	Configuration conf = new Configuration();
	    	
	    	JobConf job = new JobConf(conf, App.class);
	    	
	    	job.set("filterName", filterName);
	    	job.set("filterGenre", filterGenre);
	    	job.set("filterActor", filterActor);
	    	job.set("filterDuration", filterDuration);
	    	
	    	job.setMapperClass(MovieMapper.class);
	    	job.setReducerClass(MovieReducer.class);
	    	
	    	job.setOutputKeyClass(Text.class);
	    	job.setOutputValueClass(DoubleWritable.class);
	    	
	    	Path inputPath = new Path("hdfs://127.0.0.1:9000/input/film.csv");
	    	Path outputPath = new Path("hdfs://127.0.0.1:9000/output/film");
	    	
	    	FileInputFormat.setInputPaths(job, inputPath);
	    	FileOutputFormat.setOutputPath(job, outputPath);
	    	
	    	FileSystem fs = FileSystem.get(URI.create("hdfs://127.0.0.1:9000"), conf);
	    	
	    	if(fs.exists(outputPath)) {
	    		fs.delete(outputPath,true);
	    	}
	    	
	    	JobClient.runJob(job);
	    
	    }
}
