/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhang.client.result;

import com.zhang.client.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public final class SMSMMSResultParser extends ResultParser {

  @Override
  public SMSParsedResult parse(Result result) {
    String rawText = getMassagedText(result);
    if (!(rawText.startsWith("sms:") || rawText.startsWith("SMS:") ||
          rawText.startsWith("mms:") || rawText.startsWith("MMS:"))) {
      return null;
    }

    // Check up front if this is a URI syntax string with query arguments
    Map<String,String> nameValuePairs = parseNameValuePairs(rawText);
    String subject = null;
    String body = null;
    boolean querySyntax = false;
    if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
      subject = nameValuePairs.get("subject");
      body = nameValuePairs.get("body");
      querySyntax = true;
    }

    // Drop sms, query portion
    int queryStart = rawText.indexOf('?', 4);
    String smsURIWithoutQuery;
    // If it's not query syntax, the question mark is part of the subject or message
    if (queryStart < 0 || !querySyntax) {
      smsURIWithoutQuery = rawText.substring(4);
    } else {
      smsURIWithoutQuery = rawText.substring(4, queryStart);
    }

    int lastComma = -1;
    int comma;
    List<String> numbers = new ArrayList<String>(1);
    List<String> vias = new ArrayList<String>(1);
    while ((comma = smsURIWithoutQuery.indexOf(',', lastComma + 1)) > lastComma) {
      String numberPart = smsURIWithoutQuery.substring(lastComma + 1, comma);
      addNumberVia(numbers, vias, numberPart);
      lastComma = comma;
    }
    addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));    

    return new SMSParsedResult(numbers.toArray(new String[numbers.size()]),
                               vias.toArray(new String[vias.size()]),
                               subject,
                               body);
  }

  private static void addNumberVia(Collection<String> numbers,
                                   Collection<String> vias,
                                   String numberPart) {
    int numberEnd = numberPart.indexOf(';');
    if (numberEnd < 0) {
      numbers.add(numberPart);
      vias.add(null);
    } else {
      numbers.add(numberPart.substring(0, numberEnd));
      String maybeVia = numberPart.substring(numberEnd + 1);
      String via;
      if (maybeVia.startsWith("via=")) {
        via = maybeVia.substring(4);
      } else {
        via = null;
      }
      vias.add(via);
    }
  }

}