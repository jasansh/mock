package com.tieto.pcm.mfa.services.integration.jms;

import com.tieto.pcm.mfa.services.integration.jms.model.MessageHeaderData;

public interface JmsMessageProcessor<T> {

    void processMessage(MessageHeaderData header, T message);
}
