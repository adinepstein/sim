package view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class MainWindow extends Thread{

	Display display;
	Shell shell;
	int width,height;
	String title;
	public MainWindow(int width,int height,String title) {
		this.width=width;
		this.height=height;
		this.title=title;
	}
	
	abstract protected void initComponents();
	
	@Override
	public void run(){
		
		display=new Display();
		shell=new Shell(display);
		shell.setSize(width, height);
		shell.setText(title);
		
		initComponents();
		shell.open();
		
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
				display.sleep();
		}
		
		display.dispose();
		
	}
}
