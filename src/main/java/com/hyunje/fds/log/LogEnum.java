package com.hyunje.fds.log;

import java.util.HashMap;
import java.util.Map;

public enum LogEnum {
    REGISTER(0),
    CREATE_ACCOUNT(1),
    DEPOSIT(2),
    WITHDRAW(3),
    TRANSFER(4);

    private int value;
    private static Map<Integer, LogEnum> map = new HashMap<>();

    private LogEnum(int value) {
        this.value = value;
    }

    static {
        for (LogEnum logType : LogEnum.values()) {
            map.put(logType.value, logType);
        }
    }

    public static LogEnum valueOf(int logType) {
        return (LogEnum) map.get(logType);
    }

    public int getValue(){
        return value;
    }
}
