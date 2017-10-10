package boot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class FPRcalculator {

	HashMap<String, Double> diagnoses;
	String filename;
	
	public FPRcalculator(String fileName) throws Exception{
		this.filename=fileName;
		diagnoses=new HashMap<>();
		
		BufferedReader in=new BufferedReader(new FileReader(fileName));
		String line;
		while((line=in.readLine())!=null){
			String sp[]=line.split("\t");
			diagnoses.put(sp[0], Double.parseDouble(sp[1]));
		}		
		in.close();
	}
	
	public void normalize(){
		double sum=0;
		for(Double d : diagnoses.values())
			sum+=d;
		
		
		for(String diag : diagnoses.keySet()){
			Double d=diagnoses.get(diag);
			d=d/sum;
			diagnoses.put(diag, d);
		}
		
	}
	
	public void rank() throws Exception{
		PriorityQueue<Entry<String, Double >> q=new PriorityQueue<>(new Comparator<Entry<String, Double >>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return (int)(o2.getValue()*100000-o1.getValue()*100000);
			}
		});
		
		for(Entry<String, Double> e : diagnoses.entrySet()){
			q.add(e);
		}
		

		PrintWriter out=new PrintWriter(new FileWriter(filename+"r"));
		while(!q.isEmpty()){
			Entry<String, Double> e=q.poll();
			out.println(e.getKey()+"\t"+e.getValue());
		}
		out.flush();
		out.close();
	}
}
