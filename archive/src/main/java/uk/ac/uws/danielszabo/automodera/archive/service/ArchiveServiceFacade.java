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

package uk.ac.uws.danielszabo.automodera.archive.service;

import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ArchiveServiceFacade {

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  Optional<CertificateRequest> findCertificateRequestById(String id);

  List<Collection> retrieveAllHashCollections();

  Optional<Collection> retrieveHashCollectionById(String id);

  List<Node> retrieveAllNodes();

  Optional<Node> findKnownNodeById(String id);

  Node saveNode(Node node);

  void deleteNode(Node node);

  void saveCertificate(NodeCertificate certificate);

  Collection generateHashCollection(
      String path,
      String id,
      String name,
      String description,
      List<String> strings,
      boolean forceRecalc)
      throws IOException;

  List<Collection> findAllHashCollections();

  Optional<Collection> findHashCollectionById(String id);

  boolean checkCertificate(NodeCertificate certificate, String remoteAddr);

  CollectionRepertoire getHashCollectionReport();

  void storeNodeInfo(String subscriberId) throws Exception;

  /**
   * Send out all the enabled collections wrapped in a repertoire object to every available
   * integrator
   */
  void sendCollectionRepertoireToAllIntegrators();

  /**
   * Send out all the enabled collections wrapped in a repertoire object to the specified integrator
   */
  void sendCollectionRepertoireToIntegrator(Node integrator) throws TargetNodeUnreachableException;

  /**
   * Send out all the specified collections wrapped in a repertoire object to every available
   * integrator
   */
  void sendCollectionRepertoireToAllIntegrators(List<String> collections);

  /**
   * Send out all the specified collections wrapped in a repertoire object to the specified
   * integrator
   */
  void sendCollectionRepertoireToIntegrator(List<String> collections, Node integrator)
      throws TargetNodeUnreachableException;

  Node getLocalNode();

  void shutDown();

  void init(
      String id,
      String displayName,
      String domainName,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country);

  List<Collection> retrieveHashCollectionsByArchive(Node n) throws Exception;

  NetworkConfiguration getNetworkConfiguration();

  List<Collection> retrieveHashCollectionsByTopic(String topic) throws Exception;

  List<CertificateRequest> findAllCertificateRequests();

  NetworkConfiguration fetchNetworkConfigurationAndConnect(String host) throws Exception;

  NetworkConfiguration fetchNetworkConfiguration(String host) throws TargetNodeUnreachableException;

  void clearNetworkConfiguration();

  void enableHashCollectionById(String s);

  void disableHashCollectionById(String s);

  void removeHashCollectionById(String s);
}
