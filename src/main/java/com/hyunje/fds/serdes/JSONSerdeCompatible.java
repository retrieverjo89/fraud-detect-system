package com.hyunje.fds.serdes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hyunje.fds.log.*;

@SuppressWarnings("DefaultAnnotationParam") // being explicit for the example
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_t")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FinanceTransactionLog.class, name = "finance"),
        @JsonSubTypes.Type(value = DepositLog.class, name = "deposit"),
        @JsonSubTypes.Type(value = WithdrawLog.class, name = "withdraw"),
        @JsonSubTypes.Type(value = TransferLog.class, name = "transfer"),
        @JsonSubTypes.Type(value = TransactionStreamLog.class, name = "transferstream"),
})
public interface JSONSerdeCompatible {
}
