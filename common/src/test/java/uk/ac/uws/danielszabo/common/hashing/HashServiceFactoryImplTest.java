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
