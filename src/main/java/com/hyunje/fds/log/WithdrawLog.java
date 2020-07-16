package com.hyunje.fds.log;

import com.hyunje.fds.JSONSerdeCompatible;

public class WithdrawLog implements JSONSerdeCompatible {
    private String userId;
    private String accountId;
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
