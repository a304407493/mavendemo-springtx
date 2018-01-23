package com.mavendemo.springtx;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzJobExtendsQuartzJobBean extends QuartzJobBean{
	private static Logger logger = Logger.getLogger(QuartzJobExtendsQuartzJobBean.class);
	//调度工厂实例化后，经过timeout时间开始执行调度
	@SuppressWarnings("unused")
	private int timeout;
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("Service This is a debug message.");
        logger.info("Service This is a info message.");
        logger.warn("Service This is a warn message.");
        logger.error("Service This is a error message.");
	}
}
