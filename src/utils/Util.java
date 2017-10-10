package utils;

import simulator.DetectionSensor.Detection;

public class Util {

		public static double getAzimuth(double x,double y,double gx,double gy){
			double azimuth=90+180/Math.PI * Math.atan2( gy-y,gx-x);
			if(azimuth<0)
				azimuth=360+azimuth;
			return azimuth;
			
		}

		public static double getRange(double x, double y, double gx, double gy) {
			return Math.sqrt((gx-x)*(gx-x)+(gy-y)*(gy-y));
		}
		
		public static double getTime(double minRange,Detection d){
			double av=d.speed*Math.cos(Math.toRadians(Util.getDegDiff(d.azimuth,d.heading)));			
			if(av<0){
				return (d.range-minRange)/Math.abs(av);
			}
			else
				return Double.POSITIVE_INFINITY;
		}
		

		public static double set0to359(double deg){
			if(deg<0)
				deg+=360;
			if(deg>=360)
				deg=deg%360;
			return deg;
		}
		public static boolean isInSector(double deg,double heading,double span) {
			double startDeg=set0to359(heading-span/2);
			double stopDeg=set0to359(heading+span/2); 
			return  (deg>=startDeg && deg<=stopDeg)||
					(deg>=startDeg && deg<360 && stopDeg<startDeg)||
					(deg>=0 && deg<stopDeg && stopDeg<startDeg)||
					(startDeg==stopDeg && span>0);
		}
		
		public static double getDegDiff(double startDeg,double goalDeg){
			double d=goalDeg-startDeg;
			if(d>180)
				d=-(360-d);
			return d;
		}
		
}
