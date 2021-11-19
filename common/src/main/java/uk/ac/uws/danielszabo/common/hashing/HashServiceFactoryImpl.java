package uk.ac.uws.danielszabo.common.hashing;

import java.util.Objects;

public class HashServiceFactoryImpl implements HashServiceFactory {

  private static final int BIT_RESOLUTION = 32;

  private HashService hashService;

  @Override
  public HashService getHashService() {
    return Objects.requireNonNullElseGet(
        hashService, () -> this.hashService = new HashServiceImpl(BIT_RESOLUTION));
  }
}
