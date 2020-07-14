package com.hyunje.fds.random;

/**
 * 고객명 이름을 자동으로 생성하기 위한 클래스.
 */
public class UserNameGenerator extends AbstractStringGenerator {

    public UserNameGenerator() {
        this.MAX_NAME_LEN = 16;
    }
}
