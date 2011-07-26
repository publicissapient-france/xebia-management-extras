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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import fr.xebia.management.jms.SpringManagedConnectionFactory;

public class SpringManagedConnectionFactoryDefinitionParser extends AbstractBeanDefinitionParser {

    private static final String CONNECTION_FACTORY_ATTRIBUTE = "connection-factory";
    private static final String TRACK_LEAKS_ATTRIBUTE = "track-leaks";

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SpringManagedConnectionFactory.class);

        // Mark as infrastructure bean and attach source location.
        builder.setRole(BeanDefinition.ROLE_APPLICATION);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));

        builder.addPropertyReference("connectionFactory", element.getAttribute(CONNECTION_FACTORY_ATTRIBUTE));
        builder.addPropertyValue("trackLeaks", element.getAttribute(TRACK_LEAKS_ATTRIBUTE));

        return builder.getBeanDefinition();
    }

}
