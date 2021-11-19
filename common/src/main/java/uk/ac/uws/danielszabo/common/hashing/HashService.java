package uk.ac.uws.danielszabo.common.hashing;

import dev.brachtendorf.jimagehash.hash.Hash;

import java.io.File;
import java.io.IOException;

public interface HashService {

  Hash pHash(File image) throws IOException;

  double simScore(Hash hash1, Hash hash2);
}
