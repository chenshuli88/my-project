package com.csl.commons.mq;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csl.commons.mq.model.DelayLevel;
import com.csl.commons.mq.model.MQMessage;
import com.csl.commons.mq.model.MQMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProducer implements Closeable {
    private static Logger log = LoggerFactory.getLogger("t.c.c.MessageProducer");
    private static DefaultMQProducer PRODUCER;
    private String namesrvAddr;

    public MessageProducer() {
    }

    public String getNamesrvAddr() {
        return this.namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    protected void init() {
        if (PRODUCER == null) {
            log.info("初始化消息发送端........");
            PRODUCER = new DefaultMQProducer("Producer");
            PRODUCER.setNamesrvAddr(this.namesrvAddr);

            try {
                PRODUCER.start();
            } catch (MQClientException var2) {
                log.error("消息发送端初始化失败", var2);
            }
        }

    }

    public static MQMessageStatus send(MQMessage msg, DelayLevel delay) {
        if (msg == null) {
            return MQMessageStatus.SUCCESS;
        } else if (PRODUCER == null) {
            log.info("MQ Producer has not configuration, message will not be send.");
            return MQMessageStatus.SUCCESS;
        } else {
            Message message = new Message();
            message.setTopic(msg.getTopic());
            message.setTags(msg.getSubTopic());
            message.setKeys(msg.getKey());
            message.setDelayTimeLevel(delay.ordinal());
            Serializable data = msg.getData();
            message.setBody(((String)data).getBytes(Charset.forName("UTF-8")));
            Map<String, String> userProperties = msg.getProperties();
            if (userProperties != null && !userProperties.isEmpty()) {
                Iterator var5 = userProperties.keySet().iterator();

                while(var5.hasNext()) {
                    String uk = (String)var5.next();
                    message.putUserProperty(uk, (String)userProperties.get(uk));
                }
            }

            try {
                SendResult send = PRODUCER.send(message);
                msg.setMsgId(send.getMsgId());
                log.debug("Producer send a message :\n" + msg + "\n" + send.toString());
                return MQMessageStatus.SUCCESS;
            } catch (MQClientException var7) {
                log.error(String.format("消息发送失败[%s]", message.toString()), var7);
                return MQMessageStatus.FAIL;
            } catch (MQBrokerException var8) {
                log.error(String.format("消息发送失败[%s]", message.toString()), var8);
                return MQMessageStatus.FAIL;
            } catch (RemotingException var9) {
                log.error(String.format("消息发送失败[%s]", message.toString()), var9);
                return MQMessageStatus.FAIL;
            } catch (InterruptedException var10) {
                log.error(String.format("消息发送失败[%s]", message.toString()), var10);
                return MQMessageStatus.FAIL;
            }
        }
    }

    public static MQMessageStatus send(MQMessage msg) {
        return send(msg, DelayLevel.REAL_TIME);
    }

    public static MQMessageStatus send(String topic, String subTopic, String key, String data) {
        return send(topic, subTopic, key, data, DelayLevel.REAL_TIME);
    }

    public static MQMessageStatus send(String topic, String subTopic, String key, String data, DelayLevel level) {
        MQMessage message = new MQMessage();
        message.setTopic(topic);
        message.setSubTopic(subTopic);
        message.setKey(key);
        message.setData(data);
        return send(message, level);
    }

    public static MQMessageStatus send(String topic, String subTopic, String key, String data, Map<String, String> userProperties) {
        MQMessage message = new MQMessage();
        message.setTopic(topic);
        message.setSubTopic(subTopic);
        message.setKey(key);
        message.setData(data);
        if (userProperties != null) {
            message.setProperties(userProperties);
        }

        return send(message);
    }

    public static void queryMessageId(String topic, String key) {
        try {
            List<MessageQueue> messageQueues = PRODUCER.fetchPublishMessageQueues(topic);
            long min = 0L;
            long max = 0L;

            MessageQueue messageQueue;
            for(Iterator var7 = messageQueues.iterator(); var7.hasNext(); max = Math.max(max, PRODUCER.maxOffset(messageQueue))) {
                messageQueue = (MessageQueue)var7.next();
                min = Math.min(min, PRODUCER.minOffset(messageQueue));
            }

            QueryResult queryResult = PRODUCER.queryMessage(topic, key, 32, min, max);

            MessageExt var9;
            for(Iterator var12 = queryResult.getMessageList().iterator(); var12.hasNext(); var9 = (MessageExt)var12.next()) {
                ;
            }
        } catch (InterruptedException | MQClientException var10) {
            log.error("查询消息失败", var10);
        }

    }

    @Override
    public void close() throws IOException {
        if (PRODUCER != null) {
            ;
        }

    }
}
