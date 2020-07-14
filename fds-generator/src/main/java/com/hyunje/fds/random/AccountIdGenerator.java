package com.hyunje.fds.random;

/**
 * 계좌 번호를 자동으로 생성하기 위한 클래스.
 */
public class AccountIdGenerator extends AbstractStringGenerator {

    public AccountIdGenerator() {
        this.CHAR_STRING = "0123456789";
        this.MAX_NAME_LEN = 16;
    }
}
