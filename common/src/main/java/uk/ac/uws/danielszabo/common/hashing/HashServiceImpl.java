package uk.ac.uws.danielszabo.common.hashing;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class HashServiceImpl implements HashService {

  private final int bitResolution;

  private static final Logger LOG = LoggerFactory.getLogger("HashServiceImpl");

  private final HashingAlgorithm hasher;

  public HashServiceImpl(int bitResolution) {
    LOG.info("Hash service created with bit resolution " + bitResolution);
    this.bitResolution = bitResolution;
    this.hasher = new PerceptiveHash(bitResolution);
  }

  public int getBitResolution() {
    return bitResolution;
  }

  @Override
  public Hash pHash(File image) throws IOException {
    LOG.info("Hashing " + image.getName());
    return hasher.hash(image);
  }

  @Override
  public double simScore(Hash hash1, Hash hash2) {
    LOG.info("Calculating similarity score for  " + hash1 + " and " + hash2);
    // turning hamming distance, where 0 is identical and 1 is completely different into
    // similarity score where 1 is identical and 0 is completely differnt
    return 1 - hash1.normalizedHammingDistance(hash2);
  }
}
