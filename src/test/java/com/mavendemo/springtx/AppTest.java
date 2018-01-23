package com.mavendemo.springtx;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.pool.DruidDataSource;
import com.mavendemo.springtx.dao.PersonMapper;
import com.mavendemo.springtx.vo.Person;



/**
 * Unit test for simple App.
 */
public class AppTest  extends BaseTest
{
    private final static Logger LOGGER =  LoggerFactory.getLogger(AppTest.class);
	@Autowired
	private IAppService appService;
	/**
	 * 测试spring
	 */
	@Test
    public void testApp()
    {
		LOGGER.debug("11111{} {} a debug message.",new Object[]{"This","is"});
        LOGGER.info("11111{} {} a info message.",new Object[]{"This","is"});
        LOGGER.warn("11111{} {} a warn message.",new Object[]{"This","is"});
        LOGGER.error("11111{} {} a error message.",new Object[]{"This","is"});
		appService.processLog();
		System.out.println(appService);

    }
	@Autowired
	private DruidDataSource dataSource;
	/**
	 * 测试druid
	 */
	@Test
    public void testDruid()
    {
		System.out.println( dataSource );

    }
	/**
	 * 测试IAppService的事物
	 */
	@Test
	public void testAppServiceTransAction()
	{
		appService.processLog();
		
	}
}
