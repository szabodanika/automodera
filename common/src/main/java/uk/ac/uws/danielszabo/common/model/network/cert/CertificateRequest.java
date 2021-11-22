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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class CertificateRequest implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public enum Status {
    WAITING,
    ISSUED,
    REJECTED
  }

  // should be same as the certificate id
  @Getter @Id @NonNull String id;

  // local node data and incomplete certificate request to sign
  @Getter
  @OneToOne(cascade = CascadeType.ALL)
  @NonNull
  private Node node;

  // date the request was created
  @Getter
  @XmlJavaTypeAdapter(SQLDateAdapter.class)
  @NonNull
  private Date date = new Date(new java.util.Date().getTime());

  @Getter @Setter @NonNull private Status status = Status.WAITING;

  @Getter @Setter private String message;

  public CertificateRequest(Node localNode) {
    this.id = localNode.getId();
    this.node = localNode;
  }
}
