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

package uk.ac.uws.danielszabo.automodera.common.model.collection;

import lombok.*;
import uk.ac.uws.danielszabo.automodera.common.util.SQLDateAdapter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.sql.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Image {
  // unique identifier of this image
  // consists of archive id and local incremental count
  // eg. 44-123
  // where 44 is the archive id and 123 is the number of this image in this archive
  @Id @XmlID @XmlAttribute @NonNull private String id;

  // file on the archive's local system
  @XmlTransient @NonNull private File file;

  // date this image was added to collection
  @NonNull
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  private Date date;

  // generated hash for this image
  @NonNull private String hash;

  @ManyToOne @NonNull @XmlTransient private Collection collection;
}
