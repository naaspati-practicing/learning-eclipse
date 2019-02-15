package sam.timezone.clock.jface.parts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TimeZoneContentProvider implements ITreeContentProvider {

	
	@Override
	public Object[] getChildren(Object o) {
		if(o instanceof Map)
			return cast(o, Map.class).entrySet().toArray();
		else if(o instanceof Map.Entry)
			return getChildren(cast(o, Entry.class).getValue());
		else if(o instanceof Collection) 
			return cast(o, Collection.class).toArray();
		else
			return new Object[0];
	}

	private <E> E cast(Object o, Class<E> cls) {
		return cls.cast(o);
	}

	@Override
	public Object[] getElements (Object inputElements) {
		return inputElements instanceof Object[] ? (Object[]) inputElements : new Object[0];
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public boolean hasChildren(Object o) {
		if(o instanceof Map)
			return !cast(o, Map.class).isEmpty();
		else if(o instanceof Map.Entry)
			return hasChildren(cast(o, Entry.class).getValue());
		else if(o instanceof Collection)
			return !cast(o, Collection.class).isEmpty();
		else
			return false;
	}

	@Override
	public void dispose() {
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
