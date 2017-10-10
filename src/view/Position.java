package view;

public class Position {
	
	public double x,y;
	
	public Position(double x,double y) {
		this.x=x;
		this.y=y;
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	
	public boolean equals(Position p){
		return (p.x==x && p.y==y);
	}

}
