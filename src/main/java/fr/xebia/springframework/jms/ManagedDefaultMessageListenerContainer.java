/*
 * Copyright 2008-2009 Xebia and the original author or authors.
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
package fr.xebia.springframework.jms;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
@ManagedResource
public class ManagedDefaultMessageListenerContainer extends DefaultMessageListenerContainer implements BeanNameAware, SelfNaming {

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return ObjectName.getInstance("javax.jms:type=MessageListenerContainer,name=" + ObjectName.quote(beanName));
    }

    @ManagedOperation
    @Override
    public void stop() throws JmsException {
        super.stop();
    }

    @ManagedAttribute(description = "Return the number of currently active consumers. "
        + "This number will always be inbetween 'concurrentConsumers' and 'maxConcurrentConsumers', but might be lower than 'scheduledConsumerCount'. "
        + "(in case of some consumers being scheduled but not executed at the moment).")
        public int getContainerActiveConsumerCount() {
        return getActiveConsumerCount();
    }

    @ManagedAttribute(description = "Return the 'concurrentConsumer' setting."
        + "This returns the currently configured 'concurrentConsumers' value;"
        + "the number of currently scheduled/active consumers might differ.")
        public int getContainerConcurrentConsumers() {
        return getConcurrentConsumers();
    }

    @ManagedAttribute(description = "Name of a durable subscription to create, if any.")
    @Override
    public String getDurableSubscriptionName() {
        return super.getDurableSubscriptionName();
    }

    @ManagedAttribute(description = "Determine the number of currently paused tasks, if any.")
    @Override
    public int getPausedTaskCount() {
        return super.getPausedTaskCount();
    }

    @ManagedAttribute(description = "Determine whether this container is currently running, that is, whether it has been started and not stopped yet.")
    public boolean isContainerRunning() {
        return isRunning();
    }

    @ManagedOperation
    @Override
    public void start() throws JmsException {
        super.start();
    }

}
