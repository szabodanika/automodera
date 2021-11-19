package uk.ac.uws.danielszabo.common.hashing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HashServiceFactoryImplTest {

  private static HashServiceFactory testObject;

  @BeforeAll
  public static void init() {
    testObject = new HashServiceFactoryImpl();
  }

  @Test
  public void testGetHashService() {
    HashService hashService;

    hashService = testObject.getHashService();

    // test that we get a hashService on the first call
    assertNotNull(hashService);

    // test that we get the same hashService on the second call
    assertEquals(hashService, testObject.getHashService());
  }
}
