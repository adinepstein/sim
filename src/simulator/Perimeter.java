package simulator;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import view.Position;
import view.SimDrawable;

public class Perimeter implements SimDrawable {

	Position p;
	double size;
	public Perimeter(double x,double y,double size) {
		p=new Position(x, y);
		this.size=size;
	}
	
	@Override
	public Position getPosition() {
		return p;
	}

	@Override
	public void draw(PaintEvent e, int x, int y, int r) {
		e.gc.setForeground(new Color(null, 200, 200, 200));		
		e.gc.drawOval((int)(x-size*r), (int)(y-size*r), (int)size*r*2, (int)size*r*2);
	}

}
