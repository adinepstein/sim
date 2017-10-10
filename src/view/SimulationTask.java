package view;

import org.eclipse.swt.widgets.Listener;


public interface SimulationTask {

	void step();
	
	void addSimDoneListener(Listener listener);
}
