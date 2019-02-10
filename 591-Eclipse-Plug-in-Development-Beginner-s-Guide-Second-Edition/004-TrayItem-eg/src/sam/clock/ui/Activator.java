package sam.clock.ui;

import java.time.ZoneId;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sam.clock.ui.views.ClockWidget;


/**
 * The activator class controls the plug-in life cycle
 * 
 * see:: https://stackoverflow.com/questions/22064946/activator-start-method-never-called
 * 
 * You need to configure the Activator on the Overview tab of the plugin.xml editor.
 * There is also the Activate this plug-in when one of it classes is loaded option, if you specify this then the activator will not be run unless other code references it. If you don't specify this option then your activator will only be started if the run configuration says it should be started. If it is started it may start before the UI code is fully initialized.
 * All the above means is that the activator is the wrong place to put UI code.
 * 
 * to run: 
 *   run/debug (plugin.xml > overview > launch/debug a eclipse application) 
 *   start ClockView
 *   eclipse icon will be visible in windows tray
 *   click on it
 *   clock will be visible
 *   double click on it to close
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "004-TrayItem-eg"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private Image image;
	private TrayItem trayItem;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		Display display = Display.getDefault();
		display.asyncExec(() -> {
			image = new Image(display, getClass().getResourceAsStream("/icons/sample.gif"));
			Tray tray = display.getSystemTray();
			
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setToolTipText("Hello World");
			trayItem.setText("Hello World");
			trayItem.setVisible(true);
			trayItem.setImage(image);
			
			trayItem.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Shell shell = new Shell(display, SWT.ON_TOP | SWT.NO_TRIM);
					shell.setAlpha(255);
					Region region = new Region();
					region.add(0, 0, 200, 200);
					shell.setRegion(region);
					shell.setLayout(new FillLayout());
					ClockWidget c = new ClockWidget(shell, SWT.NONE, display.getSystemColor(SWT.COLOR_GREEN), display.getSystemColor(SWT.COLOR_BLUE), display.getSystemColor(SWT.COLOR_RED));
					c.setZoneId(ZoneId.systemDefault());
					c.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					c.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseDoubleClick(MouseEvent e) {
							shell.dispose();
						}
					});
					shell.pack();
					shell.open();

					Runnable repaint = c::redraw;
					shell.addDisposeListener(d -> region.dispose());
					
					Thread th = new Thread(() -> {
						System.out.println("started");

						while(true) {
							if(shell.isDisposed()) {
								System.out.println("STOPPED");
								break;
							}

							display.asyncExec(repaint);

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								System.out.println("stopped");
								break;
							}
						}
					});
					
					th.setDaemon(true);
					th.start();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) { }
			});
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		Display.getDefault()
		.asyncExec(() -> {
			if(trayItem != null)
				trayItem.dispose();
			if(image != null)
				trayItem.dispose();
		});
		super.stop(context);
		
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
