package com.imooc.oa.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class MD5UtilsTest {

  @Test
  public void md5Digest() {
    String hello = MD5Utils.md5Digest("hello");
    System.out.println(hello);
  }
}
