package com.mavendemo.springtx.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mavendemo.springtx.AppService;

@Controller
public class AppController {
    private final static Logger LOGGER =  LoggerFactory.getLogger(AppService.class);
//返回值：非String(且不携带@ResponseBody)和ModelAndView都是直接访问
/******一、直接访问页面或文字(没有数据绑定)*****/
	//1.返回@ResponseBody|直接访问文字
		//1.1.response直接返回字符串：类似ajax
		@RequestMapping("/hello")
		@ResponseBody
	    public String test() {
	        return "直接访问页面并返回字符串：hello @ResponseBody";
	    }
		//1.2.get请求
		@RequestMapping(value="/helloGet", method = RequestMethod.GET)//②类级别的@RequestMapping窄化
		@ResponseBody
		public String helloGet() {
			return "get方式直接访问页面并返回字符串：hello method = RequestMethod.GET";
	    }
		//1.3.post请求
	    @RequestMapping(value="/helloPost", method = RequestMethod.POST)//③类级别的@RequestMapping窄化
	    @ResponseBody
	    public String helloPost() {
	    	return "post方式直接访问页面并返回字符串：hello method = RequestMethod.POST";
	    }
	//2.返回jsp前端界面(不仅仅是文字)|直接访问页面
	    //2.1.直接访问页面
	    @RequestMapping(value="/helloJsp")
	    public void helloJspVoid() {
	    }
	    //2.2.用于对比的转发
	    @RequestMapping(value="/helloJspZF")
	    public String helloJspZF() {
	    	return "testDruids";
	    }
/******二、直接访问页面或文字(有数据绑定)*****/
    //1.直接访问页面通过Map携带参数
	    //1.1.方法携带Map参数（建议）
	    @RequestMapping(value = "/helloVoidMap")
	    public void helloVoidMap(Map<String,Object> map) {
	    	map.put("a", "helloVoidMap");
	    }
	    //1.2.新建Map参数（不太建议）
	    @RequestMapping(value = "/helloVoidMapNewMap")
	    public void helloVoidMapNewMap() {
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	map.put("a", "helloVoidMapNewMap");
	    }
	    //1.3.新建Map参数（不建议多次一举）
	    @RequestMapping(value = "/helloMapNewMap")
	    public Map<String,Object> helloMapNewMap() {
	    	Map<String,Object> map = new HashMap<String,Object>();
	    	map.put("a", "helloMapNewMap");
	        return map;
	    }
	    //1.4.方法携带Map参数|返回值是String是转发
	    @RequestMapping(value = "/helloVoidMapZF")
	    public String helloVoidMapZF(Map<String,Object> map) {
	    	map.put("a", "helloVoidMap");
	    	return "helloVoidMapZF";
	    }
	//2.直接访问页面通过Model携带参数
	    //1.1.方法携带Model参数（建议）
	    @RequestMapping(value = "/helloModelVoid")
	    public void helloModelVoid(Model model) {//ui前端传入对象model：返回值为void直接访问jsp页面
	        model.addAttribute("a", "helloModelVoid");
	    }
	    //1.2.方法携带Model参数（建议）|返回值是String是转发
	    @RequestMapping(value = "/helloModelString")
	    public String helloModelString(Model model) {//ui前端传入对象model：返回值为string(controller分发处理，一般处理表单，另一个页面显示结果)
	        model.addAttribute("a", "helloModelString");
	        return "helloModelString";
	    }
/******二、表单访问页面(必须有数据绑定)标准样式*****/
	//1.新建一个java对象当作model放入到对象里|作为方法的参数
	//2.把表单的每个字段当作方法的参数
	//3.把request对象当作表单对象
	    @RequestMapping(value = "/helloFormRequestController" , method = RequestMethod.POST)
	    public String helloFormRequestController(HttpServletRequest req,Model model) {//ui前端传入对象model：返回值为string(controller分发处理，一般处理表单，另一个页面显示结果)
//	        LOGGER.info("表单请求：req："+JSONObject.toJSONString(req));
	    	model.addAttribute("a", "helloFormRequestController");
	        return "helloFormRequestAfter";
	    }
    //4.ajax可以表单访问当前页
	    @RequestMapping(value = "/helloFormRequestAjax", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	    @ResponseBody
	    public String helloFormRequestAjax(HttpServletRequest req) {//ui前端传入对象model：返回值为string(controller分发处理，一般处理表单，另一个页面显示结果)
//	        LOGGER.info("表单请求：req："+JSONObject.toJSONString(req));
	        Map<String,Object> map = new HashMap<String, Object>();
	        map.put("a","helloFormRequestAjax");
	        return JSONObject.toJSONString(map);
	    }
    //个人理解：直接请求的应该方法中不携带任何参数
    //个人理解：直接转(controller)，应该携带request和Model或者ModelAndView或者Map或者ModelMap
    //两种请求方式：1.url 2.form表单
    //1.url：直接访问
    //2.form表单提交到url进行业务层处理   处理完毕后进行转发操作(携带地址和数据)
    //绑定数据Model Map ModelMap ModelAndView
    //接受表单数据
	    /*
	   方式1：讲解如果返回单个对象的json；==>使用@ResponseBody来实现；注解方式
	 方式2：讲解如果返回多个对象的json；==>使用MappingJacksonJsonView来实现；xml配置方式
	 方式1-扩展：讲解如果返回多个对象的json；==>使用@ResponseBody来实现；注解方式*/
}
