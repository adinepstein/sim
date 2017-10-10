package simulator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class MBDtest {
	
	
	public static class InterceptProcess{
		public FoeAgent foeAgent;
		public boolean assigned,locked,intercepted;
		public DefensingAgent cw,hpi;
		public MissileAgent m;
	}
	
	public static class Diagnosis{
		String description;
		ArrayList<Boolean> scenarios;
		@Override
		public int hashCode() {
			return description.hashCode();
		}
		
		public double getProb(){
			double count=0;
			for(Boolean b : scenarios)
				if(b)
					count++;
			return count/scenarios.size();
		}
	}
	
	private static HashMap<String,Double> allDiagnoses=new HashMap<>();
	private static HashMap<String,Diagnosis> diagnoses;
	
	public static HashMap<FoeAgent,InterceptProcess> observations;
	
	public static void reset(){
		diagnoses=new HashMap<>();
		observations=new HashMap<FoeAgent, MBDtest.InterceptProcess>();
	}
	
	
	private static void addDiganosis(String description,boolean desicion){		
		Diagnosis d=diagnoses.get(description);
		if(d==null){
			d=new Diagnosis();
			d.description=new String(description);
			d.scenarios=new ArrayList<>();
			diagnoses.put(description, d);
		}
		d.scenarios.add(desicion);		
	}
	
	public static void reportSuccess(FoeAgent f){
		InterceptProcess ip=observations.get(f);
		// intercept successful
		MissileAgent m=ip.m;
		addDiganosis("interceptor "+m.serialNumber+" dammaged sensor or movement",false);
		
		// lock successful
		DefensingAgent hpi=ip.hpi;
		addDiganosis("tracker "+hpi.serialNumber+" dammaged sensor or movement", false);
		addDiganosis("tracker "+hpi.serialNumber+" not aligned", false);

		// detection successful
		DefensingAgent cw=ip.cw;
		addDiganosis("detector "+cw.serialNumber+" dammaged sensor", false);
		addDiganosis("detector "+cw.serialNumber+" not aligned", false);
		addDiganosis("threat ordering mechanism is faulty", false);
		
	}
	
	public static void reportFailure(FoeAgent f){
		InterceptProcess ip=observations.get(f);
		MissileAgent m=ip.m;
		DefensingAgent hpi=ip.hpi;
		DefensingAgent cw=ip.cw;
		if(ip.locked){
			addDiganosis("interceptor "+m.serialNumber+" dammaged sensor or movement",true);
			addDiganosis("tracker "+hpi.serialNumber+" not aligned", true);

		}else
			
			if(ip.assigned){
				addDiganosis("tracker "+hpi.serialNumber+" dammaged sensor or movement", true);
				addDiganosis("detector "+cw.serialNumber+" not aligned", true);
				
			} else{
				//System.out.println("diagnosis: threat not considered");
				addDiganosis("threat ordering mechanism is faulty", true);
			}
	}

	
	public static void printDiagnoses(){
		for(Diagnosis d : diagnoses.values()){
			System.out.println(d.description+"\t"+d.getProb());
		}
	}
	
	public static void printDiagnoses(double p){
		for(Diagnosis d : diagnoses.values()){
			if(d.getProb()>p)
				System.out.println(d.description+"\t"+d.getProb());
		}
	}
	public static void printDiagnoses(PrintWriter out, double p){
		for(Diagnosis d : diagnoses.values()){
			if(d.getProb()>p){
				out.println(d.description+"\t"+d.getProb());
				out.flush();
			}
		}
	}
	
	
	public static int count=0;
	public static void printAllDiagnoses(PrintWriter out){
		for(String s : allDiagnoses.keySet()){
			out.println(s+"\t"+(allDiagnoses.get(s)/count));
			out.flush();
		}
	}


	public static void addScores() {
		for(String s : diagnoses.keySet()){
			if(!allDiagnoses.containsKey(s)){
				allDiagnoses.put(s, diagnoses.get(s).getProb());
			} else{
				allDiagnoses.put(s, allDiagnoses.get(s)+diagnoses.get(s).getProb());
			}
		}
		count++;
	}
}
