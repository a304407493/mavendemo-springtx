package javacommon.diagnostic;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @Title: Profiler.java
 * @Copyright: Copyright (c) 2014
 * @Description: 性能诊断工具 使用场景：1. 定义在通用框架中，用于分析整体系统的实时性能 2.需要对特定个业务块做性能分析时</br>
 * @Company: ucfgroup.com
 * @Created on 2014-4-24 下午4:16:03
 * @author 刘文涛 [liuwentao@ucfgroup.com]
 */
public final class Profiler {

	//1.线程的本地变量Entry
    private static final ThreadLocal<Entry> entryStack = new ThreadLocal<Entry>();
    //2.线程的本地变量profiler
    private static ThreadLocal<Long> profilerContext = new ThreadLocal<Long>() {
        // 初始化值
        public Long initialValue() {
            return 0L;
        }
    };

    /**
     * 开始计时1 String
     * @param message
     */
    public static void start(String message) {
        if (isStart()) {//如果启动了直接通过enter添加
            enter(message);
        } else {//如果没有启动，当前线程设置Entry
            entryStack.set(new Entry(message, null, null));
        }
        profilerContext.set(profilerContext.get() + 1L);
    }
    /**
     * 开始计时2 Message
     * @param message
     */
    public static void start(Message message) {
        if (isStart()) {
            enter(message);
        } else {
            entryStack.set(new Entry(message, null, null));
        }
        profilerContext.set(profilerContext.get() + 1L);
    }

    /**
     * 置空的方式：entryStack置空和profilerContext置为0
     */
    public static void reset() {
        Long c = profilerContext.get();
        if (null != c && c.longValue() > 1) {
            profilerContext.set(profilerContext.get() - 1L);
        } else {//置空：两个线程本地变量置空
            entryStack.set(null);
            profilerContext.set(0L);
        }
    }

    /**
     * 添加子Entry：String
     * 根据message创建Entry，并加入到前一个Entry中(1.获取当前线程的Entry 2.获取Entry的entries 3.循环取出最后一个UnreleasedEntry)
     * @param message
     */
    public static void enter(String message) {
    	//1.准备工作：获取最后一个UnreleasedEntry
        Entry currentEntry = getCurrentEntry();//过程：获取当前线程的Entry的entries的最近的一个Entry(UNRELEASED为标识符的Entry)
        //2.工作：将通过message创建的Entry添加到最后一个Entry/UnreleasedEntry
        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);//在当前线程Entry的子Entry再加入一个entries属性
        }
    }
    /**
     * 添加子Entry：Message
     * 根据message创建Entry，并加入到前一个Entry中(1.获取当前线程的Entry 2.获取Entry的entries 3.循环取出最后一个UnreleasedEntry)
     * @param message
     */
    public static void enter(Message message) {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);
        }
    }
    /**
     * 结束最近的一个entry，记录结束时间。
     */
    public static void release() {
    	//1.获取最近的entry
        Entry currentEntry = getCurrentEntry();
        //2.结束entry并记录时间
        if (currentEntry != null) {
            currentEntry.release();
        }
    }
    /**
     * 取得耗费的总时间。
     * Returns:
	 * 	耗费的总时间，如果未开始计时，则返回-1
     */
    public static long getDuration() {
        Entry entry = (Entry) entryStack.get();

        if (entry != null) {
            return entry.getDuration();
        } else {
            return -1;
        }
    }
    /**
     * 列出所有的entry。
     * Returns:
	 * 	列出所有entry，并统计各自所占用的时间
     */
    public static String dump() {
        return dump("", "");
    }

    /**
     * 判断被父级方法调用过
     * 
     * @return
     */
    public static boolean isSuperStart() {
        return profilerContext.get() > 1L;
    }
    /**
     * 列出所有的entry1——通过调用toString
	 * Parameters:
	 *  prefix 前缀
	 * Returns:
	 *  列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix) {
        return dump(prefix, prefix);
    }
    /**
     * 列出所有的entry2——通过调用toString
	 * Parameters:
	 *  prefix1 首行前缀
	 *  prefix2 后续行前缀
	 * Returns:
	 *  列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix1, String prefix2) {
        Entry entry = (Entry) entryStack.get();

        if (entry != null) {
            return entry.toString(prefix1, prefix2);
        } else {
            return "";
        }
    }
    /**
     * 取得第一个entry。
     * Returns:
   	 *  第一个entry，如果不存在，则返回null
     * @return
     */
    public static Entry getEntry() {
        return (Entry) entryStack.get();
    }

    /**
     * 取得最近的一个entry。
     * Returns:
	 *  最近的一个entry，如果不存在，则返回null
     */
    private static Entry getCurrentEntry() {
    	//1.取得当前线程的Entry
        Entry subEntry = (Entry) entryStack.get();
        Entry entry = null;

        if (subEntry != null) {
            do {
                entry = subEntry;
                //2.获取最后一个Entry[通过当前线程的Entry获取entries属性的最后一个Entry/获取没有释放的或者没有计时的Entry]
                subEntry = entry.getUnreleasedEntry();
            } while (subEntry != null);
        }

        return entry;
    }

    /**
     * 工具：判断是否开始
     * @return
     */
    public static boolean isStart() {
        return null != getEntry() && getEntry().isStart();
    }

    /**
     * 代表一个计时单元：Entry
     * @author lenovo
     *
     */
    public static final class Entry {
        private final List<Entry> subEntries = new ArrayList<Entry>(4);

        private final Object message;

        private final Entry parentEntry;

        private final Entry firstEntry;

        private final long baseTime;//第一个Entry的startTime

        private final long startTime;//开始时间

        private long endTime;//结束时间

        private boolean isStart = false;//额外增加的
        
        /**
         * 创建一个新的entry。
		 * Parameters:
		 *  message entry的信息，可以是null
		 *  parentEntry 父entry，可以是null
		 *  firstEntry 第一个entry，可以是null
         */
        private Entry(Object message, Entry parentEntry, Entry firstEntry) {
            this.message = message;
            this.startTime = System.currentTimeMillis();
            this.parentEntry = parentEntry;
            this.firstEntry = (Entry) ObjectUtils.defaultIfNull(firstEntry, this);
            this.baseTime = (firstEntry == null) ? 0 : firstEntry.startTime;//第一个Entry的startTime
            this.isStart = true;
        }

        /**
         * 取得entry的信息。
         * @return
         */
        public String getMessage() {
            String messageString = null;

            if (message instanceof String) {
                messageString = (String) message;
            } else if (message instanceof Message) {
                Message messageObject = (Message) message;
                MessageLevel level = MessageLevel.BRIEF_MESSAGE;

                if (isReleased()) {
                    level = messageObject.getMessageLevel(this);
                }

                if (level == MessageLevel.DETAILED_MESSAGE) {
                    messageString = messageObject.getDetailedMessage();
                } else {
                    messageString = messageObject.getBriefMessage();
                }
            }

            return StringUtils.defaultIfEmpty(messageString, null);
        }
        
        /**
         * 取得entry相对于第一个entry的起始时间。
		 * Returns:
		 *  相对起始时间
         */
        public long getStartTime() {
            return (baseTime > 0) ? (startTime - baseTime) : 0;
        }
        /**
         * 取得entry相对于第一个entry的结束时间。
		 * Returns:
		 *  相对结束时间，如果entry还未结束，则返回-1
         * @return
         */
        public long getEndTime() {
            if (endTime < baseTime) {
                return -1;
            } else {
                return endTime - baseTime;
            }
        }
        /**
         * 取得当前entry持续的时间。
		 * Returns:
		 *  entry持续的时间，如果entry还未结束，则返回-1
         */
        public long getDuration() {
            if (endTime < startTime) {
                return -1;
            } else {
                return endTime - startTime;//本次的结束时间减去上次的结束时间
            }
        }

        /**
         * 取得entry自身所用的时间，即总时间减去所有子entry所用的时间。
		 * Returns:
		 *  entry自身所用的时间，如果entry还未结束，则返回-1
         */
        public long getDurationOfSelf() {
            long duration = getDuration();//Entry自身消耗的时间=endTime - startTime

            if (duration < 0) {
                return -1;
            } else if (subEntries.isEmpty()) {
                return duration;
            } else {
                for (int i = 0; i < subEntries.size(); i++) {
                    Entry subEntry = (Entry) subEntries.get(i);

                    duration -= subEntry.getDuration();//duration自身消耗的时间-各个子Entry消耗的时间=最终Entry消耗的时间
                }

                if (duration < 0) {
                    return -1;
                } else {
                    return duration;
                }
            }
        }
        /**
         * 工具：取得当前entry在父entry中所占的时间百分比。
         * Returns:
         *  百分比
         */
        public double getPecentage() {
            double parentDuration = 0;
            double duration = getDuration();//当前Entry的持续时间

            if ((parentEntry != null) && parentEntry.isReleased()) {
                parentDuration = parentEntry.getDuration();//上一个Entry/局部Entry即b的
            }

            if ((duration > 0) && (parentDuration > 0)) {
                return duration / parentDuration;
            } else {
                return 0;
            }
        }
        /**
         * 工具：取得当前entry在第一个entry中所占的时间百分比。
		 * Returns:
		 *  百分比
         * @return
         */
        public double getPecentageOfAll() {
            double firstDuration = 0;
            double duration = getDuration();//当前entry时间

            if ((firstEntry != null) && firstEntry.isReleased()) {
                firstDuration = firstEntry.getDuration();//总的
            }

            if ((duration > 0) && (firstDuration > 0)) {
                return duration / firstDuration;
            } else {
                return 0;
            }
        }
        /**
         * 工具：取得当前entry自身耗时在父entry中所占的时间百分比。
         * Returns:
         *  百分比
         */
        public double getPecentageBySelf() {
            double parentDuration = 0;
            double duration = getDurationOfSelf();//当前Entry的持续时间

            if ((parentEntry != null) && parentEntry.isReleased()) {
                parentDuration = parentEntry.getDuration();//上一个Entry/局部Entry即b的
            }

            if ((duration > 0) && (parentDuration > 0)) {
                return duration / parentDuration;
            } else {
                return 0;
            }
        }
        /**
         * 工具：取得当前entry自身耗时在第一个entry中所占的时间百分比。
		 * Returns:
		 *  百分比
         * @return
         */
        public double getPecentageOfAllBySelf() {
            double firstDuration = 0;
            double duration = getDurationOfSelf();//当前entry时间

            if ((firstEntry != null) && firstEntry.isReleased()) {
                firstDuration = firstEntry.getDuration();//总的
            }

            if ((duration > 0) && (firstDuration > 0)) {
                return duration / firstDuration;
            } else {
                return 0;
            }
        }
        /**
         * 取得所有子entries。
    	 * Returns:
		 *  所有子entries的列表（不可更改）
         * @return
         */
        public List<Entry> getSubEntries() {
            return Collections.unmodifiableList(subEntries);
        }
        /**
         * 结束当前entry，并记录结束时间。
         */
        private void release() {
            endTime = System.currentTimeMillis();
        }

        /**
         * 工具：判断当前entry是否结束。
         * 
         * @return 如果entry已经结束，则返回<code>true</code>
         */
        private boolean isReleased() {
            return endTime > 0;
        }

        /**
         * 创建一个新的子entry，并添加到调用该方法的那个Entry里面
		 * Parameters:
		 *  message 子entry的信息
         * @param message
         */
        private void enterSubEntry(Object message) {
        	//1.创建一个subEntry
            Entry subEntry = new Entry(message, this, firstEntry);
            //2.this.entries添加subEntry/一般只有Entry调用添加enterSubEntry|即this指父Entry或者上一个Entry
            subEntries.add(subEntry);
        }

        /**
         * 取得未结束的子entry。
		 * Returns:
		 *  未结束的子entry，如果没有子entry，或所有entry均已结束，则返回null
         * @return
         */
        private Entry getUnreleasedEntry() {
            Entry subEntry = null;
            //UNRELEASED为标识符
            //Entry的subEntries属性是空的时候。Entry是没有结束的子Entry
            if (!subEntries.isEmpty()) {
                subEntry = (Entry) subEntries.get(subEntries.size() - 1);

                if (subEntry.isReleased()) {
                    subEntry = null;
                }
            }

            return subEntry;
        }

        /**
         * toString 1
         */
        public String toString() {
            return toString("", "");
        }
        /**
         * toString 2
         */
        private String toString(String prefix1, String prefix2) {
            StringBuffer buffer = new StringBuffer();

            toString(buffer, prefix1, prefix2);

            return buffer.toString();
        }

        /**
         * toString 3
         * 输出
         * @param buffer
         * @param prefix1
         * @param prefix2
         */
        private void toString(StringBuffer buffer, String prefix1, String prefix2) {
            buffer.append(prefix1);

            String message = getMessage();//entry信息 
            long startTime = getStartTime();//Entry的起始时间/相对与第一个Entry的时间=第一个子Entry的开始时间 - 父Entry的开始时间
            long duration = getDuration();//当前Entry的持续总时间/endTime - startTime
            long durationOfSelf = getDurationOfSelf();//自身消耗的时间=Entry消耗的总时间-所有子Entry消耗的时间
            double percent = getPecentage();//在父entry中所占的时间比例
            double percentOfAll = getPecentageOfAll();//在总时间中所旧的时间比例 
            double percentBySelf = getPecentageBySelf();//在父entry中所占的时间比例
            double percentOfAllBySelf = getPecentageOfAllBySelf();//在总时间中所旧的时间比例 
            //准备1：替换的信息
            Object[] params = new Object[] { message, // {0} - entry信息
                                            new Long(startTime), // {1} - 相对起始时间
                                            new Long(duration), // {2} - 持续总时间
                                            new Long(durationOfSelf), // {3} -
                                                                      // Entry自身消耗的时间
                                            new Double(percent), // {4} -
                                                                 // 在父entry中所占的时间比例
                                            new Double(percentOfAll), // {5} -
                                                                     // 在总时间中所旧的时间比例
                                            new Double(percentBySelf),
                                            new Double(percentOfAllBySelf)
            };
            //准备2：替换的信息的格式
//            StringBuffer pattern = new StringBuffer("{1,number} ");
            StringBuffer pattern = new StringBuffer("开始时间：{1,number}ms ");

            if (isReleased()) {
//                pattern.append("[{2,number}ms");
                pattern.append("[总耗时：{2,number}ms");

                if ((durationOfSelf > 0) && (durationOfSelf != duration)) {
//                    pattern.append(" ({3,number}ms)");
                    pattern.append(" (自身耗时：{3,number}ms");
                }
                //TODO 增加自身耗时所占父耗时百分比
                if (percentBySelf > 0) {//不是第一个
                    pattern.append(", 占用父耗时百分比{6,number,##.##%}");
                }
                //TODO 增加自身耗时所占总耗时百分比
                if (percentOfAllBySelf > 0) {//不是第一个
                	pattern.append(", 占用总耗时百分比：{7,number,##.##%}");
                }
                pattern.append(")");//TODO 添加自身耗时的右括号
                if (percent > 0) {//不是第一个
//                	pattern.append(", {4,number,##%}");//原来
                    pattern.append(", 占用父耗时百分比{4,number,##.##%}");
                }

                if (percentOfAll > 0) {
//                	pattern.append(", {5,number,##%}");//原来
                    pattern.append(", 占用总耗时百分比：{5,number,##.##%}");
                }

                pattern.append("]");
            } else {
                pattern.append("[UNRELEASED]");
            }

            if (message != null) {
                pattern.append(" - {0}");
            }
            //准备3：替换/MessageFormat.format
            buffer.append(MessageFormat.format(pattern.toString(), params));

            //准备4：处理Entry的子Entry
            for (int i = 0; i < subEntries.size(); i++) {
                Entry subEntry = (Entry) subEntries.get(i);

                buffer.append('\n');

                if (i == (subEntries.size() - 1)) {//找出最后一项（Entries列表中的最后一个）
                    subEntry.toString(buffer, prefix2 + "`---", prefix2 + "    "); // 最后一项
                } else if (i == 0) {//找出第一项
                    subEntry.toString(buffer, prefix2 + "+---", prefix2 + "|   "); // 第一项
                } else {//其他的都是中间项
                    subEntry.toString(buffer, prefix2 + "+---", prefix2 + "|   "); // 中间项
                }
            }
        }

        public boolean isStart() {
            return isStart;
        }
    }

    /**
     * 代表一个profiler entry的详细信息。
     * @author lenovo
     *
     */
    public interface Message {
        MessageLevel getMessageLevel(Entry entry);

        String getBriefMessage();

        String getDetailedMessage();
    }

    /**
     * 测试入口
     * @param args
     */
    public static void main(String[] args) {
    	/**
    	 * 1.创建线程池
    	 * 
    	 * 	corePoolSize： 线程池维护线程的最少数量 
			maximumPoolSize：线程池维护线程的最大数量 
			keepAliveTime： 线程池维护线程所允许的空闲时间 
			unit： 线程池维护线程所允许的空闲时间的单位 ——TimeUnit.SECONDS是秒
			workQueue： 线程池所使用的缓冲队列 ——new ArrayBlockingQueue<Runnable>(30 + 10)
			handler： 线程池对拒绝任务的处理策略 ——ThreadPoolExecutor.CallerRunsPolicy()
    	 */
        ThreadPoolExecutor consumeExecutor = new ThreadPoolExecutor(30, 30 + 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30 + 10), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread myThread = new Thread(r);
                myThread.setName("TT");
                System.out.println(myThread);
                return myThread;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        /**
         * 2.测试
         */
        while (true) {
        	/**
        	 * 3.线程池执行execute——匿名线程的run
        	 */
            consumeExecutor.execute(new Runnable() {
            	public void run() {
                    Profiler.start("Start method: a");
                    try {
                    	//暂停一个线程0.1秒
                        Thread.sleep(100);
//                        Thread.sleep(60*1000);//暂停60秒——导致debug停止
                        //添加一个子Entry
                        Profiler.enter("Start method: a-1");
                        
                        //暂停一个线程0.1秒
                        Thread.sleep(100);
                        //结束最近一个Entry并计时
                        Profiler.release();
                        //添加一个子Entry
                        Profiler.enter("Start method: a-2");
                        
                        //暂停一个线程0.1秒
                        Thread.sleep(100);
                        //结束最近一个Entry并计时
                        Profiler.release();

                        Profiler.start("Invoking method: b");
                        try {
                            Thread.sleep(100);

                            Profiler.enter("Start method: b-1");
                            Thread.sleep(100);
                            Profiler.release();

                            Profiler.enter("Start method: b-2");
                            Thread.sleep(100);
                            Profiler.release();

                        } finally {
                            Profiler.release();
                            if (!Profiler.isSuperStart())
                                System.out.println(Profiler.dump());
                            Profiler.reset();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Profiler.release();
                        System.out.println(Profiler.dump());
                        Profiler.reset();
                    }

                }
            });
        }
    }
}
