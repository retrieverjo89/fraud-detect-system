package com.hyunje.fds.log;

public final class Constants {
    public static final String BOOTSTRAP_SERVER = "172.16.40.3:9092";
    public static final String REDIS_SERVER = "172.16.40.3";
    public static final int REDIS_PORT = 6379;

    public static final String REGISTER_REDIS_PREFIX = "user_";
    public static final String CREATE_ACC_RERDIS_PREFIX = "account_";

    public static final String BIRTHDAY_FORMAT = "yyyy-MM-dd";
    public static final String CREATE_DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String FINANCE_LOG_KEY_REGISTER = "register";
    public static final String FINANCE_LOG_KEY_CREATEACCOUNT = "create-account";
    public static final String FINANCE_LOG_KEY_DEPOSIT = "deposit";
    public static final String FINANCE_LOG_KEY_WITHDRAW = "withdraw";
    public static final String FINANCE_LOG_KEY_TRANSFER = "transfer";

    public static final String RAW_LOG_GENERATOR_TOPIC = "fds.transactions";

    public static final String DEPOSIT_LOG_STREAM_SRC_TOPIC = "fds.deposits.src";
    public static final String DEPOSIT_LOG_STREAM_TOPIC = "fds.deposits.stream";
    public static final String DEPOSIT_LOG_STREAM_SRC_CONSUMER_GROUP = "fds.deposits.src.consumer";

    public static final String WITHDRAW_LOG_STREAM_SRC_TOPIC = "fds.withdraw.src";
    public static final String WITHDRAW_LOG_STREAM_SRC_CONSUMER_GROUP = "fds.withdraw.src.consumer";
    public static final String WITHDRAW_LOG_STREAM_TOPIC = "fds.withdraw.and.transfer.stream";

    public static final String TRANSFER_LOG_STREAM_SRC_TOPIC = "fds.transfer.src";
    public static final String TRANSFER_LOG_STREAM_SRC_CONSUMER_GROUP = "fds.transfer.src.consumer";
    public static final String TRANSFER_LOG_STREAM_TOPIC = "fds.withdraw.and.transfer.stream";

    public static final String WITHDRAW_AND_TRANSFER_LOG_STREAM_TOPIC = WITHDRAW_LOG_STREAM_TOPIC;

    public static final String EVALUATOR_STREAM_APP_ID = "fds.evaluator";
    public static final String DETECTED_FRAUD_TOPIC = "fds.detections";

    private Constants() {
    }
}
