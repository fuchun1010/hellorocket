package com.tank.common;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;

/**
 * @author
 */
public interface TransactionMsgSender<T> {

  /**
   * @param message
   * @param producer
   * @return
   */
  SendResult sendMessage(final T message, final TransactionMQProducer producer);
}
