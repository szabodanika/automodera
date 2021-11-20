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
          // test that the uk.ac.uws.danielszabo.hashnet.operator.service returns a hash
          assertNotNull(hash1);

          Hash hash2 = testObject.pHash(testImageScooter);
          // test that the uk.ac.uws.danielszabo.hashnet.operator.service returns different hash objects for different images
          assertNotEquals(hash1, hash2);

          // test that two different calls on the same image returns same hash
          Hash hash3 = testObject.pHash(testImageCow);
          assertEquals(hash1, hash3);
        });

    // test that the uk.ac.uws.danielszabo.hashnet.operator.service throws IOException for non-existent image
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
