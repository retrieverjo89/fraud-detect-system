package com.hyunje.fds;

import com.hyunje.fds.streams.DepositStreamGenerator;
import com.hyunje.fds.streams.TransferStreamGenerator;
import com.hyunje.fds.streams.WithdrawStreamGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FdsSystemApp {
    public static final Logger logger = LogManager.getLogger(FdsSystemApp.class);

    public static void main(String[] args) {
        logger.error("Start fds system");
        // Kafka Streams 를 이용해 Fraud를 Detect하는 Thread
        TransactionEvaluator transactionEvaluator = new TransactionEvaluator();
        Thread evaluatorThread = new Thread(transactionEvaluator);
        evaluatorThread.start();

        // 입금 로그를 Kafka Stream 로 전달하는 Producer Thread 수행
        DepositStreamGenerator depositStreamGenerator = new DepositStreamGenerator();
        Thread depositStreamThread = new Thread(depositStreamGenerator);
        depositStreamThread.start();

        // 이체 로그를 Kafka Stream 로 전달하는 Producer Thread 수행
        TransferStreamGenerator transferStreamGenerator = new TransferStreamGenerator();
        Thread transferStreamThread = new Thread(transferStreamGenerator);
        transferStreamThread.start();

        // 출금 로그를 Kafka Stream 로 전달하는 Producer Thread 수행
        WithdrawStreamGenerator withdrawStreamGenerator = new WithdrawStreamGenerator();
        Thread withdrawStreamThread = new Thread(withdrawStreamGenerator);
        withdrawStreamThread.start();

        // 사용자로부터 생성된 로그를 전처리하고, 각 용도별 Producer로 전달하는 Consumer Thread 수행
        TransactionConsumer transactionConsumer = new TransactionConsumer();
        Thread transConThread = new Thread(transactionConsumer);
        transConThread.start();

        // 사용자 로그를 생성하여 전처리 Consumer로 전달하는 Thread 수행
        TransactionGenerator transactionGenerator = new TransactionGenerator();
        Thread transGenThread = new Thread(transactionGenerator);
        transGenThread.start();

        // Fraud Detect 가 수행된 결과를 출력하는 Consumer Thread 수행
        FraudDetector fraudDetector = new FraudDetector();
        Thread detectorThread = new Thread(fraudDetector);
        detectorThread.start();


        logger.error("Finished start every components");
    }
}
