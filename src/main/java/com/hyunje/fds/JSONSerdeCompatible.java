package com.hyunje.fds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hyunje.fds.log.FinanceTransactionLog;

@SuppressWarnings("DefaultAnnotationParam") // being explicit for the example
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_t")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FinanceTransactionLog.class, name = "log"),
})
public interface JSONSerdeCompatible {
}
