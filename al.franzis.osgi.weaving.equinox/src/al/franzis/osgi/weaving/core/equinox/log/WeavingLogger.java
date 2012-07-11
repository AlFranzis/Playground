package al.franzis.osgi.weaving.core.equinox.log;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;

import al.franzis.osgi.weaving.core.equinox.hooks.EquinoxAdaptorHook;

public class WeavingLogger {
	private FrameworkLog frameworkLog;
	
	private static enum LOG_LEVEL { ERROR, INFO, DEBUG }
	/**
	 * Active Log Level
	 */
	private static LOG_LEVEL LOGLEVEL = LOG_LEVEL.ERROR;
	
	private WeavingLogger() {
		init();
	}
	
	public static WeavingLogger getInstance() {
		return new WeavingLogger();
	}
	
	public void info( String message) {
		if ( LOGLEVEL.ordinal() >= LOG_LEVEL.INFO.ordinal() ) {
			FrameworkLogEntry logEntry = new FrameworkLogEntry("WEAVING", FrameworkLogEntry.INFO, 1, message, 0, null, null);
			frameworkLog.log(logEntry);
		}
	}
	
	private void init() {
		frameworkLog = EquinoxAdaptorHook.getFrameworkLog();
	}
}
