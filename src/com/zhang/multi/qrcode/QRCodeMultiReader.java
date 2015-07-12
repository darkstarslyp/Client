/*
 * Copyright 2009 ZXing authors
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

package com.zhang.multi.qrcode;

import com.zhang.client.BarcodeFormat;
import com.zhang.client.BinaryBitmap;
import com.zhang.client.DecodeHintType;
import com.zhang.client.NotFoundException;
import com.zhang.client.ReaderException;
import com.zhang.client.Result;
import com.zhang.client.ResultMetadataType;
import com.zhang.client.ResultPoint;
import com.zhang.common.DecoderResult;
import com.zhang.common.DetectorResult;
import com.zhang.multi.MultipleBarcodeReader;
import com.zhang.multi.qrcode.detector.MultiDetector;
import com.zhang.qrcode.QRCodeReader;
import com.zhang.qrcode.decoder.QRCodeDecoderMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This implementation can detect and decode multiple QR Codes in an image.
 *
 * @author Sean Owen
 * @author Hannes Erven
 */
public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {

  private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];

  @Override
  public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
    return decodeMultiple(image, null);
  }

  @Override
  public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType,?> hints) throws NotFoundException {
    List<Result> results = new ArrayList<Result>();
    DetectorResult[] detectorResults = new MultiDetector(image.getBlackMatrix()).detectMulti(hints);
    for (DetectorResult detectorResult : detectorResults) {
      try {
        DecoderResult decoderResult = getDecoder().decode(detectorResult.getBits(), hints);
        ResultPoint[] points = detectorResult.getPoints();
        // If the code was mirrored: swap the bottom-left and the top-right points.
        if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
          ((QRCodeDecoderMetaData) decoderResult.getOther()).applyMirroredCorrection(points);
        }
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points,
                                   BarcodeFormat.QR_CODE);
        List<byte[]> byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
          result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
          result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        results.add(result);
      } catch (ReaderException re) {
        // ignore and continue 
      }
    }
    if (results.isEmpty()) {
      return EMPTY_RESULT_ARRAY;
    } else {
      return results.toArray(new Result[results.size()]);
    }
  }

}
