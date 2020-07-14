package com.hyunje.fds.random;

import java.util.Random;

/**
 * 숫자를 자동 생성하기 위한 추상 클래스.
 */
public abstract class AbstractIntGenerator implements RandomGenerator<Integer> {
    private static final Random random = new Random();
    private static final int MULTIPLIER = 10000;
    protected static int MAX_DIGIT = 100;

    @Override
    public Integer generate(){
        return random.nextInt(MAX_DIGIT) * MULTIPLIER;
    }

}
