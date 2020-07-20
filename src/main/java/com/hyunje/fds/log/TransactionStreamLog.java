package com.hyunje.fds.log;

import com.hyunje.fds.serdes.JSONSerdeCompatible;

public class TransactionStreamLog implements JSONSerdeCompatible {
    String accountId;
    int transactionAmount;
    String timestamp;

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return String.format("AccId: %s, Amount: %d, TransTime: %s", accountId, transactionAmount, timestamp);
    }
}
