package com.mavendemo.springtx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	private static Logger logger = Logger.getLogger(App.class);
    private final static Logger LOGGER =  LoggerFactory.getLogger(AppServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		System.out.println("hello");
		LOGGER.debug("{} {} a debug message.",new Object[]{"This","is"});
        LOGGER.info("{} {} a info message.",new Object[]{"This","is"});
        LOGGER.warn("{} {} a warn message.",new Object[]{"This","is"});
        LOGGER.error("{} {} a error message.",new Object[]{"This","is"});
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        IAppService appService = context.getBean("appService",IAppService.class);
        appService.processLog();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
