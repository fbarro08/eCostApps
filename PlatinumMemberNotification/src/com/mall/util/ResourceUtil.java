package com.mall.util;

import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ResourceUtil {
	private final static Logger logger = Logger.getLogger(EmailUtility.class);
	private static ResourceUtil instance;
	
	private ResourceUtil(){
		
	}
	
    public static ResourceUtil getInstance() {
        if (instance == null) {
            instance = new ResourceUtil();
        }

        return instance;
    }
	
	
	public String getKeyValue(String str, Properties props) {
	    String retValue = null;

	    try {
	    	if(props!=null)
	    		retValue = props.getProperty(str);

		} catch (NullPointerException ex) {
			logger.error(ex);
		} catch (MissingResourceException ex) {
			logger.error(ex);
		} catch (Exception ex) {
			logger.error(ex);
		}

		return retValue;
	 }
}
