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

package uk.ac.uws.danielszabo.common.service.network;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.network.cert.NetworkRight;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;

import java.sql.Date;
import java.util.ArrayList;

@Component
public class NodeFactoryImpl implements NodeFactory {

  @Override
  public Node getOriginNode(
      String id,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country) {
    Node node =
        new Node(
            id,
            name,
            NodeType.ORIGIN,
            domain,
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());
    NodeCertificate certificate =
        new NodeCertificate(
            id,
            node,
            domain,
            new ArrayList<>(),
            legalName,
            adminEmail,
            addressLine1,
            addressLine2,
            postCode,
            country);
    // leftover certificate properties
    certificate.setIssued(new Date(new java.util.Date().getTime()));
    // origin node's certification never expires
    certificate.setExpiration(null);
    for (NetworkRight r : NetworkRight.values()) certificate.getNetworkRights().add(r);
    // save cert in the node
    node.setCertificate(certificate);
    return node;
  }

  @Override
  public Node getOperatorNode(
      String id,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country) {
    Node node =
        new Node(
            id,
            name,
            NodeType.OPERATOR,
            domain,
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());
    NodeCertificate certificate =
        new NodeCertificate(
            id,
            node,
            domain,
            new ArrayList<>(),
            legalName,
            adminEmail,
            addressLine1,
            addressLine2,
            postCode,
            country);

    // all basic network rights
    certificate.getNetworkRights().add(NetworkRight.CHECK_CERTIFICATE);
    certificate.getNetworkRights().add(NetworkRight.ISSUE_CERTIFICATE);
    certificate.getNetworkRights().add(NetworkRight.VERIFY_CERTIFICATE);
    certificate.getNetworkRights().add(NetworkRight.SUBSCRIBE);
    certificate.getNetworkRights().add(NetworkRight.PUBLISH);

    // save cert in the node
    node.setCertificate(certificate);
    return node;
  }

  @Override
  public Node getArchiveNode(
      String id,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country) {
    Node node =
        new Node(
            id,
            name,
            NodeType.ARCHIVE,
            domain,
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());
    NodeCertificate certificate =
        new NodeCertificate(
            id,
            node,
            domain,
            new ArrayList<>(),
            legalName,
            adminEmail,
            addressLine1,
            addressLine2,
            postCode,
            country);
    certificate.getNetworkRights().add(NetworkRight.CHECK_CERTIFICATE);
    certificate.getNetworkRights().add(NetworkRight.PUBLISH);

    // save cert in the node
    node.setCertificate(certificate);
    return node;
  }

  @Override
  public Node getIntegratorNode(
      String id,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country) {
    Node node =
        new Node(
            id,
            name,
            NodeType.INTEGRATOR,
            domain,
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());
    NodeCertificate certificate =
        new NodeCertificate(
            id,
            node,
            domain,
            new ArrayList<>(),
            legalName,
            adminEmail,
            addressLine1,
            addressLine2,
            postCode,
            country);
    certificate.getNetworkRights().add(NetworkRight.CHECK_CERTIFICATE);
    certificate.getNetworkRights().add(NetworkRight.SUBSCRIBE);

    // save cert in the node
    node.setCertificate(certificate);
    return node;
  }
}
