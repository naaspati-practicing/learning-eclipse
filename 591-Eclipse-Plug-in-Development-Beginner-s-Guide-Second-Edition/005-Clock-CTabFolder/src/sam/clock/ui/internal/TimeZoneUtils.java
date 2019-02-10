package sam.clock.ui.internal;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public interface TimeZoneUtils {
	public static Map<String, Collection<ZoneId>> groupedTimeZones() {
		Map<String, Collection<ZoneId>> map = new TreeMap<>();
		Function<String, Collection<ZoneId>> compute = s -> new ArrayList<>();
		
		ZoneId.getAvailableZoneIds()
		.forEach(s -> {
			int n = s.indexOf('/');
			if(n < 0)
				return ;
			map.computeIfAbsent(s.substring(0, n), compute).add(ZoneId.of(s));
		});
		
		Comparator<ZoneId> comparator = Comparator.comparing(ZoneId::getId);
		map.forEach((s,t) -> ((List<ZoneId>)t).sort(comparator));
		return map;
	}

}
