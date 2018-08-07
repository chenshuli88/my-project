package com.csl.commons.listener;

import base.utils.StringUtils;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;

import com.csl.commons.annotation.Consumer;
import com.csl.commons.mq.MessageBatchConsumer;
import com.csl.commons.mq.MessageConsumer;
import com.csl.commons.mq.model.MQMessage;
import com.csl.commons.mq.model.MQMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

public class ConsumerRegister {
    private static Logger log = LoggerFactory.getLogger("t.c.c.Consumer");
    private static ArrayList<MQPushConsumer> consumerHolder = new ArrayList();
    private String namesrvAddr;
    private String filterSourceRoot;
    private ApplicationContext ctx;

    public ConsumerRegister() {
    }

    public String getNamesrvAddr() {
        return this.namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getFilterSourceRoot() {
        return this.filterSourceRoot;
    }

    public void setFilterSourceRoot(String filterSourceRoot) {
        this.filterSourceRoot = filterSourceRoot;
    }

    private void initConsumer() {
        Map<String, MessageConsumer> beansOfType = this.ctx.getBeansOfType(MessageConsumer.class);
        Iterator var2 = beansOfType.keySet().iterator();

        while(true) {
            while(true) {
                MessageConsumer messageConsumer;
                Consumer consumer;
                String group;
                String filterCode;
                while(true) {
                    if (!var2.hasNext()) {
                        return;
                    }

                    String beanName = (String)var2.next();
                    messageConsumer = (MessageConsumer)beansOfType.get(beanName);
                    String canonicalName = messageConsumer.getClass().getCanonicalName();
                    log.debug("scanned consumer bean : " + canonicalName);
                    consumer = (Consumer)messageConsumer.getClass().getAnnotation(Consumer.class);
                    group = StringUtils.isTrimEmpty(consumer.group()) ? canonicalName.replaceAll("\\.", "-") : consumer.group().replaceAll("[^\\w\\-_]", "");
                    if (group.contains("$")) {
                        group = group.substring(0, group.indexOf(36));
                    }

                    String filter = consumer.filter();
                    log.debug("topic:[{}] tag:[{}] tags:[{}] filter class:[{}]", new Object[]{consumer.topic(), consumer.tag(), consumer.tags(), filter});
                    filterCode = null;
                    if (StringUtils.isTrimEmpty(filter)) {
                        break;
                    }

                    if (!filter.contains("..")) {
                        filterCode = MixAll.file2String(this.filterSourceRoot + filter + ".java");
                        break;
                    }
                }

                DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(group);
                pushConsumer.setNamesrvAddr(this.namesrvAddr);
                String tag = consumer.tag();
                if (StringUtils.isBlank(tag) && consumer.tags().length > 0) {
                    StringBuilder tagBuilder = new StringBuilder(20);
                    String[] var13 = consumer.tags();
                    int var14 = var13.length;

                    for(int var15 = 0; var15 < var14; ++var15) {
                        String s = var13[var15];
                        tagBuilder.append(s).append("||");
                    }

                    tagBuilder.delete(tagBuilder.length() - 2, -1);
                    tag = tagBuilder.toString();
                }

                if (StringUtils.isBlank(tag)) {
                    log.warn("Consumer {} donot have tag or tags, so ignored...", messageConsumer.getClass().getName());
                } else {
                    try {
                        if (StringUtils.isTrimEmpty(filterCode)) {
                            pushConsumer.subscribe(consumer.topic(), tag);
                        } else {
                            pushConsumer.subscribe(consumer.topic(), tag, filterCode);
                        }

                        if (consumer.maxReconsumeTimes() > 0) {
                            pushConsumer.setMaxReconsumeTimes(consumer.maxReconsumeTimes());
                        }

                        if (consumer.recomsumeAll()) {
                            pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                        } else {
                            pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
                            pushConsumer.setConsumeTimestamp(UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - 120000L));
                        }

                        if (consumer.batchSize() > 1 && messageConsumer instanceof MessageBatchConsumer) {
                            pushConsumer.setConsumeMessageBatchMaxSize(consumer.batchSize());
                            if (consumer.orderly()) {
                                pushConsumer.registerMessageListener(new ConsumerRegister.OrderlyBatchConsumeListener((MessageBatchConsumer)messageConsumer));
                            } else {
                                pushConsumer.registerMessageListener(new ConsumerRegister.ConcurrentlyBatchConsumeListener((MessageBatchConsumer)messageConsumer));
                            }
                        } else if (consumer.orderly()) {
                            pushConsumer.registerMessageListener(new ConsumerRegister.OrderlyConsumeListener(messageConsumer));
                        } else {
                            pushConsumer.registerMessageListener(new ConsumerRegister.ConcurrentlyConsumeListener(messageConsumer));
                        }

                        pushConsumer.start();
                        consumerHolder.add(pushConsumer);
                    } catch (Exception var17) {
                        var17.printStackTrace();
                    }
                }
            }
        }
    }

    private MQMessageStatus consume(MessageExt msg, MessageConsumer messageConsumer) {
        String canonicalName = messageConsumer.getClass().getCanonicalName();

        MQMessage message;
        try {
            message = this.convertMessage(msg);
        } catch (UnsupportedEncodingException var8) {
            log.warn("{} consumed failed, try it later! because : {}\nmessage key is: {}", new Object[]{canonicalName, var8.getMessage(), msg.getKeys(), var8});
            return MQMessageStatus.FAIL;
        }

        try {
            MQMessageStatus status = messageConsumer.consume(message);
            log.debug("\n{} consumed message:\n {}\n status : {}", new Object[]{canonicalName, msg.toString(), status.name()});
            return status;
        } catch (Exception var7) {
            log.warn("{} consumed failed, try it later! because : {}\nmessage is: {}", new Object[]{canonicalName, var7.getMessage(), msg.toString(), var7});
            return MQMessageStatus.RECONSUME_LATER;
        }
    }

    private MQMessageStatus batchConsume(List<MessageExt> messages, MessageBatchConsumer messageConsumer) {
        String canonicalName = messageConsumer.getClass().getCanonicalName();
        List<MQMessage> list = new ArrayList(messages.size());

        MQMessage message;
        for(Iterator var5 = messages.iterator(); var5.hasNext(); list.add(message)) {
            MessageExt msg = (MessageExt)var5.next();

            try {
                message = this.convertMessage(msg);
            } catch (UnsupportedEncodingException var10) {
                log.warn("{} consumed failed, try it later! because : {}\nmessage key is: {}", new Object[]{canonicalName, var10.getMessage(), msg.getKeys(), var10});
                return MQMessageStatus.FAIL;
            }
        }

        try {
            MQMessageStatus status = messageConsumer.consume(list);
            log.debug("\n{} consumed message:\n {}\n status : {}", new Object[]{canonicalName, list.toString(), status.name()});
            return status;
        } catch (Exception var9) {
            log.warn("{} consumed failed, try it later! because : {}\nmessage is: {}", new Object[]{canonicalName, var9.getMessage(), list.toString(), var9});
            return MQMessageStatus.RECONSUME_LATER;
        }
    }

    private MQMessage convertMessage(MessageExt msg) throws UnsupportedEncodingException {
        MQMessage message = new MQMessage();
        message.setTopic(msg.getTopic());
        message.setSubTopic(msg.getTags());
        message.setKey(msg.getKeys());
        message.setReconsumeTimes(msg.getReconsumeTimes());
        message.setProperties(msg.getProperties());
        String bodyStr = null;
        bodyStr = new String(msg.getBody(), "UTF-8");
        message.setData(bodyStr);
        return message;
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy consumers.............");
        if (!consumerHolder.isEmpty()) {
            Iterator var1 = consumerHolder.iterator();

            while(var1.hasNext()) {
                MQPushConsumer mqConsumer = (MQPushConsumer)var1.next();

                try {
                    mqConsumer.shutdown();
                } catch (Exception var4) {
                    log.warn(mqConsumer.toString() + " shutdown failed. This is very likely to create a memory leak.");
                }
            }
        }

    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.ctx = event.getApplicationContext();
        this.initConsumer();
    }

    class OrderlyBatchConsumeListener implements MessageListenerOrderly {
        private MessageBatchConsumer messageConsumer;

        public OrderlyBatchConsumeListener(MessageBatchConsumer messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
            MQMessageStatus status = ConsumerRegister.this.batchConsume(list, this.messageConsumer);
            return MQMessageStatus.SUCCESS == status ? ConsumeOrderlyStatus.SUCCESS : ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
    }

    class ConcurrentlyBatchConsumeListener implements MessageListenerConcurrently {
        private MessageBatchConsumer messageConsumer;

        public ConcurrentlyBatchConsumeListener(MessageBatchConsumer messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            if (list.isEmpty()) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } else {
                MQMessageStatus status = ConsumerRegister.this.batchConsume(list, this.messageConsumer);
                return MQMessageStatus.SUCCESS == status ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
    }

    class OrderlyConsumeListener implements MessageListenerOrderly {
        private MessageConsumer messageConsumer;

        public OrderlyConsumeListener(MessageConsumer messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
            if (list.isEmpty()) {
                return ConsumeOrderlyStatus.SUCCESS;
            } else {
                MessageExt msg = (MessageExt)list.get(0);
                MQMessageStatus status = ConsumerRegister.this.consume(msg, this.messageConsumer);
                return MQMessageStatus.SUCCESS == status ? ConsumeOrderlyStatus.SUCCESS : ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }
    }

    class ConcurrentlyConsumeListener implements MessageListenerConcurrently {
        private MessageConsumer messageConsumer;

        public ConcurrentlyConsumeListener(MessageConsumer messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            MessageExt msg = (MessageExt)list.get(0);
            MQMessageStatus status = ConsumerRegister.this.consume(msg, this.messageConsumer);
            return MQMessageStatus.SUCCESS == status ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}

