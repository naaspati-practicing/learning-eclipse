package sam.clock.ui.views;

import java.time.ZoneId;
import java.util.Iterator;
import java.util.function.BiFunction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
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
	private Combo timeZones;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));

		Color second = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		Color minute = parent.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		Color hour = parent.getDisplay().getSystemColor(SWT.COLOR_GREEN);

		BiFunction<Integer, Integer, ClockWidget> instance = (width, height) -> {
			ClockWidget c = new ClockWidget(parent, SWT.NONE, hour, minute, second);
			c.setLayoutData(new RowData(width, height));
			return c;
		};
		ClockWidget[] clocks = {
				instance.apply(50, 50),
				instance.apply(100, 100),
				instance.apply(200, 200)
		};

		Iterator<String> itr = ZoneId.getAvailableZoneIds().iterator();
		
		timeZones = new Combo(parent, SWT.DROP_DOWN);
		timeZones.setVisibleItemCount(5);
		
		for (int i = 0; i < clocks.length - 1; i++) {
			String s = itr.next();
			clocks[i].setZoneId(ZoneId.of(s));
			timeZones.add(s);
		}
		
		itr.forEachRemaining(timeZones::add);
		ClockWidget clock = clocks[clocks.length - 1]; 

		clock.setZoneId(ZoneId.systemDefault());
		itr = null;

		Runnable repaint = () -> {
			for (ClockWidget c : clocks) 
				c.redraw();
		};
		
		timeZones.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				set(ZoneId.of(timeZones.getText()));
			}
			private void set(ZoneId z) {
				clock.setZoneId(z);
				clock.redraw();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				set(ZoneId.systemDefault());
			}
		});

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
	public void setFocus() {
		timeZones.setFocus();
	}

}
