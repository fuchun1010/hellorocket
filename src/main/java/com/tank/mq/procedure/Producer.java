package com.tank.mq.procedure;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author fuchun
 */
public class Producer {

  public static void main(String[] args) {
    String[] tags = new String[]{"TagA", "TagB"};
    DefaultMQProducer producer = new DefaultMQProducer("helloProducer");
    //Launch the instance.
    producer.setNamesrvAddr("localhost:9876");
    //producer.setVipChannelEnabled(false);
    try {
      producer.start();
      for (int i = 0; i < 10; i++) {
        //Create a message instance, specifying topic, tag and message body.
        Message msg = new Message("orderTopic",
            ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );
        //Call send message to deliver message to one of brokers.
        SendResult sendResult = producer.send(msg);
        System.out.printf("%s%n", sendResult);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Shut down once the producer instance is not longer in use.
    producer.shutdown();


  }


}
