package com.hyunje.fds.log;

import com.hyunje.fds.JSONSerdeCompatible;

public class TransferLog implements JSONSerdeCompatible {
    private String userId;
    private String accountId;
    private String toBankName;
    private String toBankAccountId;
    private String toBankUserName;
    private int amount;
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

    public String getToBankName() {
        return this.toBankName;
    }

    public void setToBankName(String toBankName) {
        this.toBankName = toBankName;
    }

    public String getToBankAccountId() {
        return this.toBankAccountId;
    }

    public void setToBankAccountId(String toBankAccountId) {
        this.toBankAccountId = toBankAccountId;
    }

    public String getToBankUserName() {
        return this.toBankUserName;
    }

    public void setToBankUserName(String toBankUserName) {
        this.toBankUserName = toBankUserName;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTradeTime() {
        return this.tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }
}
