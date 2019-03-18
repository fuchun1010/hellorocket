package com.tank.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public class ConfigLoaderTest {

  @Test
  public void testValueByKey() {
    String nameSvrAddr = ConfigLoader.<String>valueByKey("rocketmq-server.nameSvrAddr", (key, config) -> config.getString(key));
    String topic = ConfigLoader.valueByKey("rocketmq-server.topic", (key, config) -> config.getString(key));
    Assert.assertTrue(Objects.nonNull(nameSvrAddr));
    Assert.assertTrue(Objects.nonNull(topic));
  }
}