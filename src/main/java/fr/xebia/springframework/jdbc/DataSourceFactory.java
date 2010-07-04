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
package fr.xebia.springframework.jdbc;

import javax.management.ObjectName;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>
 * TODO complete properties settings.
 * </p>
 * <p>
 * To prevent need of using {@link MBeanExporter#setAllowEagerInit(boolean)} to <code>true</code>, we manually call
 * {@link MBeanExporter#registerManagedResource(Object, ObjectName)}. See <a href="http://jira.springframework.org/browse/SPR-4954">SPR-4954
 * : Add property to MBeanExporter to control eager initiailization of FactoryBeans</a>
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class DataSourceFactory extends AbstractFactoryBean<DataSource> implements FactoryBean<DataSource>, BeanNameAware {

    @ManagedResource
    public static class SpringJmxEnableBasicDataSource extends BasicDataSource {

        @ManagedAttribute
        @Override
        public synchronized int getNumActive() {
            return super.getNumActive();
        }

        @ManagedAttribute
        @Override
        public synchronized int getNumIdle() {
            return super.getNumIdle();
        }

        @ManagedAttribute
        @Override
        public synchronized String getUrl() {
            return super.getUrl();
        }
    }

    private String beanName;

    private boolean defaultAutoCommit;

    private String driverClassName;

    private int maxActive;

    private long maxWait;

    private MBeanExporter mbeanExporter;

    private String objectName;

    private String password;

    private String url;

    private String username;
    
    @Override
    protected DataSource createInstance() throws Exception {

        BasicDataSource basicDataSource = new SpringJmxEnableBasicDataSource();
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setMaxActive(maxActive);
        basicDataSource.setMaxWait(maxWait);
        basicDataSource.setDefaultAutoCommit(defaultAutoCommit);

        if (!StringUtils.hasLength(this.objectName)) {
            objectName = "javax.sql:type=DataSource,name=" + ObjectName.quote(beanName);
        }
        Assert.notNull(mbeanExporter, "mbeanExporter can not be null");

        mbeanExporter.registerManagedResource(basicDataSource, new ObjectName(objectName));

        return basicDataSource;
    }

    @Override
    protected void destroyInstance(DataSource instance) throws Exception {
        ((BasicDataSource)instance).close();
    }

    @Override
    public Class<?> getObjectType() {
        return BasicDataSource.class;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public void setMbeanExporter(MBeanExporter mbeanExporter) {
        this.mbeanExporter = mbeanExporter;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
