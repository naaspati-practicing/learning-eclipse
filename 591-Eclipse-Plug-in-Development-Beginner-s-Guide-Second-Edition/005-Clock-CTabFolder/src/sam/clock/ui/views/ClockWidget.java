package sam.clock.ui.views;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ClockWidget extends Canvas {
	private int hour, minute, second;
	private int w, h, radius;
	private final Color secondHandColor,minuteHandColor,hourHandColor;
	private ZoneId zoneId;
	
	private static final double[] radians = new double[61];
	static {
		for (int i = 0, n = 0; i <= 360; i+=6, n++) 
			radians[n] = Math.toRadians(180 - i);
	}
	
	public ClockWidget(Composite composite, int style, Color hourHandColor, Color minuteHandColor, Color secondHandColor) {
		this(100, composite, style, hourHandColor, minuteHandColor, secondHandColor);
	}
	public ClockWidget(int radius, Composite composite, int style, Color hourHandColor, Color minuteHandColor, Color secondHandColor) {
		super(composite, style);
		this.radius = radius;
		
		this.secondHandColor = secondHandColor;
		this.minuteHandColor = minuteHandColor;
		this.hourHandColor = hourHandColor;
		
		addPaintListener(this::paint);
	}
	
	private void paint(PaintEvent e) {
		w = e.width;
		h = e.height;
		radius = Math.min(w, h)/2;
		radius -= radius/4;
		int x = w/2 - radius;
		int y = h/2 - radius;

		GC g2 = e.gc;
		g2.setLineWidth(3);
		g2.setAntialias(SWT.ON);
		g2.drawArc(x, y, radius*2, radius*2, 0, 360);
		
		if(zoneId == null)
			return;

		LocalDateTime t = LocalDateTime.now(zoneId); 
		hour = t.getHour();
		minute = t.getMinute();
		second = t.getSecond();

		draw(g2, second, 5, secondHandColor, 1);
		draw(g2, minute, 15, minuteHandColor, 2);
		draw(g2, (hour%12)*5, 35, hourHandColor, 4);
	}
	
	private void draw(GC g2, int index, int reduceLineSize, Color color, int width) {
		g2.setLineWidth(width);
		double rad = radians[index];
		g2.setForeground(color);
		g2.drawLine(w/2, h/2, (int)(w/2+(radius - reduceLineSize)*Math.sin(rad)), (int)(h/2+(radius - reduceLineSize)*Math.cos(rad)));
	}
	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}	
	public ZoneId getZoneId() {
		return zoneId;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName()+"@"+zoneId;
	}
}
