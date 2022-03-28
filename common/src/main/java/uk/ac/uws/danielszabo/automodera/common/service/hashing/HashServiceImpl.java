/*
 *
 *  Copyright (c) Daniel Szabo 2022.
 *
 *  GitHub: https://github.com/szabodanika
 *  Email: daniel.szabo99@outlook.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package uk.ac.uws.danielszabo.automodera.common.service.hashing;

import com.sun.jdi.NativeMethodException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
public class HashServiceImpl implements HashService {

  @Override
  public float getSimilarityToFileWithHash(String file_name, String reference_hash) {
    return RustPHash.getSimilarityToFileWithHash(
        file_name, new BigInteger(reference_hash, 16).toString(2));
  }

  @Override
  public String pHash(File image) throws IOException {
    log.debug("Hashing " + image.getName());
    try {
      return RustPHash.getHashForFile(image.getPath());
    } catch (NativeMethodException e) {
      throw new IOException(e);
    }
  }

  @Override
  public double simScore(String hash1, String hash2) {
    log.debug("Calculating similarity score for  " + hash1 + " and " + hash2);

    int sideLen = (int) Math.sqrt(hash1.length());

    double maxValue = Double.MIN_VALUE;

    StringBuilder rotated = new StringBuilder();

    for (int i = 0; i < 4; i++) {

      for (int l = 0; l < sideLen; l++) {
        for (int j = 0; j < sideLen; j++) {
          rotated.append(hash1.charAt(j * sideLen + l));
        }
      }

      double score = RustPHash.getSimilarityScore(hash2, rotated.toString());
      if (score > maxValue) {
        maxValue = score;
      }

      hash1 = rotated.toString();
      rotated = new StringBuilder();
    }

    return maxValue;
  }
}
