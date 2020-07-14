package com.hyunje.fds.random;

import org.json.JSONObject;

//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;

public class LogGenerator {
//    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private JSONObject makeRegisterLog(long userId, String userName, String birthDate, String registerTime) {
//        LocalDateTime birthDateTime = LocalDateTime.parse(birthDate, dateFormatter);
//        LocalDateTime regTime = LocalDateTime.parse(registerTime, timeFormatter);

        JSONObject json = new JSONObject();
        json.put("user-id", userId);
        json.put("user-name", userName);
        json.put("birth-date", birthDate);
        json.put("register-datetime", registerTime);

        return json;
    }

    private JSONObject makeCreateAccountLog(long userId, String accountId, String tradeTime) {
        JSONObject json = new JSONObject();
        json.put("user-id", userId);
        json.put("account-id", accountId);
        json.put("trade-datetime", tradeTime);

        return json;
    }

    private JSONObject makeDepositLog(long userId, String toAccountId, int amount, String tradeTime) {
        JSONObject json = new JSONObject();
        json.put("user-id", userId);
        json.put("account-id", toAccountId);
        json.put("amount", amount);
        json.put("trade-datetime", tradeTime);

        return json;
    }

    private JSONObject makeWithdrawLog(long userId, String fromAccountId, int amount, String tradeTime) {
        JSONObject json = new JSONObject();
        json.put("user-id", userId);
        json.put("account-id", fromAccountId);
        json.put("amount", amount);
        json.put("trade-datetime", tradeTime);

        return json;
    }

    private JSONObject makeTransferLog(long userId, String fromAccountId, String toBankName, String toBankAccountId,
                                     String toBankUserName, int amount, String tradeTime) {
        JSONObject json = new JSONObject();
        json.put("user-id", userId);
        json.put("account-id", fromAccountId);
        json.put("to-bank-name", toBankName);
        json.put("to-bank-account-id", toBankAccountId);
        json.put("to-bank-user-name", toBankUserName);
        json.put("amount", amount);
        json.put("trade-datetime", tradeTime);

        return json;
    }

}
