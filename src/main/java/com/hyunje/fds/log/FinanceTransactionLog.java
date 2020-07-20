package com.hyunje.fds.log;

import com.hyunje.fds.serdes.JSONSerdeCompatible;

public class FinanceTransactionLog implements JSONSerdeCompatible {
    String logType;  //log type을 넣을것인까 아니면 partition 의 key 로 할 것인가?
    String jsonString;

    public String getLogType() {
        return this.logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getJsonString() {
        return this.jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
