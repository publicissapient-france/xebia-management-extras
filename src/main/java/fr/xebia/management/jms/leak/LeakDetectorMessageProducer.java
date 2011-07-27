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
import javax.jms.MessageProducer;

import fr.xebia.jms.wrapper.MessageProducerWrapper;

public class LeakDetectorMessageProducer extends MessageProducerWrapper implements MessageProducer {

    private final CreationContext creationContext = new CreationContext();

    private final LeakDetectorSession leakDetectorSession;

    public LeakDetectorMessageProducer(MessageProducer delegate, LeakDetectorSession leakDetectorSession) {
        super(delegate);
        this.leakDetectorSession = leakDetectorSession;

        leakDetectorSession.registerOpenMessageProducer(this);
    }

    @Override
    public void close() throws JMSException {
        super.close();
        leakDetectorSession.unregisterOpenMessageProducer(this);
    }

    public CreationContext getCreationContext() {
        return creationContext;
    }

    public LeakDetectorSession getLeakDetectorSession() {
        return leakDetectorSession;
    }

    public String dumpCreationContext(String offest) {
        return offest + delegate().toString() + " - " + creationContext.dumpContext(offest);
    }

}
