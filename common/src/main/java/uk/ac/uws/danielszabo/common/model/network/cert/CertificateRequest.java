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

package uk.ac.uws.danielszabo.common.model.network.cert;

import lombok.*;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.util.SQLDateAdapter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.Random;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@XmlSeeAlso({
  Date.class,
  Node.class,
})
public class CertificateRequest implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public enum Status {
    WAITING,
    ISSUED,
    REJECTED
  }

  // should be same as the certificate id
  @Id @NonNull String id;

  // local node data and incomplete certificate request to sign
  @OneToOne(cascade = CascadeType.ALL)
  @NonNull
  private Node node;

  // date the request was created
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  @NonNull
  private Date date;

  @NonNull private Status status = Status.WAITING;

  private String message;

  public CertificateRequest(String id, Node localNode) {
    this.id = id;
    this.node = localNode;
  }

}
