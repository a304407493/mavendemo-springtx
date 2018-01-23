<%@page import="com.alibaba.druid.pool.DruidDataSource"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<%
	/*
	1.获取servletContext方式一
		ServletContext sc = request.getSession().getServletContext();
	2.获取servletContext方式二
		ServletContext sc = servlet.getServletContext();
	*/
	//3.获取servletContext方式三
	ServletContext sc = this.getServletContext();
	ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	DruidDataSource datasource =  applicationContext.getBean("dataSource",DruidDataSource.class);
	out.println("数据源:"+datasource+"<br/>");


	%>
</body>
</html>