package simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.eclipse.swt.widgets.Listener;

import simulator.DetectionSensor.Detection;
import simulator.MBDtest.InterceptProcess;
import utils.Util;
import view.Drawables;
import view.SimulationTask;

public class PerimeterDefenseSimTask implements SimulationTask {
	
	ArrayList<DefensingAgent> cw;
	ArrayList<DefensingAgent> hpi;
	ArrayList<FoeAgent> foes;
	ArrayList<MissileAgent> missiles;
	double minRange;
	int h=0;
	private Listener simDoneListener;
	
	public PerimeterDefenseSimTask(String scenatioFileName) {
		Drawables.reset();		
		try {
			BufferedReader in=new BufferedReader(new FileReader(scenatioFileName));
			double perimiterRange=Integer.parseInt(in.readLine().split(" ")[1]);
			Perimeter perimeter=new Perimeter(0, 0, perimiterRange);
			minRange=perimiterRange;

			int cws=Integer.parseInt(in.readLine().split(" ")[1]);
			in.readLine();
			String line;
			cw=new ArrayList<DefensingAgent>(cws);
			for(int i=0;i<cws;i++){
				line=in.readLine();
				String[] sp=line.substring(1).split("\t");
				cw.add(new DefensingAgent(0, 0,i*360/cws, new DetectionSensor(0, Double.parseDouble(sp[0]), 360/cws,minRange)));
				cw.get(i).setDetectionDeviation(Double.parseDouble(sp[1]));
				cw.get(i).setSerialNumber(i);
			}
						
			
			int hpis=Integer.parseInt(in.readLine().split(" ")[1]);
			hpi=new ArrayList<DefensingAgent>(hpis);
			for(int i=0;i<hpis;i++)
				hpi.add(new DefensingAgent(0, 0,i*360/hpis, new DetectionSensor(0, 40, 3,minRange)));
			
			in.readLine();
						
			int i=0;
			while((line=in.readLine()).startsWith("\t")){
				String[] sp=line.substring(1).split("\t");
				hpi.get(i).setRotationSpeed(Double.parseDouble(sp[0]));
				hpi.get(i).setRotationError(Double.parseDouble(sp[1]));
				hpi.get(i).setDetectionRange(Double.parseDouble(sp[2]));
				hpi.get(i).setDetectionDeviation(Double.parseDouble(sp[3]));
				hpi.get(i).setSerialNumber(i);
				i++;
			}
			int missileCount=Integer.parseInt( line.split(" ")[1]);
			in.readLine();
			
			missiles=new ArrayList<MissileAgent>(missileCount);
			for(i=0;i<missileCount;i++){
				MissileAgent m=new MissileAgent(0, 0,i*360/missileCount,new DetectionSensor(0, 4, 60,0));
				m.setSerialNumber(i);
				missiles.add(m);
			}
			
			
			i=0;
			while((line=in.readLine()).startsWith("\t")){
				String[] sp=line.substring(1).split("\t");
				MissileAgent m = missiles.get(i);
				m.setMaxSpeed(Double.parseDouble(sp[0]));
				m.setDetectionRange(Double.parseDouble(sp[1]));
				m.setRotationError(Double.parseDouble(sp[2]));
				i++;
			}
			
			int foesCount=Integer.parseInt( line.split(" ")[1]);
			
			Random r=new Random();
			foes=new ArrayList<FoeAgent>();
			for(i=0;i<foesCount;i++){
				boolean b=r.nextBoolean();
				double x=r.nextInt(41);
				double y=r.nextInt(41);
				if(b)
					x+=20;
				else
					y+=20;
				
				if(r.nextBoolean())
					x=-x;
				if(r.nextBoolean())
					y=-y;
				foes.add(new FoeAgent(x, y, 0, 0,0.05+r.nextDouble()*0.1));
			}
			
			Drawables.drawables.add(perimeter);
			for(DefensingAgent df : cw)
				Drawables.drawables.add(df);
			for(DefensingAgent df : hpi)
				Drawables.drawables.add(df);		
			
			for(FoeAgent a : foes)
				Drawables.drawables.add(a);
			
			for(MissileAgent m : missiles)
				Drawables.drawables.add(m);
			
			in.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public void step() {
		
		for(FoeAgent a : foes)
			a.step();
		
		for(DefensingAgent df : cw)
			df.step();
		
		ArrayList<Detection> detectedFoes=new ArrayList<Detection>();
		ArrayList<Detection> detected=new ArrayList<DetectionSensor.Detection>();
		for(DefensingAgent df : cw)
			detected.addAll(df.detect());
		
		
		sortByThreat(detected);
		
		int i=1;
		for(Detection d : detected){
			if(d.foeAgent!=null && !d.foeAgent.isStopped()){
				d.foeAgent.setThreatNumber(i);
				detectedFoes.add(d);
				i++;
			}
		}
		
		//......................
		for(DefensingAgent df : cw){
			for(Detection d : df.detect()){
				InterceptProcess ip=MBDtest.observations.get(d.foeAgent);
				if(ip==null){
					ip=new InterceptProcess();
					MBDtest.observations.put(d.foeAgent, ip);
				}
				ip.cw=df;
				ip.foeAgent=d.foeAgent;				
			}
		}		
		//......................

		i=0;
		int j=0;
		while(i<detectedFoes.size() && j<hpi.size()){
			FoeAgent fa=detectedFoes.get(i).foeAgent;
			if(!fa.isStopped() && !fa.isEngaged()){
				DefensingAgent selected=null;
				double hd=360;
				for(DefensingAgent df : hpi)
					if(!df.isTracking()){
						double diff=Util.getDegDiff(df.heading, Util.getAzimuth(df.p.x, df.p.y, fa.p.x, fa.p.y));
						if(hd>diff){
							selected=df;
							hd=diff;							
						}
					}
				if(selected!=null){
					selected.assign(detectedFoes.get(i),(i+1));					
					//.............
					InterceptProcess ip=MBDtest.observations.get(detectedFoes.get(i).foeAgent);
					ip.assigned=true;
					ip.hpi=selected;
					//.............
					j++;
				}
			}
			i++;
		}
		
		for(DefensingAgent df : hpi)
			if(df.isTracking()){
				df.track();
			}

		
		for(DefensingAgent df : hpi)
			if(df.isLocked()){
				ArrayList<Detection> dets=df.detect();
				if(dets!=null && dets.size()>0){
					if(dets.size()>1)
						Collections.sort(dets,new Comparator<Detection>() {
							@Override
							public int compare(Detection d1, Detection d2) {
								return d1.foeAgent.getThreatNumber()-d2.foeAgent.getThreatNumber();
							}
						});
					if(dets.get(0)!=null){
						if(!df.isFiring() && missiles.size()>0){
							
							MissileAgent m=null;
							int mi=0;
							while(mi<missiles.size() && !missiles.get(mi).isReady()) mi++;
							if(mi<missiles.size())
								m=missiles.get(mi);
							
							if(m!=null){							
								df.setFiring(true);
								m.setGuider(df);
								m.setGoal(dets.get(0));
								m.lunch();
								
							
								//.............
								InterceptProcess ip=MBDtest.observations.get(dets.get(0).foeAgent);
								ip.locked=true;
								ip.m=m;
								//.............
							}
						}
					}
				}			
			}
		
		for(MissileAgent m : missiles)
			m.step();
				
		if(simDoneListener!=null){			
			boolean allFoesStopped=true;
			for(FoeAgent fa : foes){
				if(!fa.isStopped())
					allFoesStopped=false;
			}			
			if(allFoesStopped){
				simDoneListener.handleEvent(null);
			}
		}
	}

	private void sortByThreat(ArrayList<Detection> detectedFoes) {
		Collections.sort(detectedFoes,new Comparator<Detection>() {

			@Override
			public int compare(Detection d1, Detection d2) {
				double t1=Util.getTime(minRange,d1);
				double t2=Util.getTime(minRange,d2);
				return (int)(t1-t2);
			}
		});
		
	}

	@Override
	public void addSimDoneListener(Listener listener) {
		simDoneListener=listener;		
	}


}
