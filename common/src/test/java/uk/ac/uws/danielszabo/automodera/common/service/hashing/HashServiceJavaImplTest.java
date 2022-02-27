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

import static org.junit.jupiter.api.Assertions.*;

public class HashServiceJavaImplTest {

  //  private static HashService testHashService;
  //
  //  private static File testImageCow, testImageScooter, testImageCoin1, testImageCoin2;
  //
  //  @BeforeAll
  //  public static void init() {
  //    testHashService = new HashServiceImpl();
  //
  //    assertNotNull(testHashService);
  //
  //    testImageCow = Paths.get("src", "test", "resources", "cow.jpeg").toFile();
  //    testImageScooter = Paths.get("src", "test", "resources", "scooter.jpeg").toFile();
  //    testImageCoin1 = Paths.get("src", "test", "resources", "coin1.jpeg").toFile();
  //    testImageCoin2 = Paths.get("src", "test", "resources", "coin2.jpeg").toFile();
  //
  //    assertNotNull(testImageCow);
  //    assertNotNull(testImageScooter);
  //    assertNotNull(testImageCoin1);
  //    assertNotNull(testImageCoin2);
  //  }
  //
  //  @Test
  //  public void testPHash() {
  //    assertDoesNotThrow(
  //        () -> {
  //          String hash1 = testHashService.pHash(testImageCow);
  //          // test that the uk.ac.uws.danielszabo.hashnet.operator.service returns a hash
  //          assertNotNull(hash1);
  //
  //          String hash2 = testHashService.pHash(testImageScooter);
  //          // test that the uk.ac.uws.danielszabo.hashnet.operator.service returns different hash
  //          // objects for different images
  //          assertNotEquals(hash1, hash2);
  //
  //          // test that two different calls on the same image returns same hash
  //          String hash3 = testHashService.pHash(testImageCow);
  //          assertEquals(hash1, hash3);
  //        });
  //
  //    // test that the uk.ac.uws.danielszabo.hashnet.operator.service throws IOException for
  //    // non-existent image
  //    assertThrows(
  //        IOException.class,
  //        () -> {
  //          String hash1 = testHashService.pHash(new File("doesnotexist.jpeg"));
  //        });
  //  }
  //
  //  @Test
  //  public void testSimScore() {
  //    assertDoesNotThrow(
  //        () -> {
  //          // prepare hashes for 2 different images
  //          String hashCow = testHashService.pHash(testImageCow);
  //          String hashScooter = testHashService.pHash(testImageScooter);
  //          String hashCoin1 = testHashService.pHash(testImageCoin1);
  //          String hashCoin2 = testHashService.pHash(testImageCoin2);
  //          assertNotNull(hashCow);
  //          assertNotNull(hashScooter);
  //          assertNotNull(hashCoin1);
  //          assertNotNull(hashCoin2);
  //
  //          // test that for 2 very different images the similarity score is below 0.5
  //          assertTrue(testHashService.simScore(hashCow, hashScooter) < .5);
  //
  //          // test that for 2 slightly different images the similarity score is above 0.8
  //          assertTrue(testHashService.simScore(hashCoin1, hashCoin2) > .8);
  //
  //          // test that for identical hashes the similarity score is 1
  //          assertEquals(1, testHashService.simScore(hashCow, hashCow));
  //        });
  //  }
}
