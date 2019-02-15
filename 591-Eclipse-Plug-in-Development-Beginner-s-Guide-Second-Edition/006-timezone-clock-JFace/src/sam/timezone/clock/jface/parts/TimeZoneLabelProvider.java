package sam.timezone.clock.jface.parts;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

public class TimeZoneLabelProvider extends LabelProvider implements IStyledLabelProvider, IFontProvider {

	private final ISharedImages img;
	private final ImageRegistry imgReg;
	private final FontRegistry fontReg;

	public TimeZoneLabelProvider(ISharedImages img, ImageRegistry imgReg, FontRegistry fontReg) {
		this.img = img;
		this.imgReg = imgReg;
		this.fontReg = fontReg;
	}
	
	private <E> E cast(Object o, Class<E> cls) {
		return cls.cast(o);
	}

	@Override
	public String getText(Object e) {
		if(e instanceof Map)
			return "Time zones";
		else if(e instanceof Entry)
			return cast(e, Entry.class).getKey().toString();
		else if(e instanceof ZoneId) {
			String s = cast(e, ZoneId.class).getId();
			return s.substring(s.indexOf('/')+1);
		} else
			return "Unknown type: " + e.getClass();
	}
	public Object[] getElements (Object inputElements) {
		return inputElements instanceof Object[] ? (Object[]) inputElements : new Object[0];
	}
	@Override
	public Image getImage(Object e) {
		if(e instanceof Map.Entry)
			return img.getImage(ISharedImages.IMG_OBJ_FOLDER);
		else if(e instanceof ZoneId)
			return imgReg.get(TimeZoneTreeView.IMG_SAMPLE);
		else
			return super.getImage(e);
	}

	@Override
	public Font getFont(Object element) {
		return fontReg.getItalic(JFaceResources.DEFAULT_FONT);
	}

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);
		StyledString ss = new StyledString(text);
		if(element instanceof ZoneId ) {
			ZoneId z = (ZoneId)element;
			ZoneOffset offset = ZonedDateTime.now(z).getOffset();
			ss.append("("+offset+")", StyledString.DECORATIONS_STYLER);
		}
		return ss;
	}

}
