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

package uk.ac.uws.danielszabo.automodera.common.service.network;

import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeStatus;

import java.util.List;
import java.util.Optional;

public interface NetworkService {

  // Network Configuration

  NetworkConfiguration getNetworkConfiguration();

  NetworkConfiguration saveNetworkConfiguration(NetworkConfiguration networkConfiguration);

  Optional<CertificateRequest> findCertificateRequestById(String id);

  NetworkConfiguration fetchNetworkConfigurationAndSave(String origin)
      throws TargetNodeUnreachableException;

  // Nodes

  NodeStatus downloadNodeStatus(Node node) throws Exception;

  NodeStatus downloadNodeStatus(String address) throws Exception;

  List<Node> getAllNodes();

  Optional<Node> getKnownNodeById(String id);

  Node saveNode(Node node);

  void removeNode(Node node);

  Node getLocalNode();

  NetworkConfiguration fetchNetworkConfiguration(String originHost)
      throws TargetNodeUnreachableException;

  /**
   * this should return all the nodes in the database: - local node - operator nodes - nodes
   * subscribed to or publishing to
   *
   * @return list of all known nodes
   */
  List<Node> getAllKnownNodes();

  List<Node> getAllArchives();

  List<Node> getAllIntegrators();

  Node getNodeByHost(String host) throws TargetNodeUnreachableException;

  void fetchAllNodeStatus() throws Exception;

  // HashCollections, Topics, Images

  Collection downloadCollection(String host, String id) throws TargetNodeUnreachableException;

  void sendCollectionRepertoireToIntegrator(List<Collection> collectionList, String subscriberHost)
      throws TargetNodeUnreachableException;

  List<Collection> requestCollectionRepertoireFromArchive(Node node)
      throws TargetNodeUnreachableException;

  // Certificate Requests

  List<CertificateRequest> findAllCertificateRequests();

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  void sendProcessedCertificateRequest(CertificateRequest certificateRequest) throws Exception;

  // Certificates

  void removeCertificate(NodeCertificate certificate);

  boolean verifyCertificate(NodeCertificate certificate, String remoteAddr);

  Optional<NodeCertificate> getCertificateById(String id);

  CertificateRequest certificateRequest(String origin, Node localNode)
      throws TargetNodeUnreachableException;
}
