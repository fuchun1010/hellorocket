package com.tank.common;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.tank.common.ConfigLoader.valueByKey;

/**
 * @author
 */
public class SimpleSenderWrapper {

  public static SimpleSenderWrapper createInstance() {
    return Single.INSTANCE.fetchSendWrapper();
  }

  /**
   * @param msg
   * @param simpleMsgSender
   */
  public <T> void sendMsg(final T msg, final SimpleMsgSender<T> simpleMsgSender) {
    final DefaultMQProducer producer = Single.INSTANCE.fetchSimpleProducer();
    SendResult sendResult = null;
    try {
      sendResult = simpleMsgSender.sendMsg(msg, producer);
    } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
      e.printStackTrace();
    }
    System.out.printf("%s%n", sendResult);
  }

  public <T> void sendMsg(final List<T> msg, final SimpleMsgSender<T> simpleMsgSender) {
    final DefaultMQProducer producer = Single.INSTANCE.fetchSimpleProducer();
    msg.forEach(message -> {
      try {
        simpleMsgSender.sendMsg(message, producer);
      } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
        e.printStackTrace();
      }
    });
  }

  public void closeSenderAsync() {
    final DefaultMQProducer producer = Single.INSTANCE.fetchSimpleProducer();
    Runtime.getRuntime().addShutdownHook(new Thread(producer::shutdown));
  }

  public void closeSenderSync() {
    final DefaultMQProducer producer = Single.INSTANCE.fetchSimpleProducer();
    Optional.ofNullable(producer).ifPresent(DefaultMQProducer::shutdown);
  }

  enum Single {

    INSTANCE;

    private Single() {
      this.simpleSenderWrapper = new SimpleSenderWrapper();
      final String groupName = valueByKey("rocketmqServer.producerGroupName", (k, c) -> c.getString(k));
      final String nameSvr = valueByKey("rocketmqServer.nameSvrAddr", (k, c) -> c.getString(k));
      this.producer = new DefaultMQProducer(groupName);
      this.producer.setNamesrvAddr(nameSvr);
      try {
        this.producer.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("init simple producer ok!!!");
    }

    protected DefaultMQProducer fetchSimpleProducer() {
      if (Objects.isNull(this.producer)) {
        throw new NullPointerException("producer not allowed null");
      }
      return this.producer;
    }

    protected SimpleSenderWrapper fetchSendWrapper() {
      if (Objects.isNull(this.simpleSenderWrapper)) {
        throw new NullPointerException("simpleSenderWrapper not allowed null");
      }
      return this.simpleSenderWrapper;
    }

    private DefaultMQProducer producer;

    private SimpleSenderWrapper simpleSenderWrapper;
  }


  private SimpleSenderWrapper() {

  }

}
