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
import uk.ac.uws.danielszabo.common.model.images.HashCollection;
import uk.ac.uws.danielszabo.common.util.SQLDateAdapter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class Node {

    // unique identifier in the network
    @Id
    @XmlID
    @XmlAttribute
    @NonNull
    private String id;

    // display name
    @NonNull
    private String name;

    // type of node (INTEGRATOR,ARCHIVE,OPERATOR)
    @NonNull
    private NodeType nodeType;

    // URL that other nodes will communicate to
    // (eg. offensive-images-archive.org)
    @NonNull
    private String address;

    // certificate provided by an operator node
    // it will be sent with all the messages
    // and checked by other nodes through the operator
    // contains id and address
    @OneToOne
    @NonNull
    private NodeCertificate certificate;

    @NonNull
    @XmlJavaTypeAdapter(SQLDateAdapter.class)
    private Date createdAt;

    // certifications issued by this node
    // should only contain values if this is an operator node
    @OneToMany(mappedBy = "issuer")
    @XmlIDREF
    @XmlElementWrapper(name = "issuedCertificateList")
    @XmlElement(name="issuedCertificate")
    @ToString.Exclude
    @NonNull
    private List<NodeCertificate> issuedCertificates;

    // hash collections published by this node
    @OneToMany
    @XmlIDREF
    @XmlElementWrapper(name = "hashCollectionList")
    @XmlElement(name="hashCollection")
    @ToString.Exclude
    @NonNull
    private List<HashCollection> hashCollectionList;
}
