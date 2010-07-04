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
package fr.xebia.springframework.jdbc;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Managed attributes and operations of a {@link BasicDataSource}.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public interface ManagedBasicDataSourceMBean {

    boolean getDefaultAutoCommit();

    int getMaxActive();

    int getMaxIdle();

    long getMaxWait();

    int getMinIdle();

    int getNumActive();

    int getNumIdle();

    String getUrl();

    String getUsername();

    void setMaxActive(int maxActive);

    void setMaxIdle(int maxIdle);

    void setMaxWait(long maxWait);
}
