package simulator;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import simulator.Agent.HeadingDependent;
import utils.Util;
import view.Drawables;
import view.Position;
import view.SimDrawable;

public class DetectionSensor implements HeadingDependent{
	
	public class Detection{
		public Position position;
		public double heading;
		public double speed;
		public double range;
		public double azimuth;
		public FoeAgent foeAgent;
	}
	
	double heading;	// degrees
	double range;
	double minRange;
	double span;		// degrees
	int offset;
	double deviation;
	
	public DetectionSensor(int headingOffset,double range, double span,double minRange) {
		this.offset=headingOffset;
		this.range=range;
		this.span=span;
		this.minRange=minRange;
		deviation=0;
	}
	
	
	
	
	public void draw(PaintEvent e,int x,int y,int r){
		if(span<360){
			int x1=(int)Math.round(x+range*r*Math.cos(Math.toRadians(heading-90-span/2)));
			int y1=(int)Math.round(y+range*r*Math.sin(Math.toRadians(heading-90-span/2)));
			
			int x2=(int)Math.round(x+range*r*Math.cos(Math.toRadians(heading-90+span/2)));
			int y2=(int)Math.round(y+range*r*Math.sin(Math.toRadians(heading-90+span/2)));
	
			e.gc.setForeground(new Color(null, 200,0,0));
			e.gc.drawLine(x, y, x1, y1);
			e.gc.drawLine(x, y, x2, y2);
			double rr=range*r;
			e.gc.drawArc((int)Math.round(x-rr), (int)Math.round(y-rr), (int)Math.round(rr*2),(int)Math.round( rr*2), (int)Math.round(180-heading-90-span/2), (int)Math.round(span));
		} else{
			e.gc.setForeground(new Color(null, 200,0,0));
			e.gc.drawOval((int)Math.round(x-range*r), (int)Math.round(y-range*r), (int)Math.round(range*r*2), (int)Math.round(range*r*2));
		}
	}



	public ArrayList<Detection> detect(Position p){
		ArrayList<Detection> d=new ArrayList<DetectionSensor.Detection>();
		for(SimDrawable sd : Drawables.drawables){			
			Position fp=sd.getPosition();
			double r=Math.sqrt((p.x-fp.x)*(p.x-fp.x) + (p.y-fp.y)*(p.y-fp.y));
			if(r>minRange && r<=range){				
				double azimuth=Util.getAzimuth(p.x, p.y, fp.x,fp.y);
				if(Util.isInSector(azimuth,heading,span)){
					if(sd instanceof FoeAgent){
						Detection g=new Detection();
						g.position=sd.getPosition();
						g.azimuth=azimuth+deviation;
						g.range=r;
						FoeAgent a=(FoeAgent)sd;
						g.speed=a.getSpeed();
						g.heading=a.getHeading();
						g.foeAgent=a;
						d.add(g);						
					}
				}
			}
			}
		return d;
	}


	
	@Override
	public void updateHeading(double heading) {
		this.heading=heading+offset;
	}

}
