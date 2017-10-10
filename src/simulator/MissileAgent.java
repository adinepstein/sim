package simulator;

import java.util.ArrayList;

import simulator.DetectionSensor.Detection;
import simulator.MBDtest.InterceptProcess;
import utils.Util;
import view.Position;

public class MissileAgent extends DefensingAgent{

	private boolean lunch,searching;
	private Detection goal;
	private DefensingAgent hpi;
	double maxSpeed;
	
	boolean ready;
	
	
	public MissileAgent(double x, double y, int heading,DetectionSensor sensor){
		super(x,y,heading,sensor);
		speed=0;
		rotationSpeed=6;
		maxSpeed=0.5;
		ready=true;
		searching=false;
	}
	
	public void setGoal(Detection d){
		goal=d;
	}
	
	public void lunch(){
		lunch=true;
		searchIndex=0;
		ready=false;
		searching=false;
	}
	
	@Override
	public void step(){
		if(lunch){
			if(!searching && Util.getRange(hpi.p.x, hpi.p.y, p.x, p.y)<hpi.getTarckedRange()){
				speed=Math.min(maxSpeed,hpi.getTarckedRange());
				Position t=hpi.getTargetPosition();
				setHeading(Util.getAzimuth(p.x, p.y, t.x, t.y)+rotationError);
				super.step();
			}
			else{
				search();
			}
			// detect
			DetectionSensor ds=sensors.get(0);
			ArrayList<Detection> detected=ds.detect(p);
			for(Detection d: detected){
				if(d.foeAgent==goal.foeAgent){
					goal.foeAgent.stop();
					lunch=false;
					hpi.stopTracking();
					
					//.............
					InterceptProcess ip=MBDtest.observations.get(goal.foeAgent);
					ip.intercepted=true;
					MBDtest.reportSuccess(goal.foeAgent);
					//.............
				}
			}			
		} else
			goHome();
	}
	
	private void goHome() {
		if(Util.getRange(p.x, p.y, 0, 0)>0.1){
			double diff=Util.getDegDiff(heading, Util.getAzimuth(p.x, p.y, 0, 0));
			double rspeed=Math.min(Math.abs(diff), rotationSpeed);
			if(diff<0)
				rspeed=-rspeed;
			setHeading(heading+rspeed);
			if(Math.abs(diff)<0.1){
				setSpeed(Math.min(maxSpeed, Util.getRange(p.x, p.y, 0, 0)));
				super.step();
			}
		}else
			ready=true;
	}
	
	public boolean isReady(){
		return ready;
	}

	private void search() {
		searching=true;
		speed=0;
		setHeading(heading + rotationSpeed);
		searchIndex++;
		if(searchIndex>=360/rotationSpeed){
			searching=false;
			lunch=false;
			hpi.stopTracking();
			//.............
			InterceptProcess ip=MBDtest.observations.get(goal.foeAgent);
			ip.intercepted=false;
			MBDtest.reportFailure(goal.foeAgent);
			//.............			
		}
	}

	public void setGuider(DefensingAgent hpi) {
		this.hpi=hpi;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed=maxSpeed;
	}
	

}
