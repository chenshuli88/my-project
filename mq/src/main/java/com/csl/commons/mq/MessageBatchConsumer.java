package com.csl.commons.mq;

import com.csl.commons.mq.model.MQMessage;
import com.csl.commons.mq.model.MQMessageStatus;

import java.util.List;

public interface MessageBatchConsumer extends MessageConsumer {
    MQMessageStatus consume(List<MQMessage> var1);
}
