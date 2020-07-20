package com.hyunje.fds;

import com.hyunje.fds.streams.DepositStreamGenerator;
import com.hyunje.fds.streams.TransferStreamGenerator;
import com.hyunje.fds.streams.WithdrawStreamGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FdsSystemApp {
    public static final Logger logger = LogManager.getLogger(FdsSystemApp.class);

    public static void main(String[] args) {
        logger.warn("Start fds system");

        FraudDetector fraudDetector = new FraudDetector();
        Thread detectorThread = new Thread(fraudDetector);
        detectorThread.start();

        TransactionEvaluator transactionEvaluator = new TransactionEvaluator();
        Thread evaluatorThread = new Thread(transactionEvaluator);
        evaluatorThread.start();

        DepositStreamGenerator depositStreamGenerator = new DepositStreamGenerator();
        Thread depositStreamThread = new Thread(depositStreamGenerator);
        depositStreamThread.start();

        TransferStreamGenerator transferStreamGenerator = new TransferStreamGenerator();
        Thread transferStreamThread = new Thread(transferStreamGenerator);
        transferStreamThread.start();

        WithdrawStreamGenerator withdrawStreamGenerator = new WithdrawStreamGenerator();
        Thread withdrawStreamThread = new Thread(withdrawStreamGenerator);
        withdrawStreamThread.start();

        TransactionConsumer transactionConsumer = new TransactionConsumer();
        Thread transConThread = new Thread(transactionConsumer);
        transConThread.start();

        TransactionGenerator transactionGenerator = new TransactionGenerator();
        Thread transGenThread = new Thread(transactionGenerator);
        transGenThread.start();

        logger.info("Finished start every components");
    }
}
