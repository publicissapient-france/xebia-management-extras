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

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import fr.xebia.jms.wrapper.MessageConsumerWrapper;

public class LeakDetectorMessageConsumer extends MessageConsumerWrapper implements MessageConsumer {

    private final CreationContext creationContext = new CreationContext();

    public CreationContext getCreationContext() {
        return creationContext;
    }

    private final LeakDetectorSession leakDetectorSession;

    public LeakDetectorMessageConsumer(MessageConsumer delegate, LeakDetectorSession leakDetectorSession) {
        super(delegate);
        this.leakDetectorSession = leakDetectorSession;
        leakDetectorSession.registerOpenMessageConsumer(this);
    }

    @Override
    public void close() throws JMSException {
        super.close();
        leakDetectorSession.unregisterOpenMessageConsumer(this);
    }

    public String dumpCreationContext() {
        return delegate().toString() + " - " + creationContext.dumpContext();
    }
}
