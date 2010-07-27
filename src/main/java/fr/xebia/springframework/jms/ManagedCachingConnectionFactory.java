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
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * <p>
 * Subclass of {@link CachingConnectionFactory} to expose key attributes and
 * operations via JMX.
 * </p>
 * <p>
 * Most of the JMX exposition is done via the
 * {@link ManagedCachingConnectionFactoryMBean}.
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedCachingConnectionFactory extends CachingConnectionFactory implements ManagedCachingConnectionFactoryMBean,
        BeanNameAware, SelfNaming {

    private String beanName;

    private ObjectName objectName;

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            objectName = ObjectName.getInstance("javax.jms:type=CachingConnectionFactory,name=" + ObjectName.quote(this.beanName));
        }
        return objectName;
    }

    /**
     * make visible for JMX
     */
    @Override
    public boolean isReconnectOnException() {
        return super.isReconnectOnException();
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }
}
