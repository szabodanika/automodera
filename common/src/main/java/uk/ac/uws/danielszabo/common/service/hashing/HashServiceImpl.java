/*
 * Copyright (c) Daniel Szabo 2021.
 *
 * GitHub: https://github.com/szabodanika
 * Email: daniel.szabo99@outlook.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.uws.danielszabo.common.service.hashing;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class HashServiceImpl implements HashService {

  private final int bitResolution = 32;

  private final HashingAlgorithm hasher;

  public HashServiceImpl() {
    this.hasher = new PerceptiveHash(bitResolution);
  }

  @Override
  public Hash pHash(File image) throws IOException {
    log.debug("Hashing " + image.getName());
    return hasher.hash(image);
  }

  @Override
  public double simScore(Hash hash1, Hash hash2) {
    log.debug("Calculating similarity score for  " + hash1 + " and " + hash2);
    // turning hamming distance, where 0 is identical and 1 is completely different into
    // similarity score where 1 is identical and 0 is completely differnt
    return 1 - hash1.normalizedHammingDistance(hash2);
  }
}
