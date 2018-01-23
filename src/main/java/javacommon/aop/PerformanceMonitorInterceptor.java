package javacommon.aop;


import javacommon.diagnostic.Profiler;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;


public class PerformanceMonitorInterceptor implements MethodInterceptor {

    private final static Logger logger = Logger.getLogger(PerformanceMonitorInterceptor.class);

    /** 以毫秒表示的阈值 */
    private int threshold = 250;

    /**
     * 判断方法调用的时间是否超过阈值，如果是，则打印性能日志.
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
    	return bizInvoke(invocation);
    }
    /**
     * 判断方法调用的时间是否超过阈值，如果是，则打印性能日志.
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object bizInvoke(MethodInvocation invocation) throws Throwable {
        StringBuilder builder = new StringBuilder(64);
        builder.append(invocation.getMethod().getDeclaringClass().getName());
        builder.append(".");
        builder.append(invocation.getMethod().getName());
        String name = builder.toString();
        Profiler.start("Invoking method: " + name);
        try {
            return invocation.proceed();
        } finally {
            Profiler.release();
            if (!Profiler.isSuperStart()) {
                long elapseTime = Profiler.getDuration();

                if (elapseTime > threshold) {
                    StringBuilder builderTmp = new StringBuilder();
                    builderTmp.append(" method ").append(name);
                    // 执行时间超过阈值时间
                    builderTmp.append(" over PMX = ").append(threshold).append("ms,");
                    // 实际执行时间为
                    builderTmp.append(" used P = ").append(elapseTime).append("ms.\r\n");
                    builderTmp.append(Profiler.dump());
                    logger.info(builderTmp.toString());
                } else {
                    if (logger.isDebugEnabled()) {
                        StringBuilder builderTmp = new StringBuilder();
                        builderTmp.append("method").append(name);
                        // 实际执行时间为
                        builderTmp.append(" used P = ").append(elapseTime).append("ms.\r\n");
                        logger.debug(builderTmp.toString());
                    }
                }
            }
            Profiler.reset();
        }

    }
    // ----- 容器方法 ------

    /**
     * @param threshold
     *            The threshold to set.
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

}

