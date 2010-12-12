/*
 * Copyright 2008-2010 Xebia and the original author or authors.
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
package fr.xebia.springframework.jdbc;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * JMX enabled {@link BasicDataSource}.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ManagedBasicDataSource extends BasicDataSource implements ManagedBasicDataSourceMBean, BeanNameAware, SelfNaming,
        DisposableBean {

    private String beanName;

    private ObjectName objectName;

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            objectName = new ObjectName("javax.sql:type=DataSource,name=" + beanName);
        }
        return objectName;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setObjectName(ObjectName objectName) throws MalformedObjectNameException {
        this.objectName = objectName;
    }

    public void setObjectName(String objectName) throws MalformedObjectNameException {
        this.objectName = ObjectName.getInstance(objectName);
    }

    public void destroy() throws Exception {
        this.close();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this) //
                .append("url", getUrl()) //
                .append("username", getUsername()) //
                .append("maxActive", getMaxActive()) //
                .toString();
    }
}
