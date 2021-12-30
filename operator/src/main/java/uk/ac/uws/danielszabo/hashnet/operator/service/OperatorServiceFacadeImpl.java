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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.messages.ArchiveAddressesMessage;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.common.service.network.SubscriptionService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OperatorServiceFacadeImpl implements OperatorServiceFacade {

  private final HashService hashService;

  private final LocalNodeService localNodeService;

  private final NetworkService networkService;

  private final SubscriptionService subscriptionService;

  private final HashCollectionService hashCollectionService;

  public OperatorServiceFacadeImpl(
      HashService hashService,
      LocalNodeService localNodeService,
      NetworkService networkService,
      SubscriptionService subscriptionService,
      HashCollectionService hashCollectionService) {
    this.hashService = hashService;
    this.localNodeService = localNodeService;
    this.networkService = networkService;
    this.subscriptionService = subscriptionService;
    this.hashCollectionService = hashCollectionService;
  }

  @Override
  public boolean handleCertificateRequest(
      CertificateRequest certificateRequest, CertificateRequest.Status newStatus, String message)
      throws Exception {
    certificateRequest.setStatus(newStatus);
    if (newStatus == CertificateRequest.Status.ISSUED) {
      NodeCertificate cert = certificateRequest.getNode().getCertificate();
      cert.setIssued(new Date(new java.util.Date().getTime()));
      // TODO this should be configurable, not always 365 days
      cert.setExpiration(new Date(new java.util.Date().getTime() + (1000L * 60 * 60 * 24 * 365)));
      cert.setIssuer(localNodeService.get());

      // save cert in this operator/origin node's list
      localNodeService
          .get()
          .getIssuedCertificates()
          .add(certificateRequest.getNode().getCertificate());
      // persist local node
      localNodeService.set();
      // persist request
      networkService.saveCertificateRequest(certificateRequest);
    }

    certificateRequest.setMessage(message);

    networkService.sendProcessedCertificateRequest(certificateRequest);

    // TODO dont always just return true
    return true;
  }

  @Override
  public boolean reissueCertificateForNode(Node node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean revokeCertificateForNode(Node node) {
    if (networkService.findCertificateById(node.getCertificate().getId()).isPresent()) {
      networkService.removeCertificate(node.getCertificate());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean verifyCertificate(NodeCertificate certificate) {
    // find the certificate in our own database
    Optional<NodeCertificate> localCert = networkService.findCertificateById(certificate.getId());
    if (localCert.isPresent()) {
      // check validity dates
      if (localCert.get().getExpiration().before(new Date(new java.util.Date().getTime()))
          || (new Date(new java.util.Date().getTime()).before(localCert.get().getIssued())))
        return false;

      // date all good, check that the certificate we received matches our own version
      return localCert.map(nodeCertificate -> nodeCertificate.equals(certificate)).orElse(false);
    } else return false;
  }

  @Override
  public CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest) {
    return networkService.saveCertificateRequest(certificateRequest);
  }

  @Override
  public List<CertificateRequest> findAllCertificateRequests() {
    return networkService.findAllCertificateRequests();
  }

  @Override
  public Optional<CertificateRequest> findCertificateRequestById(String id) {
    return networkService.findCertificateRequestById(id);
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
  public List<HashCollection> retrieveHashCollectionByArchive(Node node) throws Exception {
    return networkService.requestAllHashCollectionsByArchive(node);
  }

  @Override
  public List<Node> findAllNodes() {
    return networkService.getAllKnownNodes();
  }

  @Override
  public Optional<Node> findKnownNodeById(String id) {
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
  public void saveNetworkConfiguration(NetworkConfiguration networkConfiguration) {
    networkService.saveNetworkConfiguration(networkConfiguration);
  }

  @Override
  public NetworkConfiguration getNetworkConfiguration() {
    return networkService.getNetworkConfiguration();
  }

  @Override
  public ArchiveAddressesMessage getArchiveAddressesMessage() {
    return new ArchiveAddressesMessage(
        networkService.getAllKnownNodes().stream().map(Node::getHost).collect(Collectors.toList()));
  }

  @Override
  public Node getLocalNode() {
    return localNodeService.get();
  }
}
