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
package fr.xebia.management.jms.leak;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreationContext {

    private final List<StackTraceElement> creationStackTrace = Collections.unmodifiableList(Arrays.asList(Thread.currentThread()
            .getStackTrace()));

    private final String creationThreadName = Thread.currentThread().getName();

    private final long creationTimestamp = System.currentTimeMillis();

    public List<StackTraceElement> getCreationStackTrace() {
        return creationStackTrace;
    }

    public String getCreationThreadName() {
        return creationThreadName;
    }

    /**
     * 
     * @See {@link System#currentTimeMillis()}
     */
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public String dumpContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creation context - Thread: '" + this.creationThreadName + "', date: '" + new Timestamp(creationTimestamp)
                + ", stacktrace: \r\n");
        for (StackTraceElement stackTraceElement : this.creationStackTrace) {
            sb.append("\t" + stackTraceElement);
        }
        return sb.toString();
    }
}
