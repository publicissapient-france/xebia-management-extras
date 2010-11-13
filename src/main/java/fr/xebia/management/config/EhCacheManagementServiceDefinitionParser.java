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
package fr.xebia.management.config;

import javax.management.MBeanServer;

import net.sf.ehcache.management.ManagementService;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class EhCacheManagementServiceDefinitionParser extends AbstractBeanDefinitionParser {

    private static final String EH_CACHE_MANAGEMENT_SERVICE_BEAN_NAME = "ehCacheManagementService";

    private static final String MBEAN_SERVER_ATTRIBUTE = "mbean-server";

    private static final String CACHE_MANAGER_ATTRIBUTE = "cache-manager";

    private static final String REGISTER_CACHE_MANAGER_ATTRIBUTE = "register-cache-manager";

    private static final String REGISTER_CACHES_ATTRIBUTE = "register-caches";

    private static final String REGISTER_CACHE_CONFIGURATIONS_ATTRIBUTE = "register-cache-configurations";

    private static final String REGISTER_CACHE_STATISTICS_ATTRIBUTE = "register-cache-statistics";

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        return (StringUtils.hasText(id) ? id : EH_CACHE_MANAGEMENT_SERVICE_BEAN_NAME);
    }

    /**
     * Instiates a {@link ManagementService} vie
     * {@link ManagementService#ManagementService(net.sf.ehcache.CacheManager, MBeanServer, boolean, boolean, boolean, boolean)}
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(net.sf.ehcache.management.ManagementService.class);

        // Mark as infrastructure bean and attach source location.
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));

        builder.addConstructorArgReference(element.getAttribute(CACHE_MANAGER_ATTRIBUTE));
        builder.addConstructorArgReference(element.getAttribute(MBEAN_SERVER_ATTRIBUTE));
        builder.addConstructorArgValue(element.getAttribute(REGISTER_CACHE_MANAGER_ATTRIBUTE));
        builder.addConstructorArgValue(element.getAttribute(REGISTER_CACHES_ATTRIBUTE));
        builder.addConstructorArgValue(element.getAttribute(REGISTER_CACHE_CONFIGURATIONS_ATTRIBUTE));
        builder.addConstructorArgValue(element.getAttribute(REGISTER_CACHE_STATISTICS_ATTRIBUTE));

        builder.setInitMethodName("init");
        builder.setDestroyMethodName("dispose");

        return builder.getBeanDefinition();
    }
}
