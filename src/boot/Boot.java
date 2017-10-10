package boot;

import simulator.MBDtest;
import simulator.PerimeterDefenseSimTask;
import view.SimulatorWindow;

public class Boot {
	
	
	// test
	public static void main(String[] args) {
		MBDtest.reset();
		SimulatorWindow sw=new SimulatorWindow(800, 500, "my simulator",new PerimeterDefenseSimTask("scenarios/sc0.txt"));
		sw.start();
		try {
			sw.join();
			MBDtest.addScores();			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MBDtest.printDiagnoses(0.0);
		
		
	}

}
