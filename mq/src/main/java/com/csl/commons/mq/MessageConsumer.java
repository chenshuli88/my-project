package com.csl.commons.mq;

import com.csl.commons.mq.model.MQMessage;
import com.csl.commons.mq.model.MQMessageStatus;

public interface MessageConsumer {
    MQMessageStatus consume(MQMessage var1);
}
