package com.tank.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class Order {

  private String id;

  private double totalPrice;

  private String targetAddress;

  private String receiver;

  private String mobile;

  private String sender;

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id = ");
    sb.append(id);
    sb.append(",receiver = ");
    sb.append(receiver);
    sb.append(", mobile = ");
    sb.append(mobile);
    sb.append(", targetAddress = ");
    sb.append(targetAddress);
    sb.append(", sender = ");
    sb.append(sender);
    return sb.toString();
  }
}
