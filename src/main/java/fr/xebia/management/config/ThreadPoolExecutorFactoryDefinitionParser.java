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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import fr.xebia.springframework.concurrent.ThreadPoolExecutorFactory;

public class ThreadPoolExecutorFactoryDefinitionParser extends AbstractBeanDefinitionParser {

    private static final String MBEAN_SERVER_BEAN_NAME = "executor-service";

    private static final String POOL_SIZE_ATTRIBUTE = "pool-size";

    private static final String QUEUE_CAPACITY_ATTRIBUTE = "queue-capacity";

    private static final String KEEP_ALIVE_ATTRIBUTE = "keep-alive";

    private static final String REJECTION_POLICY_ATTRIBUTE = "rejection-policy";

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        return (StringUtils.hasText(id) ? id : MBEAN_SERVER_BEAN_NAME);
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ThreadPoolExecutorFactory.class);

        // Mark as infrastructure bean and attach source location.
        builder.setRole(BeanDefinition.ROLE_APPLICATION);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));

        String poolSize = element.getAttribute(POOL_SIZE_ATTRIBUTE);
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }

        String queueCapacity = element.getAttribute(QUEUE_CAPACITY_ATTRIBUTE);
        if (StringUtils.hasText(queueCapacity)) {
            builder.addPropertyValue("queueCapacity", queueCapacity);
        }

        String keepAlive = element.getAttribute(KEEP_ALIVE_ATTRIBUTE);
        if (StringUtils.hasText(keepAlive)) {
            builder.addPropertyValue("keepAliveTimeInSeconds", keepAlive);
        }

        String rejectionPolicy = element.getAttribute(REJECTION_POLICY_ATTRIBUTE);

        Class<? extends RejectedExecutionHandler> rejectedExecutionHandlerClass;
        if ("ABORT".equals(rejectionPolicy)) {
            rejectedExecutionHandlerClass = AbortPolicy.class;
        } else if ("CALLER_RUNS".equals(rejectionPolicy)) {
            rejectedExecutionHandlerClass = CallerRunsPolicy.class;
        } else if ("DISCARD".equals(rejectionPolicy)) {
            rejectedExecutionHandlerClass = DiscardPolicy.class;
        } else if ("DISCARD_OLDEST".equals(rejectionPolicy)) {
            rejectedExecutionHandlerClass = DiscardOldestPolicy.class;
        } else {
            throw new IllegalArgumentException("Unsupported '" + REJECTION_POLICY_ATTRIBUTE + "': '" + rejectionPolicy + "'");
        }
        builder.addPropertyValue("rejectedExecutionHandlerClass", rejectedExecutionHandlerClass);

        return builder.getBeanDefinition();
    }
}
