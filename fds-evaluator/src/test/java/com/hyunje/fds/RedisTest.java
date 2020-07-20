package com.hyunje.fds;

import com.hyunje.fds.log.Constants;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

public class RedisTest {
    public static void main(String[] args) {
        String redisHost = "172.16.40.3";
        int redisPort = 6379;
        Jedis redis = new Jedis(redisHost, redisPort);

//        redis.hset("user_xxx", "user-name", "test-name");
//        redis.hset("user_xxx", "user-reg-time", "20200710");
//        System.out.println(redis.hget("user_xxx", "user-name"));
//        System.out.println(redis.hgetAll("user_xxx"));
//        redis.del("user_xxx");
//        System.out.println(redis.hget("user_xxx", "user-name"));
        System.out.println(redis.hget(Constants.CREATE_ACC_RERDIS_PREFIX + "0000", "userId"));
        System.out.println(redis.hget(Constants.REGISTER_REDIS_PREFIX + "0000", "birthDate"));
        System.out.println(redis.hget(Constants.REGISTER_REDIS_PREFIX + "1", "birthDate"));
    }
}
