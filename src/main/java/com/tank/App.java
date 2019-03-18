package com.tank;


import com.alibaba.fastjson.JSON;
import com.tank.common.ConfigLoader;
import com.tank.common.OrderTransactionListener;
import com.tank.common.SimpleSenderWrapper;
import com.tank.common.TransactionSenderWrapper;
import com.tank.domain.Order;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fuchun
 */
public class App {
  public static void main(String[] args) throws Exception {


    // basic send
    //basicSend();

    //order send
    //orderSend();

    //transaction send
    sendTransaction();

  }

  private static void basicSend() {
    final SimpleSenderWrapper simpleSenderWrapper = SimpleSenderWrapper.createInstance();
    List<Message> messages = composeMessage();

    messages.forEach(msg -> {
      simpleSenderWrapper.<Message>sendMsg(msg, (Message data, DefaultMQProducer producer) -> {
        try {
          return producer.send(data);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      });
    });
    simpleSenderWrapper.closeSenderSync();
  }


  private static void orderSend() {

    final SimpleSenderWrapper wrapper = SimpleSenderWrapper.createInstance();
    List<Message> messages = composeMessage();

    wrapper.sendMsg(messages, (Message data, DefaultMQProducer producer) -> {

      SendResult sendResult = producer.send(data, (mqs, msg, arg) -> {
        int queues = mqs.size();
        String content = new String(msg.getBody());
        int hashCode = Math.abs(Objects.hashCode(content));
        int index = Math.floorMod(hashCode, queues);
        if (index < 0 || index > queues) {
          throw new ArrayIndexOutOfBoundsException("array index error");
        }
        return mqs.get(index);
      }, "1", TimeUnit.MILLISECONDS.toMillis(3000));

      return sendResult;
    });

    wrapper.closeSenderSync();
  }

  private static void sendTransaction() {
    final TransactionSenderWrapper wrapper = TransactionSenderWrapper.createInstance();
    composeOrders().forEach(order -> {
      wrapper.initTransactionListener(new OrderTransactionListener())
          .<Message>sendMsg(order, (Message data, TransactionMQProducer producer) -> {
            try {
              SendResult result = producer.sendMessageInTransaction(data, "1");
              System.out.printf("trans send: %s%n", result);
              return result;
            } catch (Exception e) {
              e.printStackTrace();
            }
            return null;
          });
    });
    wrapper.closeTransactionSenderSync();
  }

  private static List<Message> composeMessage() {
    final String topic = ConfigLoader.valueByKey("rocketmqServer.topic", (key, c) -> c.getString(key));

    List<Message> messages = IntStream.range(0, 5)
        .<String>mapToObj(String::valueOf)
        .<String>map("hello"::concat)
        .<Message>map(str -> {
          String tag = randomTag();
          Message message = new Message(topic, tag, str.getBytes());
          return message;
        }).collect(Collectors.toList());
    return messages;
  }

  private static List<Message> composeOrders() {
    Order orderA = new Order().setId("201900121231232")
        .setMobile("18623377391")
        .setReceiver("fuchun")
        .setSender("xujing")
        .setTargetAddress("BeiJin")
        .setTotalPrice(3000);

    final String topic = ConfigLoader.valueByKey("rocketmqServer.transTp", (key, c) -> c.getString(key));

    List<Message> messages = Collections.<Order>singleton(orderA)
        .stream().<String>map(JSON::toJSONString)
        .map(json -> {
          byte[] content = null;
          try {
            content = json.getBytes(RemotingHelper.DEFAULT_CHARSET);
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
          Message message = new Message(topic, "tagOrder", content);
          return message;
        }).collect(Collectors.toList());

    return messages;
  }

  private static String randomTag() {
    String[] tags = new String[]{"tagA", "tagB"};
    int index = ThreadLocalRandom.current().nextInt(tags.length);
    if (index < 0 || index > tags.length) {
      throw new ArrayIndexOutOfBoundsException("array index error");
    }
    return tags[index];
  }

}
