package com.hyunje.fds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunje.fds.log.*;
import com.hyunje.fds.random.AccountIdGenerator;
import com.hyunje.fds.random.UserIdGenerator;
import com.hyunje.fds.random.UserNameGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;

public class LogMaker {
    private static String TARGET_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserIdGenerator userIdGenerator = new UserIdGenerator(8);
    private final UserNameGenerator userNameGenerator = new UserNameGenerator(4);
    private final AccountIdGenerator accountIdGenerator = new AccountIdGenerator(8);

    public LogMaker(String targetTopic) {
        TARGET_TOPIC = targetTopic;
    }

    private ProducerRecord<String, FinanceTransactionLog> makeTransferLog(String userId, String accId, int amount, String transferDate) throws JsonProcessingException {
        TransferLog transferLog = new TransferLog();
        transferLog.setUserId(userId);
        transferLog.setAccountId(accId);
        transferLog.setAmount(amount);
        transferLog.setTradeTime(transferDate);

        FinanceTransactionLog transferTransactionLog = new FinanceTransactionLog();
        transferTransactionLog.setJsonString(objectMapper.writeValueAsString(transferLog));
        transferTransactionLog.setLogType(Constants.FINANCE_LOG_KEY_TRANSFER);

        return new ProducerRecord<>(TARGET_TOPIC, Constants.FINANCE_LOG_KEY_TRANSFER, transferTransactionLog);
    }

    private ProducerRecord<String, FinanceTransactionLog> makeWithdrawLog(String userId, String accId, int amount, String withdrawTime) throws JsonProcessingException {
        WithdrawLog withdrawLog = new WithdrawLog();
        withdrawLog.setUserId(userId);
        withdrawLog.setAccountId(accId);
        withdrawLog.setAmount(amount);
        withdrawLog.setTradeTime(withdrawTime);

        FinanceTransactionLog withdrawTransactionLog = new FinanceTransactionLog();
        withdrawTransactionLog.setJsonString(objectMapper.writeValueAsString(withdrawLog));
        withdrawTransactionLog.setLogType(Constants.FINANCE_LOG_KEY_WITHDRAW);

        return new ProducerRecord<>(TARGET_TOPIC, Constants.FINANCE_LOG_KEY_WITHDRAW, withdrawTransactionLog);
    }

    private ProducerRecord<String, FinanceTransactionLog> makeDepositLog(String userId, String accId, int amount, String depositTime) throws JsonProcessingException {
        DepositLog depositLog = new DepositLog();
        depositLog.setUserId(userId);
        depositLog.setAccountId(accId);
        depositLog.setAmount(amount);
        depositLog.setTradeTime(depositTime);

        FinanceTransactionLog depositTransactionLog = new FinanceTransactionLog();
        depositTransactionLog.setJsonString(objectMapper.writeValueAsString(depositLog));
        depositTransactionLog.setLogType(Constants.FINANCE_LOG_KEY_DEPOSIT);

        return new ProducerRecord<>(TARGET_TOPIC, Constants.FINANCE_LOG_KEY_DEPOSIT, depositTransactionLog);
    }

    private ProducerRecord<String, FinanceTransactionLog> makeCreateAccountLog(String userId, String accId, String createdTime) throws JsonProcessingException {
        CreateAccountLog createAccountLog = new CreateAccountLog();
        createAccountLog.setUserId(userId);
        createAccountLog.setAccountId(accId);
        createAccountLog.setTradeTime(createdTime);

        FinanceTransactionLog cAccTransactionLog = new FinanceTransactionLog();
        cAccTransactionLog.setJsonString(objectMapper.writeValueAsString(createAccountLog));
        cAccTransactionLog.setLogType(Constants.FINANCE_LOG_KEY_CREATEACCOUNT);

        return new ProducerRecord<>(TARGET_TOPIC, Constants.FINANCE_LOG_KEY_CREATEACCOUNT, cAccTransactionLog);
    }

    private ProducerRecord<String, FinanceTransactionLog> makeRegisterLog(String userId, String userName, String birthDate, String registerTime) throws JsonProcessingException {
        RegisterLog registerLog = new RegisterLog();
        registerLog.setUserId(userId);
        registerLog.setUserName(userName);
        registerLog.setBirthDate(birthDate);
        registerLog.setRegisterTime(registerTime);

        FinanceTransactionLog registerTransactionLog = new FinanceTransactionLog();
        registerTransactionLog.setJsonString(objectMapper.writeValueAsString(registerLog));
        registerTransactionLog.setLogType(Constants.FINANCE_LOG_KEY_REGISTER);

        return new ProducerRecord<>(TARGET_TOPIC, Constants.FINANCE_LOG_KEY_REGISTER, registerTransactionLog);
    }

    public List<ProducerRecord<String, FinanceTransactionLog>> makeFraudLogs() throws JsonProcessingException {
        List<ProducerRecord<String, FinanceTransactionLog>> recordList = new ArrayList<>();

        String userId = userIdGenerator.generate();
        String userName = userNameGenerator.generate();
        String userBirthDate = "19591001";
        String registerTime = "20200720090000";
        String createAccTime = "20200720100000";
        String accId = accountIdGenerator.generate();
        List<String> depositTimeList = new ArrayList<>();
        depositTimeList.add("20200719101000");
        depositTimeList.add("20200719102000");
        depositTimeList.add("20200720090000");
        String withdrawTime = "20200720100000";
        String transferTime = "20200720101000";

        ProducerRecord<String, FinanceTransactionLog> registerRecord = makeRegisterLog(userId, userName, userBirthDate, registerTime);
        recordList.add(registerRecord);

        ProducerRecord<String, FinanceTransactionLog> createAccRecord = makeCreateAccountLog(userId, accId, createAccTime);
        recordList.add(createAccRecord);

        for (String depTime : depositTimeList) {
            ProducerRecord<String, FinanceTransactionLog> depositRecord = makeDepositLog(userId, accId, 100, depTime);
            recordList.add(depositRecord);
        }


        ProducerRecord<String, FinanceTransactionLog> withdrawRecord = makeWithdrawLog(userId, accId, 100, withdrawTime);
        recordList.add(withdrawRecord);

        ProducerRecord<String, FinanceTransactionLog> transferRecord = makeTransferLog(userId, accId, 200, transferTime);
        recordList.add(transferRecord);

        return recordList;
    }

    public List<ProducerRecord<String, FinanceTransactionLog>> makeNonFraudLogs() throws JsonProcessingException {
        String userId = userIdGenerator.generate();
        String userName = userNameGenerator.generate();
        String userBirthDate = "19561001";
        String registerTime = "20200720010000";
        String createAccTime = "20200720103000";
        String accId = accountIdGenerator.generate();

        List<String> depositTimeList = new ArrayList<>();
        depositTimeList.add("20200720104000");
        depositTimeList.add("20200720104500");
        depositTimeList.add("20200720113000");

        String transferTime = "20200720201000";
        String withdrawTime = "20200720201100";

        List<ProducerRecord<String, FinanceTransactionLog>> recordList = new ArrayList<>();

        ProducerRecord<String, FinanceTransactionLog> registerRecord = makeRegisterLog(userId, userName, userBirthDate, registerTime);
        recordList.add(registerRecord);

        ProducerRecord<String, FinanceTransactionLog> createAccRecord = makeCreateAccountLog(userId, accId, createAccTime);
        recordList.add(createAccRecord);

        for (String depTime : depositTimeList) {
            ProducerRecord<String, FinanceTransactionLog> depositRecord = makeDepositLog(userId, accId, 150, depTime);
            recordList.add(depositRecord);
        }


        ProducerRecord<String, FinanceTransactionLog> withdrawRecord = makeWithdrawLog(userId, accId, 30, withdrawTime);
        recordList.add(withdrawRecord);

        ProducerRecord<String, FinanceTransactionLog> transferRecord = makeTransferLog(userId, accId, 100, transferTime);
        recordList.add(transferRecord);

        return recordList;
    }
}
