package al.franzis.osgi.weaving.core.equinox;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Bundle;

import al.franzis.osgi.weaving.core.equinox.hooks.EquinoxAdaptorHook;


public class ClassFilter {
	private static final String CLASSFILTER_FILE = "resources/classfilter.properties";
	private static final String INCLUDE_PROPERTY_NAME = "include";
	private static final String EXCLUDE_PROPERTY_NAME = "exclude";
	
	private String[] includes = new String[0];
	private String[] excludes = new String[0];
	
	public ClassFilter() {
		readFilterFile();
	}
	
	public boolean filter(String className) {
		for( int i = 0; i < includes.length; i++) {
			if(className.startsWith(includes[i]))
				return false;
		}
		
		for( int i = 0; i < excludes.length; i++) {
			if(className.startsWith(excludes[i]))
				return true;
		}
		
		return false;
	}
	
	private void readFilterFile() {
		try {
			URL url = getClassfilterUrl();
			if (url != null) {
				InputStream inputStream = url.openStream();
				if (inputStream != null) {
					Properties props = new Properties();
					props.load(inputStream);
					String include = props.getProperty(INCLUDE_PROPERTY_NAME);
					includes = parsePrefixes(include);
					String exclude = props.getProperty(EXCLUDE_PROPERTY_NAME);
					excludes = parsePrefixes(exclude);
					inputStream.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] parsePrefixes(String s) {
		return s.split(",");
	}
	
	private URL getClassfilterUrl() {
		URL url = null;
		for ( Bundle b : EquinoxAdaptorHook.getBundleContext().getBundles()) {
			if(b.getSymbolicName().equals(Constants.WEAVING_BUNDLE)) {
				url = b.getEntry(CLASSFILTER_FILE);
				System.out.println("Found the classfilter properties file at " + url);
				break;
			}
		
		}
		return url;
	}
	
}
