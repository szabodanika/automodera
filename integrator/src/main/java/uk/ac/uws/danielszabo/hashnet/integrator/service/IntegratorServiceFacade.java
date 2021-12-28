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

package uk.ac.uws.danielszabo.hashnet.integrator.service;

import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.network.messages.HashCollectionsMessage;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IntegratorServiceFacade {

  boolean verifyCertificate(NodeCertificate certificate);

  CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest);

  List<CertificateRequest> retrieveAllCertificateRequests();

  Optional<CertificateRequest> findCertificateRequestById(String id);

  List<HashCollection> retrieveAllHashCollections();

  Optional<HashCollection> retrieveHashCollectionById(String id);

  List<HashCollection> retrieveHashCollectionByTopic(Topic topic);

  List<HashCollection> retrieveHashCollectionByArchive(Node topic);

  List<Node> retrieveAllNodes();

  Optional<Node> retrieveNodeById(String id);

  Node saveNode(Node node);

  void deleteNode(Node node);

  List<Subscription> getSubscriptions();

  void saveCertificate(NodeCertificate certificate);

  HashCollection generateHashCollection(
      String path,
      String id,
      String name,
      String description,
      List<Topic> topics,
      boolean forceRecalc)
      throws IOException;

  Optional<Topic> findTopicById(String s) throws Exception;

  List<HashCollection> findAllHashCollections();

  Optional<HashCollection> findAllHashCollectionById(String id);

  boolean checkCertificate(NodeCertificate certificate, String remoteAddr);

  HashCollectionsMessage getHashCollectionReport();

  Optional<Node> retrieveNodeByHost(String host) throws Exception;

  boolean removeSubscriptionByArchiveId(String id);

  void addSubscription(Topic topic);

  List<Node> getAllArchives();
}
