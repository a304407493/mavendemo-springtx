package javacommon.aop;


import javacommon.diagnostic.Profiler;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceMonitorInterceptorAnnotation  {

    private final static Logger logger = Logger.getLogger(PerformanceMonitorInterceptorAnnotation.class);

    /** 以毫秒表示的阈值 */
    private int threshold = 250;
    @Around("execution(* com.mavendemo..*.*(..))")//环绕通知 doBasicProfiling    pjp可以修改  用于权限
    public Object bizInvoke(ProceedingJoinPoint pjp) throws Throwable {
        StringBuilder builder = new StringBuilder(64);
        builder.append(pjp.getTarget().getClass().getName());
        builder.append(".");
        builder.append(pjp.getSignature().toString());
        String name = builder.toString();
        Profiler.start("Invoking method: " + name);
        try {
            return pjp.proceed();
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

