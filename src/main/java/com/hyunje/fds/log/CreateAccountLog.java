package com.hyunje.fds.log;

import com.hyunje.fds.serdes.JSONSerdeCompatible;

public class CreateAccountLog implements JSONSerdeCompatible {
    private String userId;
    private String accountId;
    private String tradeTime;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTradeTime() {
        return this.tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }
}
