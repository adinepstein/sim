package simulator;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import utils.Util;

public class FoeAgent extends Agent {

	double gx,gy;
	private boolean madeIt;
	private int ton=0;
	private boolean engaged;
	private boolean stopped;
	int count;
	int inertia=15;
	public FoeAgent(double x, double y,double gx, double gy,double speed) {		
		super(x, y, Util.getAzimuth(x, y, gx, gy));
		this.gx=gx;		
		this.gy=gy;
		this.speed=speed;
		stopped=false;
	}

	@Override
	public void step(){
		if(!stopped){
			if(Util.getRange(p.x,p.y,gx,gy)>10){
				if(count%inertia==0){
					if(Math.random()>0.7){
						setHeading(heading-90+Math.random()*180);
						inertia=30;
					} else{
						setHeading(Util.getAzimuth(p.x, p.y, gx, gy));
						inertia=15;
					}
				}
				super.step();
				count++;
			} else{
				madeIt=true;
				stop();
				System.out.println("penetration");
				MBDtest.reportFailure(this);
			}
		}
	}

	@Override
	public void draw(PaintEvent e, int x, int y, int r) {
		if(!madeIt && !stopped){
			//super.draw(e, x, y, r);
			e.gc.setForeground(new Color(null, 255,0,0));
			e.gc.drawLine(x-r, y, x, y-r);
			e.gc.drawLine(x+r, y, x, y-r);
			e.gc.drawLine(x-r, y, x, y+r);
			e.gc.drawLine(x+r, y, x, y+r);
			
			int x1=(int)Math.round(x+ r * Math.cos(Math.toRadians(heading-90)));
			int y1=(int)Math.round(y+ r * Math.sin(Math.toRadians(heading-90)));
			e.gc.drawLine(x, y, x1, y1);
			
			if(ton>0 && ton!=Integer.MAX_VALUE)
				e.gc.drawString(""+ton, x+r, y+r,true);
				
		}
	}
	
	public void setThreatNumber(int ton){
		this.ton=ton;
	}

	public boolean isEngaged() {
		return this.engaged;
	}

	public void setEngaged(boolean b) {
		engaged=b;
	}

	public void stop() {
		stopped=true;
		setSpeed(0);
		setHeading(heading-180);
		ton=Integer.MAX_VALUE;
	}

	public boolean isStopped() {
		return stopped;
	}

	public int getThreatNumber() {
		return ton;
	}
	
}
