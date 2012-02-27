package al.franzis.osgi.weaving.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class ClassFilter {
	private static final String CLASSFILTER_FILE = "resources/classfilter.properties";
	private static final String INCLUDE_PROPERTY_NAME = "include";
	private static final String EXCLUDE_PROPERTY_NAME = "exclude";
	
	private String[] includes;
	private String[] excludes;
	
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
			InputStream inputStream = FileLocator.openStream(Activator.getBundleContext().getBundle(), new Path(CLASSFILTER_FILE), false);
			if ( inputStream != null )
			{
				Properties props = new Properties();
				props.load(inputStream);
				String include = props.getProperty(INCLUDE_PROPERTY_NAME);
				includes = parsePrefixes(include);
				String exclude = props.getProperty(EXCLUDE_PROPERTY_NAME);
				excludes = parsePrefixes(exclude);
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] parsePrefixes(String s) {
		return s.split(",");
	}
	
}
