package com.hyunje.fds.random;

/**
 * 고객 번호를 자동으로 생성하기 위한 클래스.
 */
public class UserIdGenerator extends AbstractStringGenerator {

    public UserIdGenerator() {
        this.CHAR_STRING = "0123456789";
        this.MAX_NAME_LEN = 10;
    }

    public UserIdGenerator(int maxlen){
        this.CHAR_STRING = "0123456789";
        this.MAX_NAME_LEN = 10;
    }
}
