package com.omisoft.micro.common.structures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.omisoft.micro.common.serializers.SecureStringDeserializer;
import com.omisoft.micro.common.serializers.SecureStringSerializer;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Secure string implementation TODO Hash string so that even if dumping memory during operation on
 * SecureString we are protected Created by dido on 12/7/16.
 */
@JsonDeserialize(using = SecureStringDeserializer.class)
@JsonSerialize(using = SecureStringSerializer.class)
public class SecureString implements CharSequence, Serializable {

  public static final SecureString EMPTY_STRING =
      new SecureString(new char[]{Character.MIN_VALUE});
  private final char[] chars;

  public SecureString(char[] chars) {
    this.chars = new char[chars.length];
    System.arraycopy(chars, 0, this.chars, 0, chars.length);

  }

  /**
   * This constructor is insecure
   */
  public SecureString(String string) {
    this(string.toCharArray());
  }

  /**
   * TODO Think of a secure way to do this
   */
  public SecureString(byte[] bytes) {

    this(new String(bytes, StandardCharsets.UTF_8).toCharArray());


  }

  public SecureString(char[] chars, int start, int end) {
    this.chars = new char[end - start];
    System.arraycopy(chars, start, this.chars, 0, this.chars.length);
  }

  @Override
  public int length() {
    return chars.length;
  }

  @Override
  public char charAt(int index) {
    return chars[index];
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return new SecureString(this.chars, start, end);
  }

  /**
   * MUST manually clear the underlying array holding the characters
   */
  public void clear() {
    Arrays.fill(chars, '0');
  }

  @Override
  protected void finalize() throws Throwable {
    clear();
    super.finalize();
  }

  /**
   * Returns copy of char array (use only in secure objects)
   */
  public char[] toCharArray() {
    return Arrays.copyOf(this.chars, this.length());
  }

  public char codeAt(int i) {
    return chars[i];
  }

  public SecureString concat(SecureString strToAdd) {
    int thisLength = this.length();
    int thatLength = strToAdd.length();
    char[] result = new char[thisLength + thatLength];
    System.arraycopy(this.chars, 0, result, 0, thisLength);
    System.arraycopy(strToAdd.chars, 0, result, thisLength, thatLength);
    return new SecureString(result);
  }

  /**
   * Convert secure string to byte array. Secure string is utf-8
   */
  public byte[] toBytes() {
    CharBuffer charBuffer = CharBuffer.wrap(chars);
    ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
    byte[] bytes =
        Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
    return bytes;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SecureString that = (SecureString) o;

    return Arrays.equals(chars, that.chars);

  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(chars);
  }

  @Override
  public String toString() {
    return new String(chars);
  }
}