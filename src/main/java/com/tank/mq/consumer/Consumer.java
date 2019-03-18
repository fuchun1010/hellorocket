package com.tank.mq.consumer;

import com.tank.common.ConfigLoader;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class Consumer {

  public static void main(String[] args) {
    System.out.println("...transaction Consumer...");
    String topic = ConfigLoader.<String>valueByKey("rocketmqServer.transTp", (k, c) -> c.getString(k));
    String nameSvr = ConfigLoader.valueByKey("rocketmqServer.nameSvrAddr", (k, c) -> c.getString(k));
    String consumerGroup = ConfigLoader.valueByKey("rocketmqServer.cusomerGroup", (k, c) -> c.getString(k));

    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);

    consumer.setNamesrvAddr(nameSvr);
    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

    // Subscribe one more more topics to consume.
    try {
      consumer.subscribe(topic, "*");
      consumer.registerMessageListener(new MessageListenerConcurrently() {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                        ConsumeConcurrentlyContext context) {
          System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
      });

      //Launch the consumer instance.
      consumer.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Register callback to execute on arrival of messages fetched from brokers.


    System.out.printf("Consumer Started.%n");
  }
}
