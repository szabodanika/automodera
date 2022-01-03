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

package uk.ac.uws.danielszabo.common.util;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.Assert.assertThrows;

public class SQLDateAdapterTest extends TestCase {

  private SQLDateAdapter testSQLDateAdapter = new SQLDateAdapter();

  @Test
  public void testUnmarshal() {
    String marshalledSQLDate = testSQLDateAdapter.marshal(new Date(1641067954546L));
    assertEquals("1641067954546", marshalledSQLDate);
  }

  @Test
  public void testMarshal() throws Exception {
    Date unmarshalledSQLDate = testSQLDateAdapter.unmarshal("1641067954546");
    assertEquals(new Date(1641067954546L), unmarshalledSQLDate);

    assertThrows(
        NumberFormatException.class,
        () -> {
          testSQLDateAdapter.unmarshal("NaN");
        });
  }
}
