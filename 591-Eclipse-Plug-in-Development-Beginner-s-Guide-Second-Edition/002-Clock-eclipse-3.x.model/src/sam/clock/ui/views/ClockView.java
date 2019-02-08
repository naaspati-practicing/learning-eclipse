package sam.clock.ui.views;

import java.time.LocalDateTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ClockView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "sam.clock.ui.views.ClockView";

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		final Canvas clock = new Canvas(parent, SWT.NONE);
		clock.addPaintListener(p -> paintClock(p));
		
		Runnable repaint = clock::redraw;
		
		Thread t = new Thread(() -> {
			System.out.println("started");
			while(true) {
				update();
				if(clock.isDisposed()) {
					System.out.println("stopped");
					break;
				}
				clock.getDisplay().asyncExec(repaint);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("stopped");
					break;
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	final int radius = 100; 

	private void paintClock(PaintEvent e) {
		w = e.width;
		h = e.height;
		int x = w/2 - radius;
		int y = h/2 - radius;

		GC g2 = e.gc;
		g2.setLineWidth(3);
		g2.setAntialias(SWT.ON);
		g2.drawArc(x, y, radius*2, radius*2, 0, 360);

		// update();
		draw(g2, second, 5, e.display.getSystemColor(SWT.COLOR_RED), 1);
		draw(g2, minute, 15, e.display.getSystemColor(SWT.COLOR_BLUE), 2);
		draw(g2, (hour%12)*5, 35, e.display.getSystemColor(SWT.COLOR_GREEN), 4);
		
	}
	
	private final double[] radians = new double[61];
	{
		for (int i = 0, n = 0; i <= 360; i+=6, n++) 
			radians[n] = Math.toRadians(180 - i);
	}

	int hour, minute, second;
	int w, h;
	
	void update() {
		LocalDateTime t = LocalDateTime.now(); 
		hour = t.getHour();
		minute = t.getMinute();
		second = t.getSecond();
	}
	private void draw(GC g2, int index, int reduceLineSize, Color color, int width) {
		g2.setLineWidth(width);
		double rad = radians[index];
		g2.setForeground(color);
		g2.drawLine(w/2, h/2, (int)(w/2+(radius - reduceLineSize)*Math.sin(rad)), (int)(h/2+(radius - reduceLineSize)*Math.cos(rad)));
	}

	@Override
	public void setFocus() { }

}
