package com.mavendemo.springtx;

import org.apache.log4j.Logger;

public class QuartzJobNoExtendsQuartzJobBean{
	private static Logger logger = Logger.getLogger(QuartzJobNoExtendsQuartzJobBean.class);
	protected void doJob(){
		logger.debug("Service This is a debug message.");
        logger.info("Service This is a info message.");
        logger.warn("Service This is a warn message.");
        logger.error("Service This is a error message.");
	}
}
