package com.tank.common;

import lombok.val;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;

import java.util.Objects;
import java.util.Optional;

/**
 * @author fuchun
 */
public class TransactionSenderWrapper {

  public static TransactionSenderWrapper createInstance() {
    return Single.INSTANCE.fetchTransactionSenderWrapper();
  }

  public <T> SendResult sendMsg(final T msg, final TransactionMsgSender<T> transactionMsgSender) {

    if (Objects.isNull(this.transactionListener)) {
      throw new NullPointerException("please init transactionListener first !!!!!");
    }

    if (Objects.isNull(msg)) {
      throw new NullPointerException("transaction message not allowd empty");
    }

    val producer = Single.INSTANCE.fetchTransactionMQProducer();
    producer.setTransactionListener(this.transactionListener);
    try {
      producer.start();
    } catch (MQClientException e) {
      e.printStackTrace();
    }

    return transactionMsgSender.sendMessage(msg, producer);

  }


  public TransactionSenderWrapper initTransactionListener(final TransactionListener transactionListener) {
    this.transactionListener = transactionListener;
    return this;
  }

  public void closeTransactionSenderSync() {
    val producer = Single.INSTANCE.fetchTransactionMQProducer();
    Optional.ofNullable(producer).ifPresent(TransactionMQProducer::shutdown);
  }

  enum Single {

    INSTANCE;

    Single() {
      this.transactionSenderWrapper = new TransactionSenderWrapper();
      this.initTransactionSenderWrapper();
      System.out.println("init transaction procedure complete");
    }

    protected TransactionSenderWrapper fetchTransactionSenderWrapper() {
      return this.transactionSenderWrapper;
    }

    protected TransactionMQProducer fetchTransactionMQProducer() {
      return this.transactionMQProducer;
    }

    private void initTransactionSenderWrapper() {
      final String nameSrv = ConfigLoader.valueByKey("rocketmqServer.nameSvrAddr", (k, c) -> c.getString(k));
      final String groupName = ConfigLoader.valueByKey("rocketmqServer.procedureName", (k, c) -> c.getString(k));
      this.transactionMQProducer = new TransactionMQProducer(groupName);
      this.transactionMQProducer.setNamesrvAddr(nameSrv);
    }

    private TransactionSenderWrapper transactionSenderWrapper;

    private TransactionMQProducer transactionMQProducer;
  }


  private TransactionSenderWrapper() {

  }


  private TransactionListener transactionListener;
}
