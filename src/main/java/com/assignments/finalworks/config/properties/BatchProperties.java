package com.assignments.finalworks.config.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
public class BatchProperties {

	public static final String BATCH_PROPERTIES_PREFIX = "app.batch";

	private List<String> ipList;
	private long port;
	private List<String> fieldList;
	private String collectCycle;
	private String deleteCycle;
	private String saveDir;
	private long ttl;

}
