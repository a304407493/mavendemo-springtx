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
	<% String basePath = request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+request.getContextPath()+"/";%>

	<form action="<%=basePath%>helloFormRequestController" method="post">
		姓名：<input id="name" name="name" value="" /><br/>
		年龄：<input id="age" name="age" value="" /><br/>
		<input type="submit" value="提交"><br/>
	</form>
</body>
</html>