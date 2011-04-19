/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.springframework.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ThreadPoolExecutorFactory extends AbstractFactoryBean<ThreadPoolExecutor> implements FactoryBean<ThreadPoolExecutor>,
        BeanNameAware {

    private static class CountingRejectedExecutionHandler implements RejectedExecutionHandler {

        private final AtomicInteger rejectedExecutionCount = new AtomicInteger();

        private final RejectedExecutionHandler rejectedExecutionHandler;

        public CountingRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            super();
            this.rejectedExecutionHandler = rejectedExecutionHandler;
        }

        public int getRejectedExecutionCount() {
            return rejectedExecutionCount.get();
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejectedExecutionCount.incrementAndGet();
            rejectedExecutionHandler.rejectedExecution(r, executor);
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("rejectedExecutionCount", this.rejectedExecutionCount)
                    .append("rejectedExecutionHandler", this.rejectedExecutionHandler).toString();
        }

    }

    @ManagedResource
    public static class SpringJmxEnabledThreadPoolExecutor extends ThreadPoolExecutor implements SelfNaming {

        private ObjectName objectName;

        public SpringJmxEnabledThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler,
                ObjectName objectName) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new CountingRejectedExecutionHandler(
                    rejectedExecutionHandler));
            this.objectName = objectName;
        }

        @Override
        @ManagedAttribute(description = "Returns the approximate number of threads that are actively executing tasks")
        public int getActiveCount() {
            return super.getActiveCount();
        }

        @ManagedAttribute(description = "Returns the approximate total number of tasks that have completed execution.")
        @Override
        public long getCompletedTaskCount() {
            return super.getCompletedTaskCount();
        }

        @ManagedAttribute(description = "Returns the core number of threads")
        @Override
        public int getCorePoolSize() {
            return super.getCorePoolSize();
        }

        @ManagedAttribute(description = "Returns the largest number of threads that have ever simultaneously been in the pool.")
        @Override
        public int getLargestPoolSize() {
            return super.getLargestPoolSize();
        }

        @ManagedAttribute(description = "Returns the maximum allowed number of threads")
        @Override
        public int getMaximumPoolSize() {
            return super.getMaximumPoolSize();
        }

        public ObjectName getObjectName() throws MalformedObjectNameException {
            return objectName;
        }

        @ManagedAttribute(description = "Returns the number of additional elements that this queue can "
                + "ideally (in the absence of memory or resource constraints) accept without  "
                + "blocking, or Integer.MAX_VALUE if there is no intrinsic limit.")
        public int getQueueRemainingCapacity() {
            return getQueue().remainingCapacity();
        }

        @ManagedAttribute(description = "Returns the number of tasks that has ever been rejected")
        public int getRejectedExecutionCount() {
            return ((CountingRejectedExecutionHandler) getRejectedExecutionHandler()).getRejectedExecutionCount();
        }

        @Override
        @ManagedAttribute(description = "Returns the approximate total number of tasks that have ever been scheduled for execution "
                + "(does not include the rejected tasks)")
        public long getTaskCount() {
            return super.getTaskCount();
        }

        @ManagedAttribute(description = "Sets the core number of threads. "
                + "If the new value is smaller than the current value, excess existing threads will be terminated when they next "
                + "become idle. If larger, new threads will, if needed, be started to execute any queued tasks.")
        @Override
        public void setCorePoolSize(int corePoolSize) {
            super.setCorePoolSize(corePoolSize);
        }

        @ManagedAttribute(description = "Sets the maximum allowed number of threads. "
                + "If the new value is smaller than the current value, excess existing threads will be "
                + "terminated when they next become idle.")
        @Override
        public void setMaximumPoolSize(int maximumPoolSize) {
            super.setMaximumPoolSize(maximumPoolSize);
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("objectName", this.objectName)
                    .append("corePoolSize", this.getCorePoolSize())
                    .append("maximumPoolSize", this.getMaximumPoolSize())
                    .append("keepAliveTimeInMillis", this.getKeepAliveTime(TimeUnit.MILLISECONDS))
                    .append("queue", this.getQueue().getClass()).append("rejectedExecutionHandler", this.getRejectedExecutionHandler())
                    .toString();
        }
    }

    private String beanName;

    private int corePoolSize = 1;

    private long keepAliveTimeInSeconds;

    private int maximumPoolSize = Integer.MAX_VALUE;

    private int queueCapacity = Integer.MAX_VALUE;

    private Class<? extends RejectedExecutionHandler> rejectedExecutionHandlerClass = AbortPolicy.class;

    @Override
    protected ThreadPoolExecutor createInstance() throws Exception {
        Assert.isTrue(this.corePoolSize > 0, "corePoolSize must be greater than zero");
        Assert.isTrue(this.maximumPoolSize > 0, "maximumPoolSize must be greater than zero");
        Assert.isTrue(this.queueCapacity > 0, "queueCapacity must be greater than zero");

        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory(this.beanName + "-");
        threadFactory.setDaemon(true);

        BlockingQueue<Runnable> blockingQueue;
        if (queueCapacity == 0) {
            blockingQueue = new SynchronousQueue<Runnable>();
        } else {
            blockingQueue = new LinkedBlockingQueue<Runnable>(queueCapacity);
        }
        ThreadPoolExecutor instance = new SpringJmxEnabledThreadPoolExecutor(corePoolSize, //
                maximumPoolSize, //
                keepAliveTimeInSeconds, //
                TimeUnit.SECONDS, //
                blockingQueue, //
                threadFactory, //
                rejectedExecutionHandlerClass.newInstance(), //
                new ObjectName("java.util.concurrent:type=ThreadPoolExecutor,name=" + beanName));

        return instance;
    }

    @Override
    protected void destroyInstance(ThreadPoolExecutor instance) throws Exception {
        instance.shutdown();
    }

    @Override
    public Class<?> getObjectType() {
        return SpringJmxEnabledThreadPoolExecutor.class;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setKeepAliveTimeInSeconds(long keepAliveTimeInSeconds) {
        this.keepAliveTimeInSeconds = keepAliveTimeInSeconds;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * @deprecated Use {@link #setCorePoolSize(int)} and
     *             {@link #setMaximumPoolSize(int)} or
     *             {@link #setPoolSize(String)}.
     */
    @Deprecated
    public void setNbThreads(int nbThreads) {
        this.corePoolSize = nbThreads;
        this.maximumPoolSize = nbThreads;
    }

    public void setPoolSize(String poolSize) {
        if (!StringUtils.hasText(poolSize)) {
            return;
        }

        switch (StringUtils.countOccurrencesOf(poolSize, "-")) {
        case 0:
            this.corePoolSize = Integer.parseInt(poolSize);
            this.maximumPoolSize = this.corePoolSize;
            break;
        case 1:
            String[] splittedPoolSize = StringUtils.split(poolSize, "-");
            this.corePoolSize = Integer.parseInt(splittedPoolSize[0]);
            this.maximumPoolSize = Integer.parseInt(splittedPoolSize[1]);
            break;
        default:
            throw new BeanCreationException(this.beanName, "Invalid pool-size value [" + poolSize + "]: only single maximum integer "
                        + "(e.g. \"5\") and minimum-maximum range (e.g. \"3-5\") are supported.");
        }
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setRejectedExecutionHandlerClass(Class<? extends RejectedExecutionHandler> rejectedExecutionHandlerClass) {
        this.rejectedExecutionHandlerClass = rejectedExecutionHandlerClass;
    }
}
