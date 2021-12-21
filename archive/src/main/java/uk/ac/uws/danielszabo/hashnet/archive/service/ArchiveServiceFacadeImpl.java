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

package uk.ac.uws.danielszabo.hashnet.archive.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.common.service.image.TopicService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.common.service.network.SubscriptionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArchiveServiceFacadeImpl implements ArchiveServiceFacade {

  private final HashService hashService;

  private final LocalNodeService localNodeService;

  private final NetworkService networkService;

  private final SubscriptionService subscriptionService;

  private final HashCollectionService hashCollectionService;

  private final TopicService topicService;

  public ArchiveServiceFacadeImpl(
      HashService hashService,
      LocalNodeService localNodeService,
      NetworkService networkService,
      SubscriptionService subscriptionService,
      HashCollectionService hashCollectionService,
      TopicService topicService) {
    this.hashService = hashService;
    this.localNodeService = localNodeService;
    this.networkService = networkService;
    this.subscriptionService = subscriptionService;
    this.hashCollectionService = hashCollectionService;
    this.topicService = topicService;
  }

  @Override
  public boolean verifyCertificate(NodeCertificate certificate) {
    Optional<NodeCertificate> localCert = networkService.findCertificateById((certificate.getId()));
    return localCert.map(nodeCertificate -> nodeCertificate.equals(certificate)).orElse(false);
  }

  @Override
  public CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest) {
    return networkService.saveCertificateRequest(certificateRequest);
  }

  @Override
  public List<CertificateRequest> retrieveAllCertificateRequests() {
    return networkService.findAllCertificateRequests();
  }

  @Override
  public Optional<NodeCertificate> findCertificateRequestById(String id) {
    return networkService.findCertificateById(id);
  }

  @Override
  public List<HashCollection> retrieveAllHashCollections() {
    return hashCollectionService.findAll();
  }

  @Override
  public Optional<HashCollection> retrieveHashCollectionById(String id) {
    return hashCollectionService.findById(id);
  }

  @Override
  public List<HashCollection> retrieveHashCollectionByTopic(Topic topic) {
    return hashCollectionService.findByTopic(topic);
  }

  @Override
  public List<HashCollection> retrieveHashCollectionByArchive(Node topic) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Node> retrieveAllNodes() {
    return networkService.getAllKnownNodes();
  }

  @Override
  public Optional<Node> retrieveNodeById(String id) {
    return networkService.findKnownNodeById(id);
  }

  @Override
  public Node saveNode(Node node) {
    return networkService.saveNode(node);
  }

  @Override
  public void deleteNode(Node node) {
    networkService.removeNode(node);
  }

  @Override
  public List<Subscription> getSubscriptions() {
    return subscriptionService.getSubscriptions();
  }

  @Override
  public void saveCertificate(NodeCertificate certificate) {
    Node localNode = localNodeService.get();
    certificate.setNode(localNode);
    localNode.setCertificate(certificate);
    localNodeService.set(localNode);
  }

  @Override
  public HashCollection generateHashCollection(
      String path,
      String id,
      String name,
      String description,
      List<Topic> topics,
      boolean forceRecalc)
      throws IOException {
    return hashCollectionService.generateHashCollection(
        path, id, name, description, localNodeService.get(), topics, forceRecalc);
  }

  @Override
  public Topic findTopicById(String s) {
    return topicService.findById(s).orElse(null);
  }

  @Override
  public List<HashCollection> findAllHashCollections() {
    return hashCollectionService.findAll();
  }

  @Override
  public Optional<HashCollection> findAllHashCollectionById(String id) {
    return hashCollectionService.findById(id);
  }

  @Override
  public boolean checkCertificate(NodeCertificate certificate) {
    return networkService.checkCertificate(certificate);
  }
}
