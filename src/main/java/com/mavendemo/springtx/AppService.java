package com.mavendemo.springtx;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mavendemo.springtx.dao.PersonMapper;
import com.mavendemo.springtx.vo.Person;
@Service("appService")
public class AppService implements IAppService{
//	private static Logger logger = Logger.getLogger(AppService.class);
    private final static Logger LOGGER =  LoggerFactory.getLogger(AppService.class);
    @Value(value="${username}")
//    @Value("#{configProperties['username']}")
//    @Value("#{apps['username']}")
    private String hello;
    
    @Autowired
    private PersonMapper personMapper;
    /*log4j
	public void processLog(){
		logger.debug("Service This is a debug message.");
        logger.info("Service This is a info message.");
        logger.warn("Service This is a warn message.");
        logger.error("Service This is a error message.");
	}
	*/
//    @Transactional(value="transactionManager",rollbackFor=Exception.class)
//    @Transactional(rollbackFor=Exception.class)
//    @Transactional
	public void processLog(){
		Person record = new Person();
		record.setId(1111);
		record.setName("1111");
		record.setPassword("111111");
		personMapper.insert(record );
		LOGGER.info("{} {} hello {}",new Object[]{"This","is",hello});
		LOGGER.debug("{} {} a debug message.",new Object[]{"This","is"});
        LOGGER.info("{} {} a info message.",new Object[]{"This","is"});
        LOGGER.warn("{} {} a warn message.",new Object[]{"This","is"});
        LOGGER.error("{} {} a error message.",new Object[]{"This","is"});
        //异常一：检查类型的异常，不进行回滚——IO异常和Timeout异常——继承Exception的异常都是不会滚的
//        try {
//			throw new IOException("");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        //异常二：Runtime异常，进行回滚
        throw new RuntimeException("");
        //异常三：自定义的异常——是否增加rollbackFor=Exception.class类解决不回滚的问题
//        int i=1/0;
	}
}
