package boot;

import java.io.FileWriter;
import java.io.PrintWriter;

import simulator.MBDtest;
import simulator.PerimeterDefenseSimTask;
import view.SimulatorWindow;

public class RunExperiments {
	
	
	// test
	public static void main(String[] args) throws Exception{
		
		String outFile="results_roc/out7.txt";
		String scenarioFile="scenarios/sc7.txt";
		
		PrintWriter out=new PrintWriter(new FileWriter(outFile));
		System.out.println("start");
		for(int i=0;i<30;i++){
			MBDtest.reset();
			SimulatorWindow sw=new SimulatorWindow(800, 500, "my simulator",new PerimeterDefenseSimTask(scenarioFile),100,true,true);
			sw.start();
			try {
				sw.join();
				System.out.println("-----------");				
				sw=null;
				System.gc();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MBDtest.addScores();
		}
		MBDtest.printDiagnoses(out,0.0);
		out.close();
		
		
		FPRcalculator c=new FPRcalculator(outFile);
		c.normalize();
		c.rank();
		
	}

}
