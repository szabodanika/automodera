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

package uk.ac.uws.danielszabo.automodera.integrator.service;

import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.integrator.model.Report;
import uk.ac.uws.danielszabo.automodera.integrator.model.IntegrationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IntegratorServiceFacade {

  boolean verifyCertificate(NodeCertificate certificate);

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  List<CertificateRequest> retrieveAllCertificateRequests();

  Optional<CertificateRequest> findCertificateRequestById(String id);

  List<Collection> retrieveAllHashCollections();

  Optional<Collection> retrieveHashCollectionById(String id);

  List<Collection> retrieveHashCollectionByTopic(String string);

  List<Collection> retrieveHashCollectionByArchive(Node topic);

  List<Node> getAllKnownNodes();

  Optional<Node> getKnownNodeById(String id);

  Node saveNode(Node node);

  void deleteNode(Node node);

  List<String> getSubscriptions();

  void saveCertificate(NodeCertificate certificate);

  List<Collection> findAllHashCollections();

  Optional<Collection> findCollectionById(String id);

  boolean checkCertificate(NodeCertificate certificate, String remoteAddr);

  Report checkImage(String image, String attachment, String source) throws IOException;

  void requestAllCollectionRepertoires() throws Exception;

  List<Collection> requestCollectionRepertoireFromArchive(Node archive)
      throws TargetNodeUnreachableException;

  void addSubscription(String string);

  List<Node> getAllArchives();

  Collection downloadHashCollection(String host, String id) throws Exception;

  void removeSubscription(String topic);

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

  List<Collection> requestCollectionRepertoireFormArchive(Node n) throws Exception;

  NetworkConfiguration getNetworkConfiguration();

  List<Collection> getCollecitonsByTopic(String topic) throws Exception;

  List<CertificateRequest> getAllCertificateRequests();

  void connectToNetwork(String host) throws Exception;

  List<Collection> retrieveDownloadedHashCollections();

  List<String> getAllTopics();

  Map<String, Map<String, Object>> getAllTopicsWithCollections();

  boolean isSubscribedToAny(List<String> topicList);

  boolean isSubscribedTo(String topic);

  void processCollectionRepertoire(CollectionRepertoire collectionRepertoire);

  NetworkConfiguration fetchNetworkConfigurationAndConnect(String host) throws Exception;

  NetworkConfiguration fetchNetworkConfiguration(String host) throws TargetNodeUnreachableException;

  void clearNetworkConfiguration();

  IntegrationContext getIntegrationConfiguration();

  List<Report> getAllReports();
}
