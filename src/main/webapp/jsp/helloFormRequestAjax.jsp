<%@page import="com.alibaba.druid.pool.DruidDataSource"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; utf-8"
    pageEncoding="utf-8"%>
<% String basePath = request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+request.getContextPath()+"/";%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<link/>
<link href="../css/bootstrap.min.css" rel="stylesheet"  />
<script src="../js/jquery-3.2.1.min.js" type="text/javascript" ></script>
<script src="../js/bootstrap.min.js" type="text/javascript" ></script>
<script>
/* 一、定义全局变量    */
 	/*1.java对象赋值js对象*/
	var basePath= "<%=basePath%>";
/* 二、定义全局js或者jquery函数（定义jquery函数：直接使用jquery库函数）   */
 	//ajax回调
 	function ajaxCallSuccess(result){
	console.log(result);
		alert(result);
		//$("div").html(result);
		//添加扩展方法
	}

	//不建议的写法
	//基础1：ajax请求Post，不携带参数
	function ajaxPost(url,callSuccessFn){
		$.ajax({type:'post',url:url,async:true,success:function(result){
			callSuccessFn(result);
		}});
	}
	//基础2：ajax请求Get，不携带参数
	function ajaxGet(url,callSuccessFn){
		$.ajax({type:'get',url:url,async:true,success:function(result){
			ajaxCallback(result);
		}});
	}
	//基础3：ajax请求Post，携带参数
	function ajaxPostData(url,reqJson,callSuccessFn){
		$.ajax({type:'post',url:url,data:reqJson,async:true,success:function(result){
			callSuccessFn(result);
		}});
	}
	//基础4：ajax请求Get，携带参数
	function ajaxGetData(url,reqJson,callSuccessFn){
		$.ajax({type:'get',url:url,data:reqJson,async:true,success:function(result){
			callSuccessFn(result);
		}});
	}
	//应用1：ajax请求Post，携带参数
	function ajaxPostFormId(url,formId,callSuccessFn){
		var reqJson = $("#"+formId).serialize();
		$.ajax({type:'post',url:url,data:reqJson,async:true,success:function(result){
			callSuccessFn(result);
		}});
		/* 请查原因：加上alert即可
		ajaxPostData(url,reqJson,callSuccessFn);
		*/
	}
	//应用2：ajax请求Get，携带参数
	function ajaxGetFormId(url,formId,callSuccessFn){
		var reqJson = $("#"+formId).serialize();
		$.ajax({type:'get',url:url,data:reqJson,async:true,success:function(result){
			callSuccessFn(result);
		}});
		/* 请查原因：加上alert即可
		ajaxGetData(url,reqJson,callSuccessFn);
		*/
	}
/* 三、执行jquery，在加载完毕document之后（都是加载完毕之后调用匿名函数） */
	/* jquery开始方式一
	$(document).ready(function(){

	});
	*/
	/* jquery开始方式二 */
	$(function(){
		/* 案例1：测试匿名函数的执行方法 */
		//调用匿名函数1.通过()包住2.结尾加()
		(function(testFunc){
			//alert("匿名函数测试，传入函数还不会用");
		})();
		/* 案例2：测试form表单提交并取消form表单的默认提交 */
		$("#form1Submit").click(function(){
			var url = basePath+"helloFormRequestAjax";
			//ajaxPost(url,ajaxCallSuccess);
			ajaxPostFormId(url,"form1Submit",ajaxCallSuccess);
			return false;//取消form表单的默认提交
		});
		/* 案例3：测试button的提交并取消button的默认提交 */
		$("button").click(function(){
			var url = basePath+"helloFormRequestAjax";
			//ajaxPost(url,ajaxCallSuccess);
			//ajaxPostData(url,"a",ajaxCallSuccess);
			ajaxPostFormId(url,"form1Submit",ajaxCallSuccess);
			return false;//取消button的默认提交
		});
	})


</script>
</head>
<body>
	<form id="form1" >
		姓名：<input id="name" name="name" value="" /><br/>
		年龄：<input id="age" name="age" value="" /><br/>
		<input id="form1Submit" type="submit" value="提交"><br/>
	</form>
	<div><h2> AJAX 可以修改文本内容</h2></div>
	<button id="test">修改内容</button>
</body>
</html>