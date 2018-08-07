package com.csl.commons.mq.model;

public enum MQMessageStatus {
    SUCCESS,
    FAIL,
    RECONSUME_LATER;

    private MQMessageStatus() {
    }
}