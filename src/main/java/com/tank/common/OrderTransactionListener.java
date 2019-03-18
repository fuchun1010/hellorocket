package com.tank.common;

import com.alibaba.fastjson.JSON;
import com.tank.domain.Order;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;

/**
 * @author fuchun
 */
public class OrderTransactionListener implements TransactionListener {

  @Override
  public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    System.out.println("executeLocalTransaction");
    String jsonStr = null;
    try {
      jsonStr = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    Order order = JSON.parseObject(jsonStr, Order.class);

    final boolean isSuccess = this.addOrder(order);

    if (!isSuccess) {
      return LocalTransactionState.ROLLBACK_MESSAGE;
    }

    return LocalTransactionState.COMMIT_MESSAGE;
  }

  @Override
  public LocalTransactionState checkLocalTransaction(MessageExt msg) {
    System.out.println("checkLocalTransaction =  not execute in real project");
    return LocalTransactionState.COMMIT_MESSAGE;
  }

  private boolean addOrder(final Order order) {
    System.out.println("insert order = [" + order.toString() + "]");
    return true;
  }
}
