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

package uk.ac.uws.danielszabo.common.model.images;

import lombok.*;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.util.SQLDateAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class HashCollection {

  @Id @XmlID @NonNull private String id;

  @NonNull private String name;

  // simple counter for updates
  @NonNull private int version = 1;

  // date this collection was created
  @NonNull
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  private Date created;

  // date this collection was last updated:
  // - topics changed
  // - image list changed
  // - name changed
  @NonNull
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  private Date updated;

  // brief description of what this collection contains
  @NonNull private String description;

  // this can be used to enable/disable it on the integrators end by the archive
  @NonNull boolean enabled = true;

  @ManyToOne @NonNull private Node archive;

  @ManyToMany
  @JoinTable(
      name = "hashcollection_topic",
      joinColumns = @JoinColumn(name = "topic_id"),
      inverseJoinColumns = @JoinColumn(name = "hashcollection_id"))
  @XmlElementWrapper(name = "topicList")
  @XmlElement(name = "topic")
  @ToString.Exclude
  @NonNull
  //    @XmlTransient
  private List<Topic> topicList;

  // this will not be stored or transferred in XML
  // loaded from H2 database instead
  @OneToMany
  @ToString.Exclude
  //    @XmlElementWrapper(name = "imageList")
  //    @XmlElement(name = "image")
  @NonNull
  @XmlTransient
  private List<Image> imageList;
}
