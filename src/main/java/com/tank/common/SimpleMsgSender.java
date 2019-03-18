package com.tank.common;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author fuchun
 */
@FunctionalInterface
public interface SimpleMsgSender<T> {

  /**
   * @param msg
   * @param simpleProducer
   * @return
   */
  SendResult sendMsg(final T msg, final DefaultMQProducer simpleProducer) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;
}
