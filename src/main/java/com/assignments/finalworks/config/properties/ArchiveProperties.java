package com.assignments.finalworks.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.archive")
@Getter
@Setter
public class ArchiveProperties {

	private int fiveMinute;
	private int oneHour;
	private int sixHours;
	private int oneDay;
}
