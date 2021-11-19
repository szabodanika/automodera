package uk.ac.uws.danielszabo.common.hashing;

import dev.brachtendorf.jimagehash.hash.Hash;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class HashServiceImplTest {

  private static HashService testObject;

  private static File testImageCow, testImageScooter, testImageCoin1, testImageCoin2;

  @BeforeAll
  public static void init() {
    testObject = new HashServiceFactoryImpl().getHashService();

    assertNotNull(testObject);

    testImageCow = Paths.get("src", "test", "resources", "cow.jpeg").toFile();
    testImageScooter = Paths.get("src", "test", "resources", "scooter.jpeg").toFile();
    testImageCoin1 = Paths.get("src", "test", "resources", "coin1.jpeg").toFile();
    testImageCoin2 = Paths.get("src", "test", "resources", "coin2.jpeg").toFile();

    assertNotNull(testImageCow);
    assertNotNull(testImageScooter);
    assertNotNull(testImageCoin1);
    assertNotNull(testImageCoin2);
  }

  @Test
  public void testPHash() {
    assertDoesNotThrow(
        () -> {
          Hash hash1 = testObject.pHash(testImageCow);
          // test that the service returns a hash
          assertNotNull(hash1);

          Hash hash2 = testObject.pHash(testImageScooter);
          // test that the service returns different hash objects for different images
          assertNotEquals(hash1, hash2);

          // test that two different calls on the same image returns same hash
          Hash hash3 = testObject.pHash(testImageCow);
          assertEquals(hash1, hash3);
        });

    // test that the service throws IOException for non-existent image
    assertThrows(
        IOException.class,
        () -> {
          Hash hash1 = testObject.pHash(new File("doesnotexist.jpeg"));
        });
  }

  @Test
  public void testSimScore() {
    assertDoesNotThrow(
        () -> {
          // prepare hashes for 2 different images
          Hash hashCow = testObject.pHash(testImageCow);
          Hash hashScooter = testObject.pHash(testImageScooter);
          Hash hashCoin1 = testObject.pHash(testImageCoin1);
          Hash hashCoin2 = testObject.pHash(testImageCoin2);
          assertNotNull(hashCow);
          assertNotNull(hashScooter);
          assertNotNull(hashCoin1);
          assertNotNull(hashCoin2);

          // test that for 2 very different images the similarity score is below 0.5
          assertTrue(testObject.simScore(hashCow, hashScooter) < .5);

          // test that for 2 slightly different images the similarity score is above 0.8
          assertTrue(testObject.simScore(hashCoin1, hashCoin2) > .8);

          // test that for identical hashes the similarity score is 1
          assertEquals(1, testObject.simScore(hashCow, hashCow));
        });
  }
}
