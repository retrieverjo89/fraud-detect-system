package com.hyunje.fds.redis;

import com.hyunje.fds.log.CreateAccountLog;
import com.hyunje.fds.log.RegisterLog;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class RedisClientTest {
    public final String redisHost = "172.16.40.3";
    public final int redisPort = 6379;

    public final String registerUserLogPrefix = "user_";
    public final String createAccountLogPrefix = "account_";

    public final String userId = "0000";
    public final String userName = "UUUUXXXXX";
    public final String birthDate = "19891001";
    public final String regDate = "20200701";

    public final String accountId = "99999999";
    public final String tradeTime = "20200702";

    public String getRegisterLogId(String uid) {
        return registerUserLogPrefix + uid;
    }

    public String getCreateLogId(String accId) {
        return createAccountLogPrefix + accId;
    }

    public void setRegisterLogToRedis() {
        Jedis redis = new Jedis(redisHost, redisPort);
        Map<String, String> regValMap = new HashMap<>();
        regValMap.put("userId", userId);
        regValMap.put("userName", userName);
        regValMap.put("birthDate", birthDate);
        regValMap.put("registerTime", regDate);

        redis.hset(getRegisterLogId(userId), regValMap);
    }

    public void deleteRegisterLog() {
        Jedis redis = new Jedis(redisHost, redisPort);
        redis.del(getRegisterLogId(userId));
    }

    public void setCreateAccountLogToRedis() {
        Jedis redis = new Jedis(redisHost, redisPort);
        Map<String, String> accLValMap = new HashMap<>();
        accLValMap.put("userId", userId);
        accLValMap.put("accountId", accountId);
        accLValMap.put("tradeTime", tradeTime);

        redis.hset(getCreateLogId(accountId), accLValMap);
    }

    public void deleteCreateAccountLog() {
        Jedis redis = new Jedis(redisHost, redisPort);
        redis.del(getCreateLogId(accountId));
    }


    @Test
    public void testSetRegisterUserLog() {
        RedisClient redisClient = new RedisClient(redisHost, redisPort);

        RegisterLog registerLog = new RegisterLog();
        registerLog.setUserName(userName);
        registerLog.setUserId(userId);
        registerLog.setBirthDate(birthDate);
        registerLog.setRegisterTime(regDate);

        redisClient.setRegisterUserLog(registerLog);

        assert registerLog.getUserId().equals(redisClient.getRegisteredUserInfo(userId, "userId"));
        assert registerLog.getUserName().equals(redisClient.getRegisteredUserInfo(userId, "userName"));
        assert registerLog.getBirthDate().equals(redisClient.getRegisteredUserInfo(userId, "birthDate"));
        assert registerLog.getRegisterTime().equals(redisClient.getRegisteredUserInfo(userId, "registerTime"));

        RegisterLog prevLog = redisClient.getRegisteredUserInfo(userId);

        assert registerLog.getUserId().equals(prevLog.getUserId());
        assert registerLog.getUserName().equals(prevLog.getUserName());
        assert registerLog.getBirthDate().equals(prevLog.getBirthDate());
        assert registerLog.getRegisterTime().equals(prevLog.getRegisterTime());
    }

    @Test
    public void testSetCreateAccountLog() {
        RedisClient redisClient = new RedisClient(redisHost, redisPort);

        CreateAccountLog createAccountLog = new CreateAccountLog();
        createAccountLog.setUserId(userId);
        createAccountLog.setAccountId(accountId);
        createAccountLog.setTradeTime(tradeTime);

        redisClient.setCreateAccountLog(createAccountLog);

        assert createAccountLog.getAccountId().equals(redisClient.getCreatedAccountInfo(accountId, "accountId"));
        assert createAccountLog.getUserId().equals(redisClient.getCreatedAccountInfo(accountId, "userId"));
        assert createAccountLog.getTradeTime().equals(redisClient.getCreatedAccountInfo(accountId, "tradeTime"));

        CreateAccountLog prevLog = redisClient.getCreatedAccountInfo(accountId);

        assert createAccountLog.getAccountId().equals(prevLog.getAccountId());
        assert createAccountLog.getUserId().equals(prevLog.getUserId());
        assert createAccountLog.getTradeTime().equals(prevLog.getTradeTime());
    }
}