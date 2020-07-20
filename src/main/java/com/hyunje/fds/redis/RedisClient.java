package com.hyunje.fds.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.CreateAccountLog;
import com.hyunje.fds.log.RegisterLog;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class RedisClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Jedis redisClient;

    public RedisClient(String host, int port){
        redisClient = new Jedis(host, port);
    }

    public void setRegisterUserLog(RegisterLog log){
        String logKey = Constants.REGISTER_REDIS_PREFIX + log.getUserId();
        HashMap<String, String> logInfo = this.objectMapper.convertValue(log,
                new TypeReference<HashMap<String, String>>() {});
        this.redisClient.hset(logKey, logInfo);
    }

    public void setCreateAccountLog(CreateAccountLog log){
        String logKey = Constants.CREATE_ACC_RERDIS_PREFIX + log.getAccountId();
        Map<String, String> logInfo = this.objectMapper.convertValue(log,
                new TypeReference<Map<String, String>>() {});
        this.redisClient.hset(logKey, logInfo);
    }

    public RegisterLog getRegisteredUserInfo(String userId){
        String logKey = Constants.REGISTER_REDIS_PREFIX + userId;
        Map<String, String> valMap = this.redisClient.hgetAll(logKey);
        return objectMapper.convertValue(valMap, RegisterLog.class);
    }

    public String getRegisteredUserInfo(String userId, String field){
        String logKey = Constants.REGISTER_REDIS_PREFIX + userId;
        return this.redisClient.hget(logKey, field);
    }

    public CreateAccountLog getCreatedAccountInfo(String accountId){
        String logKey = Constants.CREATE_ACC_RERDIS_PREFIX + accountId;
        Map<String, String> valMap = this.redisClient.hgetAll(logKey);
        return objectMapper.convertValue(valMap, CreateAccountLog.class);
    }

    public String getCreatedAccountInfo(String accountId, String field){
        String logKey = Constants.CREATE_ACC_RERDIS_PREFIX + accountId;
        return this.redisClient.hget(logKey, field);
    }

}
