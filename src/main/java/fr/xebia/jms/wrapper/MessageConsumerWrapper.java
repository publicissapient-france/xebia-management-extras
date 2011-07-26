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
package fr.xebia.jms.wrapper;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class MessageConsumerWrapper extends ForwardingObject implements MessageConsumer {

    private final MessageConsumer delegate;

    protected MessageConsumer delegate() {
        return delegate;
    }

    public void close() throws JMSException {
        delegate.close();
    }

    public MessageListener getMessageListener() throws JMSException {
        return delegate.getMessageListener();
    }

    public String getMessageSelector() throws JMSException {
        return delegate.getMessageSelector();
    }

    public Message receive() throws JMSException {
        return delegate.receive();
    }

    public Message receive(long timeout) throws JMSException {
        return delegate.receive(timeout);
    }

    public Message receiveNoWait() throws JMSException {
        return delegate.receiveNoWait();
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        delegate.setMessageListener(listener);
    }

    public MessageConsumerWrapper(MessageConsumer delegate) {
        super();
        this.delegate = delegate;
    }

}
