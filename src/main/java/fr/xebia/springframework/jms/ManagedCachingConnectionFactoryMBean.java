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
package fr.xebia.springframework.jms;

import org.springframework.jms.connection.CachingConnectionFactory;

/**
 * Exposed attributes and operations exposed via JMX by the
 * {@link ManagedCachingConnectionFactory} and its
 * {@link CachingConnectionFactory} parent.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public interface ManagedCachingConnectionFactoryMBean {
    int getSessionCacheSize();

    boolean isCacheConsumers();

    boolean isCacheProducers();

    boolean isReconnectOnException();

    void resetConnection();

    void setCacheConsumers(boolean cacheConsumers);

    void setCacheProducers(boolean cacheProducers);

    void setReconnectOnException(boolean reconnectOnException);

    void setSessionCacheSize(int sessionCacheSize);
}
