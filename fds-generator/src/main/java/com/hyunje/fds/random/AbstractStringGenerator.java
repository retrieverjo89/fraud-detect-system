package com.hyunje.fds.random;

import java.util.Random;

/**
 * 문자열을 자동생성 하기 위한 추상 클래스.
 */
public abstract class AbstractStringGenerator implements RandomGenerator<String> {
    private static final Random random = new Random();
    protected String CHAR_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOKQRSTUVWXYZ0123456789";
    protected int MAX_NAME_LEN = 8;

    @Override
    public String generate(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < this.MAX_NAME_LEN; i++)
            builder.append(this.CHAR_STRING.charAt(random.nextInt(this.CHAR_STRING.length())));
        return builder.toString();
    }
}
