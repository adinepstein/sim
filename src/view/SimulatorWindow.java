package view;


import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

public class SimulatorWindow extends MainWindow{

	Timer timer;
	TimerTask redrawTask;
	int rate,scale;
	boolean simIsRunning,autoStart,grid;
	SimDisplay sd;
	SimulationTask st;	
	
	private class RedrawTask extends TimerTask{
		@Override
		public void run() {
			if(!display.isDisposed())
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						if(!sd.isDisposed()){
							st.step();
							sd.redraw();
						}
					}
				});
		}
	}
	
	private void startSimulation(){
		simIsRunning=true;
		timer=new Timer();
		redrawTask=new RedrawTask();
		timer.scheduleAtFixedRate(redrawTask,0,rate);		
	}
	
	private void stopSimulation(){
		simIsRunning=false;
		if(redrawTask!=null)
			redrawTask.cancel();
		if(timer!=null)
			timer.cancel();
	}
	
	
	public SimulatorWindow(int width, int height, String title,SimulationTask st) {
		super(width, height, title);
		this.st=st;
		scale=1;
		autoStart=false;
		grid=true;
	}

	public SimulatorWindow(int width, int height, String title,SimulationTask st,int rate, boolean autoStart,boolean autoClose) {
		super(width, height, title);
		this.st=st;
		scale=rate;
		grid=false;
		this.autoStart=autoStart;
		if(autoClose){
			st.addSimDoneListener(new Listener() {
				
				@Override
				public void handleEvent(Event arg0) {
					display.asyncExec(new Runnable() {
						
						@Override
						public void run() {
							shell.close();
							shell.dispose();
						}
					});
				}
			});
		}		
	}
	
	@Override
	protected void initComponents() {
		shell.setLayout(new GridLayout(2, false));
		
		
		Button startButton=new Button(shell, SWT.SELECTED);
		startButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		startButton.setText("start");		
		
		
		sd=new SimDisplay(shell, SWT.BORDER);
		sd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		sd.redraw();
		
		Button stopButton=new Button(shell, SWT.SELECTED);
		stopButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		stopButton.setText("stop");
		stopButton.setEnabled(false);
		
		Label simRate=new Label(shell, SWT.None);
		simRate.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		simRate.setText("rate: "+scale+"/sec");
		
		Scale rateScale=new Scale(shell, SWT.HORIZONTAL);
		rateScale.setLayoutData(new GridData(SWT.NONE, SWT.TOP, false, false));		
		rateScale.setMinimum(1);
		rateScale.setMaximum(100);
		rateScale.setIncrement(1);
		rateScale.setSelection(scale);
		rate=1000/scale;
		
		
		Button gridButton=new Button(shell, SWT.CHECK);
		gridButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		gridButton.setText("grid");
		gridButton.setSelection(grid);
		sd.setShowGrid(grid);
		

		startButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				startSimulation();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				sd.setFocus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				System.out.println("defult selection event");
			}
		});
		
		if(autoStart){
			startSimulation();
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			sd.setFocus();
		}
		
		stopButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				sd.setFocus();
				stopSimulation();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		
		rateScale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				rate=1000/rateScale.getSelection();				
				if(simIsRunning){
					stopSimulation();
					startSimulation();
				}
				simRate.setText("rate: "+rateScale.getSelection()+"/sec");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		
		gridButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				sd.setShowGrid(gridButton.getSelection());
				sd.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
				
		
		
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				stopSimulation();
			}
		});
	}
	

}
