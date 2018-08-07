package com.csl.commons.mq.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MQMessage implements Serializable {
    private String msgId;
    private String topic;
    private String subTopic;
    private String key;
    private String data;
    private int reconsumeTimes;
    private Map<String, String> properties;

    public MQMessage() {
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubTopic() {
        return this.subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void put(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }

        this.properties.put(key, value);
    }

    public String remove(String key) {
        return this.properties == null ? null : (String)this.properties.remove(key);
    }

    @Override
    public String toString() {
        return "MQMessage{msgId='" + this.msgId + '\'' + ", topic='" + this.topic + '\'' + ", subTopic='" + this.subTopic + '\'' + ", key='" + this.key + '\'' + ", data=" + this.data + ", properties=" + this.properties + '}';
    }

    public int getReconsumeTimes() {
        return this.reconsumeTimes;
    }

    public void setReconsumeTimes(int reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }
}