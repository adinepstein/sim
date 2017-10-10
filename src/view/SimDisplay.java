package view;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class SimDisplay extends Canvas{

	int zoom;
	int x0,y0,width,height,mx,my;
	boolean rightClick;
	boolean showGrid;
	


	ArrayList<SimDrawable> drawables=Drawables.drawables;
	
	private class GridPaintListener implements PaintListener{

		@Override
		public void paintControl(PaintEvent e) {
			width=getSize().x;
			height=getSize().y;			
			//e.gc.drawString(x0+","+y0,10,10);
			
			int x=x0 -(x0/zoom)*zoom;
			int y=y0 -(y0/zoom)*zoom;

			if(showGrid){
				e.gc.setForeground(new Color(getDisplay(), 240,240,240));
				for(int i=y;i<height;i+=zoom){
					e.gc.drawString(""+(i-y0)/zoom, 0, i);
					e.gc.drawLine(0,i,width, i);
				}
				for(int j=x;j<width;j+=zoom){
					e.gc.drawString(""+(int)Math.round((j-x0)/zoom), j,0);
					e.gc.drawLine(j, 0, j, height);					
				}
			}
			
			
			if(drawables!=null && drawables.size()>0)
				for(SimDrawable d : drawables){
					double cx=d.getPosition().x;
					double cy=d.getPosition().y;
					d.draw(e, (int)Math.round(x0+cx*zoom), (int)Math.round(y0+cy*zoom), zoom);
				}
		}
		
	}
	
	
	public SimDisplay(Composite parent, int style) {
		super(parent, style);
		zoom=10;
		x0=400;
		y0=250;
		
		rightClick=false;
		showGrid=true;		
		
		setFocus();
		
		setBackground(new Color(getDisplay(), 255,255,255));
		addPaintListener(new GridPaintListener());
		
		addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if(rightClick){
					x0=e.x-mx;
					y0=e.y-my;
					redraw();
				}
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(rightClick)
					rightClick=false;
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				setFocus();
				rightClick=(e.button==3);
				mx=e.x-x0;
				my=e.y-y0;
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {				
				zoom+=e.count;
				zoom=Math.max(zoom, 2);
				redraw();
			}
		});

	}
	
	

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}


}
