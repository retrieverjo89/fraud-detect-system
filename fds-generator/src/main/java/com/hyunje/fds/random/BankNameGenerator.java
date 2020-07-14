package com.hyunje.fds.random;

/**
 * 은행명을 자동으로 생성하기 위한 클래스.
 */
public class BankNameGenerator extends AbstractStringGenerator {

    public BankNameGenerator() {
        this.MAX_NAME_LEN = 8;
    }
}
