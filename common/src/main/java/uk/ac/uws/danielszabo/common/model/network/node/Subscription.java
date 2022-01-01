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

package uk.ac.uws.danielszabo.common.model.network.node;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import uk.ac.uws.danielszabo.common.model.hash.Topic;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Subscription {


  // XML transient because it is just a local db id
  // and it means nothing
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @XmlTransient
  private Long id;

  // one subscription object for each topic even between the same publisher and subscriber nodes
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "topic_id")
  @NonNull
  private Topic topic;

  @JoinColumn(name = "publisher_id", insertable = false, updatable = false)
  @ManyToOne(targetEntity = Node.class, fetch = FetchType.EAGER)
  @XmlTransient
  @NotFound(action = NotFoundAction.IGNORE)
//  @Transient
  private Node publisher;

  @Column(name = "publisher_id")
  @NonNull
  private String publisherId;

  @JoinColumn(name = "subscriber_id", insertable = false, updatable = false)
  @ManyToOne(targetEntity = Node.class, fetch = FetchType.EAGER)
  @XmlTransient
  @NotFound(action = NotFoundAction.IGNORE)
//  @Transient
  private Node subscriber;

  @Column(name = "subscriber_id")
  @NonNull
  private String subscriberId;


}
