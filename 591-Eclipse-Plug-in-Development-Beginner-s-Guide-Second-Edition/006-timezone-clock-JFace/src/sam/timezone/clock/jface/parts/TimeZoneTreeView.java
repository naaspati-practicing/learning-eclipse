package sam.timezone.clock.jface.parts;

import java.time.ZoneId;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;

import sam.clock.ui.internal.TimeZoneUtils;
import sam.clock.ui.internal.TimeZoneViewerComparator;
import sam.clock.ui.internal.TimeZoneViewerFilter;

public class TimeZoneTreeView {
	public static final String IMG_SAMPLE = "sample";
	private TreeViewer tree;
	@Inject
	private ISharedImages images;

	@Inject
	private ESelectionService selectionService;
	private int launchCount;

	@PostConstruct
	public void createPartControl(Composite parent) {
		ResourceManager rm = JFaceResources.getResources();
		LocalResourceManager lrm = new LocalResourceManager(rm, parent);
		ImageRegistry imgs = new ImageRegistry(lrm);
		FontRegistry fontReg = new FontRegistry();

		imgs.put(IMG_SAMPLE, ImageDescriptor.createFromURL(getClass().getResource("/icons/Sample.gif")));

		tree = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		tree.setLabelProvider(new DelegatingStyledCellLabelProvider(new TimeZoneLabelProvider(images, imgs, fontReg)));
		tree.setContentProvider(new TimeZoneContentProvider());
		tree.setInput(new Object[] {TimeZoneUtils.groupedTimeZones()});
		tree.setData("REVERSE", Boolean.TRUE);
		tree.setComparator(new TimeZoneViewerComparator());
		// tree.setFilters(new TimeZoneViewerFilter("GMT"));
		tree.setExpandPreCheckFilters(true);
		tree.addDoubleClickListener(e -> {
			Viewer viewe = e.getViewer();
			java.util.Optional.ofNullable(viewe.getSelection())
			.filter(IStructuredSelection.class::isInstance)
			.map(IStructuredSelection.class::cast)
			.filter(s -> !s.isEmpty())
			.map(IStructuredSelection::getFirstElement)
			.filter(ZoneId.class::isInstance)
			.map(ZoneId.class::cast)
			.ifPresent(z -> {
				Shell shell = viewe.getControl().getShell();
				MessageDialog.openInformation(shell, z.getId(), z.toString());
			});
		});

		tree.addSelectionChangedListener(e -> java.util.Optional.ofNullable(e.getSelection())
				.map(s -> ((IStructuredSelection)s).getFirstElement())
				.filter(s -> selectionService != null)
				.ifPresent(selectionService::setSelection)
				);
		
		System.out.println("Launch count is: " + launchCount);
	}

	@Focus
	public void setFocus() {
		tree.getControl().setFocus();

	}
}
