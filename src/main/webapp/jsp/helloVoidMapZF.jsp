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
	直接访问页面，返回值String；携带参数controller的方法入参携带map,进行转发
	helloModelString：返回值String，Controller分发器，用于表单提交Controller再返回到另一个jsp输出结果的形式,输出结果：${a}
</body>
</html>