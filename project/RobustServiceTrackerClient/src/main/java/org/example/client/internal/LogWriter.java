package org.example.client.internal;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LogWriter implements LogListener {

	@Override
	public void logged(LogEntry entry) {
		System.out.println(entry.getMessage());
	}

}
