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
package fr.xebia.management.statistics;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Profiled {

    /**
     * By default {@link IOException}
     */
    Class<? extends Throwable>[] communicationExceptionsTypes() default { IOException.class };

    /**
     * By default, no exception type is defined
     */
    Class<? extends Throwable>[] businessExceptionsTypes() default {};

    /**
     * By default, the compact fully qualified method name will be used (e.g. "
     * <code>c.m.MyClass.myMethod</code>")
     */
    String name() default "";

    /**
     * By default 500 ms
     */
    long slowInvocationThresholdInMillis() default 500;

    /**
     * By default 1000 ms
     */
    long verySlowInvocationThresholdInMillis() default 1000;
    
    /**
     * Max active invocation. By default, no limit.
     */
    int maxActive() default -1;
    
    /**
     * Max active invocation as Spring Expression Language Expression. By default, no limit.
     * Sample : "#{ T(java.lang.Integer).parseInt(systemProperties['tomcat.thread-pool.size']) / 2 }"
     */
    String maxActiveExpression() default "";
    
    /**
     * Max wait time to acquire the max active semaphore. By default 100ms.
     */
    int maxActiveSemaphoreAcquisitionMaxTimeInMillis() default 100;
}
