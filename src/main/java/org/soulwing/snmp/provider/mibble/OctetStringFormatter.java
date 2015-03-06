/*
 * File created on Jan 24, 2013 
 *
 * Copyright (c) 2013 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.snmp.provider.mibble;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import org.soulwing.snmp.Formatter;

class OctetStringFormatter implements Formatter {

  private final String hint;
  
  public OctetStringFormatter(String hint) {
    this.hint = hint;
  }

  public String format(Object value) {
    final StringBuilder sb = new StringBuilder();
    final HintIterator it = new HintIterator(hint);
    int offset = 0;
    while (offset < ((byte[]) value).length) {
      ByteArrayFormatter formatter = it.next();
      offset += formatter.format((byte[]) value, offset, sb); 
    }
    return sb.toString();
  }

  private static class HintIterator {
  
    private final String hint;
    private int hintOffset;
    private int lastHintOffset;
    
    
    public HintIterator(String hint) {
      this.hint = hint;
    }

    public ByteArrayFormatter next() {
      if (hintOffset >= hint.length()) {
        hintOffset = lastHintOffset;
      }
      lastHintOffset = hintOffset;

      assertNotEndOfSpecifier();
      ByteArrayFormatter formatter = new ByteArrayFormatter();
      
      formatter.setRepeatIndicator(hint.charAt(hintOffset) == '*');
      if (formatter.isRepeatIndicator()) {
        hintOffset++;
      }
      
      int length = 0;
      while (hintOffset < hint.length()
          && Character.isDigit(hint.charAt(hintOffset))) {
        length = 10 * length + (hint.charAt(hintOffset++) - '0');
      }
      formatter.setLength(length);
      
      assertNotEndOfSpecifier();
      formatter.setFormat(hint.charAt(hintOffset++));
      
      if (hintOffset < hint.length()) {
        char s = hint.charAt(hintOffset);
        if (s != '*' && (s < '0' || s > '9')) {
          formatter.setSeparator(s);
          hintOffset++;
        }
        if (hintOffset < hint.length() && formatter.isRepeatIndicator()) {
          char t = hint.charAt(hintOffset);
          if (t != '*' && (t < '0' || t > '9')) {
            formatter.setTerminator(t);
            hintOffset++;
          }
        }
      }

      return formatter;
    }
    
    private void assertNotEndOfSpecifier() {
      if (hintOffset >= hint.length()) {
        throw new IllegalArgumentException("invalid octet format specifier");
      }
    }

  }
  
  private static class ByteArrayFormatter {
    
    private int length;
    private char format; 
    private boolean repeatIndicator;
    private int repeatCount = 1;
    private int separator = -1;
    private int terminator = -1;

    public int format(byte[] octets, int offset, StringBuilder sb) {
      if (isRepeatIndicator()) {
        repeatCount = (int) (octets[offset++] & 0xff);
      }
      int startingOffset = offset;
      for (int i = 0; i < repeatCount && offset < octets.length; i++) {
        int count = Math.min(octets.length - offset, length);
        formatOctets(Character.toLowerCase(format), octets, offset, count, 
            sb);
        offset += count;
        if (offset < octets.length) {
          if (separator != -1 && (terminator == -1 || i < repeatCount - 1)) {
            sb.append((char) separator);
          }
          else if (terminator != -1 && i == repeatCount - 1) {
            sb.append((char) terminator);
          }
        }
      }
      return offset - startingOffset;
    }
    
    private void formatOctets(char format, byte[] octets, int offset, int count,
        StringBuilder sb) {
      try {
        if (format == 'a') {
          sb.append(new String(octets, offset, count, "US-ASCII"));
        }
        else if (format == 't') {
          sb.append(new String(octets, offset, count, "UTF-8"));
        }
        else {
          BigInteger value = BigInteger.ZERO;
          for (int i = 0; i < count; i++) {
            value = value.shiftLeft(8).or(
                    BigInteger.valueOf(octets[offset + i] & 0xff));
          }
          sb.append(value.toString(RadixUtil.radixForFormat(format)));
        }
      }
      catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
    }

    public void setLength(int length) {
      this.length = length;
    }

    public void setFormat(char format) {
      this.format = format;
    }

    public boolean isRepeatIndicator() {
      return repeatIndicator;
    }

    public void setRepeatIndicator(boolean hasRepeatIndicator) {
      this.repeatIndicator = hasRepeatIndicator;
    }

    public void setSeparator(int separator) {
      this.separator = separator;
    }

    public void setTerminator(int terminator) {
      this.terminator = terminator;
    }

  }


}
