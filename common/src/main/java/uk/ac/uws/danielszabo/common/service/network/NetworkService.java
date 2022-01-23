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

import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeStatus;

import java.util.List;
import java.util.Optional;

public interface NetworkService {

  // Network Configuration

  NetworkConfiguration getNetworkConfiguration();

  NetworkConfiguration saveNetworkConfiguration(NetworkConfiguration networkConfiguration);

  Optional<CertificateRequest> findCertificateRequestById(String id);

  void getNetworkConfigurationFromOrigin(String origin) throws Exception;

  // Nodes

  NodeStatus getNodeStatus(Node node) throws Exception;

  NodeStatus getNodeStatus(String address) throws Exception;

  List<Node> findAllNodes();

  Optional<Node> findKnownNodeById(String id);

  Node saveNode(Node node);

  void removeNode(Node node);

  Node getLocalNode();

  /**
   * this should return all the nodes in the database: - local node - operator nodes - nodes
   * subscribed to or publishing to
   *
   * @return list of all known nodes
   */
  List<Node> getAllKnownNodes();

  List<Node> getAllArchives();

  String resolveNodeId(String origin, String id) throws TargetNodeUnreachableException;

  Node getNodeByHost(String host) throws TargetNodeUnreachableException;

  // HashCollections, Topics, Images

  HashCollection downloadHashCollection(String host, String id)
      throws TargetNodeUnreachableException;

  void publishHashCollections(List<HashCollection> hashCollectionList, Node subscriber)
      throws Exception;

  //  Optional<String> getTopicById(String id) throws Exception;

  List<HashCollection> requestAllHashCollectionsByArchive(Node node)
      throws TargetNodeUnreachableException;

  // Certificate Requests

  List<CertificateRequest> findAllCertificateRequests();

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  void sendProcessedCertificateRequest(CertificateRequest certificateRequest) throws Exception;

  // Certificates

  void removeCertificate(NodeCertificate certificate);

  boolean checkCertificate(NodeCertificate certificate, String remoteAddr);

  Optional<NodeCertificate> findCertificateById(String id);

  CertificateRequest certificateRequest(String origin, Node localNode) throws Exception;

  void sendSubscription(Node node, String string) throws TargetNodeUnreachableException;

  void fetchAllNodeStatus() throws Exception;
}
