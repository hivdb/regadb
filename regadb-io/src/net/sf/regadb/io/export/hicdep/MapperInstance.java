package net.sf.regadb.io.export.hicdep;

import net.sf.regadb.util.settings.HicdepConfig;

public class MapperInstance {
	public static Mapper getInstance(HicdepConfig.Mapping mapping) {
		if (mapping.type == HicdepConfig.Mapping.Type.Interval)
			return new IntervalMapper(Interval.parse(mapping.from), mapping.to);
		else if (mapping.type == HicdepConfig.Mapping.Type.String)
			return new StringMapper(mapping.from, mapping.to);
		else
			return null;
	}
}
