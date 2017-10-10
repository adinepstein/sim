package view;

import org.eclipse.swt.events.PaintEvent;

public interface  SimDrawable {

	
	Position getPosition();
	
	void draw(PaintEvent e, int x, int y, int zoom);
	
}
