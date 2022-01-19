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

package uk.ac.uws.danielszabo.hashnet.operator.service;

import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.messages.ArchiveAddressesMessage;
import uk.ac.uws.danielszabo.common.model.network.node.Node;

import java.util.List;
import java.util.Optional;

public interface OperatorServiceFacade {

  boolean handleCertificateRequest(
      CertificateRequest certificateRequest, CertificateRequest.Status newStatus, String message)
      throws Exception;

  boolean reissueCertificateForNode(Node node);

  boolean revokeCertificateForNode(Node node);

  boolean verifyCertificate(NodeCertificate certificate);

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  List<CertificateRequest> findAllCertificateRequests();

  Optional<CertificateRequest> findCertificateRequestById(String id);

  List<HashCollection> retrieveAllHashCollections();

  Optional<HashCollection> retrieveHashCollectionById(String id);

  List<HashCollection> retrieveHashCollectionByTopic(Topic topic);

  List<HashCollection> retrieveHashCollectionByArchive(Node topic) throws Exception;

  List<Node> findAllNodes() throws Exception;

  Optional<Node> findKnownNodeById(String id);

  Node saveNode(Node node);

  void deleteNode(Node node);

  void saveNetworkConfiguration(NetworkConfiguration networkConfiguration);

  NetworkConfiguration getNetworkConfiguration();

  ArchiveAddressesMessage getArchiveAddressesMessage();

  Node getLocalNode();

  void fetchAllNodeStatus() throws Exception;
}
