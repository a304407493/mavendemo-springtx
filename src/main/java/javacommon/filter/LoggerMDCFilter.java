package javacommon.filter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javacommon.diagnostic.Profiler;
import javacommon.utils.HttpHeaderUtils;
import javacommon.utils.LocalIPUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
/**
 * 存放在MDC中的数据，log4j可以直接引用并作为日志信息打印出来.
 * 
 * <pre>
 * 示例使用:
 * log4j.appender.stdout.layout.conversionPattern=%d [%X{loginUserId}/%X{req.remoteAddr}/%X{req.id} - %X{req.requestURI}?%X{req.queryString}] %-5p %c{2} - %m%n
 * </pre>
 * @author badqiu
 */
public class LoggerMDCFilter extends OncePerRequestFilter implements Filter{
	/** 以毫秒表示的性能的阈值 */
    private int threshold = 250;
    private String requestURIWithQueryString = "";
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain chain)throws ServletException,IOException {
        try {
            //示例为一个固定的登陆用户,请直接修改代码
            MDC.put("loginUserId", "demo-loginUsername");
            //URL
            String url = request.getRequestURL().toString();
            MDC.put("req.requestURL", StringUtils.defaultString(url));//得到请求的URL地址
            //请求参数
            MDC.put("req.queryString", StringUtils.defaultString(request.getQueryString()));//得到请求的URL地址中附带的参数
            //URL+请求参数
            requestURIWithQueryString = HttpHeaderUtils.getRequestURLWithParameter(((HttpServletRequest) request));
//            MDC.put("req.requestURIWithQueryString", request.getRequestURL() + (request.getQueryString() == null ? "" : "?"+request.getQueryString()));
            MDC.put("req.requestURIWithQueryString", requestURIWithQueryString);
            //客户端IP
            MDC.put("req.serverIp", StringUtils.defaultString(LocalIPUtils.getIp4Single()));
            String clientIp = HttpHeaderUtils.getClientIP(request);
            if (StringUtils.isNotBlank(clientIp) && clientIp.length() > 15) {
                clientIp = clientIp.split(",")[0];
                if (clientIp.length() > 15) {
                    clientIp = clientIp.substring(0, 15);
                }
            }
            //服务器端IP
            MDC.put("req.clinetIP", StringUtils.defaultString(clientIp));
            //来源referer(在哪个页面提交的请求)
            MDC.put("req.referer", StringUtils.defaultString(request.getHeader("Referer")));//页面来源
            //为每一个请求创建一个ID，方便查找日志时可以根据ID查找出一个http请求所有相关日志
            MDC.put("req.id", StringUtils.remove(UUID.randomUUID().toString(),"-")); 
            //为每一个session创建一个ID，方便查找日志时可以根据ID查找出一个http请求所有相关日志——放入一个context上下文中
            MDC.put("req.session", StringUtils.defaultString(request.getRequestedSessionId(),"-")); 
            //1.启动性能日志
            Profiler.start("Invoking URL: " + url);
            chain.doFilter(request, response);
        }finally {
        	//2.记录性能时间并打印日志
        	Profiler.release();//记录时间
        	recordPref(requestURIWithQueryString);//打印日志
        	//3.释放性能相关线程资源
        	Profiler.reset();
        	//清理log4j的MDC
            clearMDC();
        }
    }

    private void clearMDC() {
        Map<?, ?> map = MDC.getContext();
        if(map != null) {
            map.clear();
        }
    }
    private void recordPref(String requestURIWithQueryString){
    	long elapseTime = Profiler.getDuration();

        if (elapseTime > threshold) {
            StringBuilder builder = new StringBuilder();
            builder.append("URL:");
            builder.append(requestURIWithQueryString);
            //执行时间超过阈值时间
            builder.append(" over PMX = ").append(threshold).append("ms,");
            //实际执行时间为
            builder.append(" used P = ").append(elapseTime).append("ms.\r\n");
            //各自占用的时间
            builder.append(Profiler.dump());
            //记录日志
            logger.info(builder.toString());
        }
    }
}
