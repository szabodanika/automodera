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

package uk.ac.uws.danielszabo.common.model.nodes;

import lombok.*;
import uk.ac.uws.danielszabo.common.model.images.Topic;
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
public class NodeCertificate {

  // unique identifier of certificate
  @Id @XmlID @XmlAttribute @NonNull private String id;

  // id of the issuer operator node
  @ManyToOne @XmlIDREF @NonNull private Node issuer;

  // issue and expiration date
  @NonNull
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  private Date issued, expiration;

  // node it was issued to
  @OneToOne @XmlIDREF @NonNull private Node node;

  // address where this certificate is valid
  @NonNull private String address;

  // topics in which the certified node can operate
  @ManyToMany
  @XmlIDREF
  @XmlElementWrapper(name = "topicList")
  @XmlElement(name = "topic")
  @ToString.Exclude
  @NonNull
  private List<Topic> topicList;

  // rights given to this node by certificate issuer
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "networkRights", joinColumns = @JoinColumn(name = "id"))
  @Enumerated(EnumType.STRING)
  @XmlElementWrapper(name = "networkRightList")
  @XmlElement(name = "networkRight")
  @NonNull
  private List<NetworkRights> networkRights;
}
