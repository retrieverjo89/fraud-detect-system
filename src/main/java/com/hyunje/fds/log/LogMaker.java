package com.hyunje.fds.log;

//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;

public class LogMaker {
//    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public RegisterLog makeRegisterLog(String userId, String userName, String birthDate, String registerTime) {
//        LocalDateTime birthDateTime = LocalDateTime.parse(birthDate, dateFormatter);
//        LocalDateTime regTime = LocalDateTime.parse(registerTime, timeFormatter);

        RegisterLog registerLog = new RegisterLog();
        registerLog.setUserId(userId);
        registerLog.setUserName(userName);
        registerLog.setBirthDate(birthDate);
        registerLog.setRegisterTime(registerTime);
        return registerLog;
    }

    public CreateAccountLog makeCreateAccountLog(String userId, String accountId, String tradeTime) {

        CreateAccountLog createAccountLog = new CreateAccountLog();
        createAccountLog.setUserId(userId);
        createAccountLog.setAccountId(accountId);
        createAccountLog.setTradeTime(tradeTime);
        return createAccountLog;
    }

    public DepositLog makeDepositLog(String userId, String toAccountId, int amount, String tradeTime) {

        DepositLog depositLog = new DepositLog();
        depositLog.setUserId(userId);
        depositLog.setAccountId(toAccountId);
        depositLog.setAmount(amount);
        depositLog.setTradeTime(tradeTime);

        return depositLog;
    }

    public WithdrawLog makeWithdrawLog(String userId, String fromAccountId, int amount, String tradeTime) {

        WithdrawLog withdrawLog = new WithdrawLog();
        withdrawLog.setUserId(userId);
        withdrawLog.setAccountId(fromAccountId);
        withdrawLog.setAmount(amount);
        withdrawLog.setTradeTime(tradeTime);

        return withdrawLog;
    }

    public TransferLog makeTransferLog(String userId, String fromAccountId, String toBankName, String toBankAccountId,
                                       String toBankUserName, int amount, String tradeTime) {
        TransferLog transferLog = new TransferLog();
        transferLog.setUserId(userId);
        transferLog.setAccountId(fromAccountId);
        transferLog.setToBankName(toBankName);
        transferLog.setToBankAccountId(toBankAccountId);
        transferLog.setToBankUserName(toBankUserName);
        transferLog.setAmount(amount);
        transferLog.setTradeTime(tradeTime);

        return transferLog;
    }
}
