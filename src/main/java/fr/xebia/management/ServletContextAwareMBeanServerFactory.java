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
package fr.xebia.management;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>
 * Instantiate {@link MBeanServer} which add a "path" property with value
 * {@link ServletContext#getContextPath()} to each {@link ObjectName} passed as
 * method parameter. The goal is to prevent collisions between MBeans declared
 * in different web applications.
 * </p>
 * <p>
 * On Tomcat, an extra property named <code>"host"</code> with the name of the
 * Tomcat host in which the webapp is declared is added (value
 * <code>"org.apache.catalina.core.ApplicationContextFacade#context#context#hostName"</code>
 * ).
 * </p>
 * <p>
 * Sample : EHCache's {@link net.sf.ehcache.management.ManagementService} will
 * register Hibernate's {@linkplain org.hibernate.cache.StandardQueryCache} as 
 * <code>"net.sf.ehcache:CacheManager=my-cachemanager,name=org.hibernate.cache.StandardQueryCache,type=CacheStatistics"</code>
 * that could collide with other applications and this MBeanServer will add
 * the <code>"path"</code> attribute to prevent problems : 
 * <code>"net.sf.ehcache:CacheManager=my-cachemanager,name=org.hibernate.cache.StandardQueryCache,type=CacheStatistics,path=/my-application</code>
 * " .
 * </p>
 * <p>
 * Implementation decisions:
 * </p>
 * <ul>
 * <li>The added property was named <code>"path"</code> to follow Tomcat JMX
 * beans naming convention,</li>
 * <li>This {@link FactoryBean} doesn't extend {@link AbstractFactoryBean} due
 * to <a href="http://jira.springframework.org/browse/SPR-4968">SPR-4968 : Error
 * "Singleton instance not initialized yet" triggered by toString call in case
 * of circular references</a></li>
 * </ul>
 * <hr/>
 * <p>
 * <strong>Configuration sample:</strong>
 * </p>
 * 
 * <pre>
 * <code>
 * &lt;beans ... &gt;
 *    &lt;context:mbean-server id="rawMbeanServer" /&gt;
 *    &lt;bean id="mbeanServer" class="fr.xebia.management.ServletContextAwareMBeanServerFactory"&gt;
 *       &lt;property name="mbeanServer" ref="rawMbeanServer" /&gt;
 *    &lt;/bean&gt;
 *    &lt;context:mbean-export server="mbeanServer" /&gt;
 *    ...
 * &lt;/beans&gt;
 * </code>
 * </pre>
 * 
 * <p>
 * An object name 
 * <code>"net.sf.ehcache:CacheManager=my-cache-manager,name=my-cache,type=Cache</code>
 * " will be registered as 
 * <code>"net.sf.ehcache:CacheManager=my-cache-manager,name=my-cache,type=Cache,host=localhost,path=/my-application"</code>
 * for an application "my-application" declared in the "localhost" host of a
 * Tomcat server: attributes <code>"path=/my-application"</code> and 
 * <code>"host=localhost"</code> are added to the object name.
 * </p>
 * <p>
 * <strong>Advanced configuration sample:</strong>
 * </p>
 * <p>
 * The <code>"objectNameExtraAttributes"</code> property allows to manually add extra attributes in addition to the 
 * <code>"path"</code> (and <code>"host"</code>) attribute that is automatically
 * added.
 * </p>
 * 
 * <pre>
 * <code>
 * &lt;beans ... &gt;
 *    &lt;context:mbean-server id="rawMbeanServer" /&gt;
 *    &lt;bean id="mbeanServer" class="fr.xebia.management.ServletContextAwareMBeanServerFactory"&gt;
 *       &lt;property name="mbeanServer" ref="rawMbeanServer" /&gt;
 *       &lt;property name="objectNameExtraAttributes" &gt;
 *          &lt;map&gt;
 *             &lt;entry key="ze-app-id-asked-by-ze-monitoring-team" value="my-application-id" /&gt;
 *          &lt;/map&gt;
 *       &lt;/property&gt;
 *    &lt;/bean&gt;
 *    &lt;context:mbean-export server="mbeanServer" /&gt;
 *    ...
 * &lt;/beans&gt;
 * </code>
 * </pre>
 * 
 * <p>
 * An object name
 * <code>"net.sf.ehcache:CacheManager=my-cache-manager,name=my-cache,type=Cache"</code>
 * will be registered as
 * <code>"net.sf.ehcache:CacheManager=my-cache-manager,ze-app-id-asked-by-ze-monitoring-team=my-application-id,name=my-cache,type=Cache,host=localhost,path=/my-application"</code>
 * for an application "my-application" declared in the "localhost" host of a
 * Tomcat server: attributes <code>"path=/my-application"</code>, 
 * <code>"host=localhost"</code>and 
 * <code>"ze-app-id-asked-by-ze-monitoring-team=my-application-id"</code> are
 * added to the object name.
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class ServletContextAwareMBeanServerFactory implements FactoryBean<MBeanServer>, ServletContextAware, InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(ServletContextAwareMBeanServerFactory.class);

    protected MBeanServer instance;

    protected MBeanServer mbeanServer;

    protected Map<String, String> objectNameExtraAttributes = new HashMap<String, String>();

    protected ServletContext servletContext;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.mbeanServer, "mbeanServer can NOT be null");
        Assert.notNull(this.servletContext, "servletContext can NOT be null");

        objectNameExtraAttributes.put("path", servletContext.getContextPath());

        if ("org.apache.catalina.core.ApplicationContextFacade".equals(servletContext.getClass().getName())) {

            Field applicationContextFacadeContextField = Class.forName("org.apache.catalina.core.ApplicationContextFacade")
                    .getDeclaredField("context");
            applicationContextFacadeContextField.setAccessible(true);

            Field applicationContextContextField = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("context");
            applicationContextContextField.setAccessible(true);

            Field standardContextHostNameField = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("hostName");
            standardContextHostNameField.setAccessible(true);

            Object applicationContext = applicationContextFacadeContextField.get(servletContext);
            Object standardContext = applicationContextContextField.get(applicationContext);
            String hostName = (String) standardContextHostNameField.get(standardContext);

            objectNameExtraAttributes.put("host", hostName);
        }
        logger.trace("Extra objectname attributes : {}" + this.objectNameExtraAttributes);
    }

    public MBeanServer getObject() throws Exception {
        if (instance == null) {
            InvocationHandler invocationHandler = new InvocationHandler() {
                /**
                 * <p>
                 * Copy the given <code>objectName</code> adding the extra
                 * attributes.
                 * </p>
                 */
                protected ObjectName addExtraAttributesToObjectName(ObjectName objectName) throws MalformedObjectNameException {
                    Hashtable<String, String> table = new Hashtable<String, String>(objectName.getKeyPropertyList());
                    table.putAll(objectNameExtraAttributes);

                    ObjectName result = ObjectName.getInstance(objectName.getDomain(), table);
                    logger.trace("addExtraAttributesToObjectName({}): {}", objectName, result);

                    return result;
                }

                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object[] modifiedArgs = args.clone();
                    for (int i = 0; i < modifiedArgs.length; i++) {
                        Object arg = modifiedArgs[i];
                        if (arg instanceof ObjectName) {
                            ObjectName objectName = (ObjectName) arg;
                            modifiedArgs[i] = addExtraAttributesToObjectName(objectName);
                        }
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(method + " : " + Arrays.asList(modifiedArgs));
                    }
                    try {
                        return method.invoke(mbeanServer, modifiedArgs);
                    } catch (InvocationTargetException ite) {
                        throw ite.getCause();
                    }
                }
            };
            instance = (MBeanServer) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class[] { MBeanServer.class },
                    invocationHandler);

        }
        return instance;
    }

    public Class<? extends MBeanServer> getObjectType() {
        return MBeanServer.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

    public void setObjectNameExtraAttributes(Map<String, String> objectNameExtraAttributes) {
        this.objectNameExtraAttributes = objectNameExtraAttributes;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
