package sam.clock.ui.views;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.COLOR_LIST_BACKGROUND;
import static org.eclipse.swt.SWT.H_SCROLL;
import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.SHADOW_ETCHED_IN;
import static org.eclipse.swt.SWT.V_SCROLL;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;

import sam.clock.ui.internal.TimeZoneUtils;

public class TimeZoneView extends ViewPart {
	private final AtomicReference<ClockWidget[]> activeClocks = new AtomicReference<ClockWidget[]>(null);

	@Override
	public void createPartControl(Composite parent) {
		CTabFolder tabs = new CTabFolder(parent, BOTTOM);
		Color background = parent.getDisplay().getSystemColor(COLOR_LIST_BACKGROUND);
		
		Color second = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		Color minute = parent.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		Color hour = parent.getDisplay().getSystemColor(SWT.COLOR_GREEN);
		
		Map<String, Collection<ZoneId>> map = TimeZoneUtils.groupedTimeZones();
		
		map.forEach((region, zones) -> {
			CTabItem item = new CTabItem(tabs, BOTTOM);
			item.setText(region);
			ScrolledComposite scroll = new ScrolledComposite(tabs, H_SCROLL | V_SCROLL);
			Composite clocks = new Composite(scroll, NONE);
			clocks.setBackground(background);
			scroll.setContent(clocks);
			item.setControl(scroll);
			clocks.setLayout(new RowLayout());
			
			ClockWidget[] array = new ClockWidget[zones.size()];
			item.setData(array);
			
			int n = 0;
			for (ZoneId z : zones) {
				Group group = new Group(clocks, SHADOW_ETCHED_IN);
				group.setLayout(new FillLayout());
				// group.setSize(200, 200);
				
				String s = z.getId();
				group.setText(s.substring(s.indexOf('/')+1));
				ClockWidget c = new ClockWidget(group, NONE, hour, minute, second);
				c.setZoneId(z);
				c.setToolTipText(s);
				
				array[n++] = c;	
			}
			
			scroll.setExpandHorizontal(true);
			scroll.setExpandVertical(true);
		});
		
		Runnable repaint = () -> {
			try {
				for (ClockWidget c : activeClocks.get())
					c.redraw();
			} catch (Exception e) { }
		};
		
		tabs.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				activeClocks.set((ClockWidget[]) tabs.getSelection().getData());
				repaint.run();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		
		tabs.setSelection(0);
		activeClocks.set((ClockWidget[]) tabs.getItem(0).getData());
		
		Thread th = new Thread(() -> {
			System.out.println("started");

			while(true) {
				if(parent.isDisposed()) {
					System.out.println("STOPPED");
					break;
				}

				parent.getDisplay().asyncExec(repaint);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("stopped");
					break;
				}
			}
		});
		
		th.setDaemon(true);
		th.start();
		
	}

	@Override
	public void setFocus() { }

}
