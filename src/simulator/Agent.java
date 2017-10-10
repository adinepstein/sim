package simulator;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import view.Position;
import view.SimDrawable;

public class Agent implements SimDrawable {
	
	public interface HeadingDependent{
		void updateHeading(double heading);
	}

	Position p;
	double heading;	// degrees
	double speed;
	int serialNumber;
	

	ArrayList<HeadingDependent> headingDependents;
	
	public Agent(double x,double y,double heading) {
		p=new Position(x, y);
		this.heading=heading;
	}
	
	public void addHeadingDependent(HeadingDependent hd){
		if(headingDependents==null)
			headingDependents=new ArrayList<Agent.HeadingDependent>();
		hd.updateHeading(heading);
		headingDependents.add(hd);
	}
	
	public void removeHeadingDependent(HeadingDependent hd){
		if(headingDependents!=null)
			headingDependents.remove(hd);
	}
	
	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
		if(headingDependents!=null)
			for(HeadingDependent hd : headingDependents){
				hd.updateHeading(heading);
			}
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public Position getPosition() {
		return p;
	}
	
	public void step(){
		double x1=p.x+speed * Math.cos(Math.toRadians(heading-90));
		double y1=p.y+speed * Math.sin(Math.toRadians(heading-90));
		p.x=x1;
		p.y=y1;
	}

	@Override
	public void draw(PaintEvent e, int x, int y, int r) {
		e.gc.setForeground(new Color(null, 0, 0, 255));
		e.gc.drawOval(x-r/2, y-r/2, r, r);
		int x1=(int)Math.round(x+(r/3) * Math.cos(Math.toRadians(heading-90)));
		int y1=(int)Math.round(y+(r/3) * Math.sin(Math.toRadians(heading-90)));
		e.gc.drawLine(x, y, x1, y1);
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public int getSerialNumber() {
		return serialNumber;
	}
	
	@Override
	public int hashCode() {
		return serialNumber;
	}
}
