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

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class NodeCertificate implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    // unique identifier of certificate
    // can be null because it is assigned by the issuer
    @Id
    @XmlID
    @XmlAttribute
    private String id;

    // id of the issuer operator node
    @ManyToOne
    @ToString.Exclude
    private Node issuer;

    // node it was issued to
    @OneToOne(mappedBy = "certificate")
    @XmlIDREF
    @ToString.Exclude
    @NonNull
    private Node node;

    // issue and expiration date
    // null before signing
    @XmlJavaTypeAdapter(SQLDateAdapter.class)
    private Date issued, expiration;

    // domain where this certificate is valid
    // eg. hashnet.publicarchive.org
    @NonNull
    private String host;


    // additional info about the node
    @NonNull
    private String legalName, adminEmail, addressLine1, addressLine2, postCode, country;

    // rights given to this node by certificate issuer
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "networkRights", joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @XmlElementWrapper(name = "networkRightList")
    @XmlElement(name = "networkRight")
    @NonNull
    private List<NetworkRight> networkRights;


    public NodeCertificate(String id, @NonNull Node node, @NonNull String host, @NonNull List<NetworkRight> networkRights, @NonNull String legalName, @NonNull String adminEmail, @NonNull String addressLine1, String addressLine2, @NonNull String postCode, @NonNull String country) {
        this.id = id;
        this.node = node;
        this.host = host;
        this.legalName = legalName;
        this.adminEmail = adminEmail;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postCode = postCode;
        this.country = country;
        this.networkRights = networkRights;
    }


    // Auto-generated by IntelliJ

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeCertificate that = (NodeCertificate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!issuer.equals(that.issuer)) return false;
        if (!node.equals(that.node)) return false;
        if (issued != null ? !issued.equals(that.issued) : that.issued != null) return false;
        if (expiration != null ? !expiration.equals(that.expiration) : that.expiration != null) return false;
        if (!host.equals(that.host)) return false;
        if (!legalName.equals(that.legalName)) return false;
        if (!adminEmail.equals(that.adminEmail)) return false;
        if (!addressLine1.equals(that.addressLine1)) return false;
        if (!addressLine2.equals(that.addressLine2)) return false;
        if (!postCode.equals(that.postCode)) return false;
        if (!country.equals(that.country)) return false;
        return networkRights.equals(that.networkRights);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + issuer.hashCode();
        result = 31 * result + node.hashCode();
        result = 31 * result + (issued != null ? issued.hashCode() : 0);
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        result = 31 * result + host.hashCode();
        result = 31 * result + legalName.hashCode();
        result = 31 * result + adminEmail.hashCode();
        result = 31 * result + addressLine1.hashCode();
        result = 31 * result + addressLine2.hashCode();
        result = 31 * result + postCode.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + networkRights.hashCode();
        return result;
    }
}
