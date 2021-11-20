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

package uk.ac.uws.danielszabo.common.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SQLDateAdapter extends XmlAdapter<String, Date> {

  private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

  @Override
  public Date unmarshal(String xml) throws Exception {
    return (Date) dateFormat.parse(xml);
  }

  @Override
  public String marshal(Date object) {
    return dateFormat.format(object);
  }
}
