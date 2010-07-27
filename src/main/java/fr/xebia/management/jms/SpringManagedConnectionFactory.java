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
package fr.xebia.management.jms;

import javax.jms.ConnectionFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class SpringManagedConnectionFactory extends ManagedConnectionFactory implements ConnectionFactory, ManagedConnectionFactoryMBean,
        SelfNaming, BeanNameAware {

    private String beanName;

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return ObjectName.getInstance("javax.jms:type=ConnectionFactory,name=" + beanName);
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

}
