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
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.event.LocalNodeUpdatedEvent;
import uk.ac.uws.danielszabo.common.model.images.HashCollection;
import uk.ac.uws.danielszabo.common.model.images.Topic;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.rest.RestService;
import uk.ac.uws.danielszabo.hashnet.operator.repository.CertificateRequestRepository;
import uk.ac.uws.danielszabo.hashnet.operator.repository.HashCollectionRepository;
import uk.ac.uws.danielszabo.hashnet.operator.repository.NodeCertificateRepository;
import uk.ac.uws.danielszabo.hashnet.operator.repository.NodeRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OperatorServiceImpl implements OperatorService {

  private final NodeCertificateRepository nodeCertificateRepository;

  private final NodeRepository nodeRepository;

  private final HashCollectionRepository hashCollectionRepository;

  private final CertificateRequestRepository certificateRequestRepository;

  private final RestService restService;

  private final LocalNodeService localNodeService;

  public OperatorServiceImpl(
      NodeCertificateRepository nodeCertificateRepository,
      NodeRepository nodeRepository,
      HashCollectionRepository hashCollectionRepository,
      CertificateRequestRepository certificateRequestRepository,
      RestService restService,
      LocalNodeService localNodeService) {
    this.nodeCertificateRepository = nodeCertificateRepository;
    this.nodeRepository = nodeRepository;
    this.hashCollectionRepository = hashCollectionRepository;
    this.certificateRequestRepository = certificateRequestRepository;
    this.restService = restService;
    this.localNodeService = localNodeService;
  }

  // persist local node in the database as well, because lots of certificates, certificate requests
  // etc. are going to refer to it
  @EventListener
  public void handleLocalNodeUpdatedEvent(LocalNodeUpdatedEvent event) {
    nodeRepository.save(event.getLocalNode());
  }

  @Override
  public List<NodeCertificate> retrieveAllCertificates() {
    return nodeCertificateRepository.findAll();
  }

  @Override
  public boolean handleCertificateRequest(
      CertificateRequest certificateRequest, CertificateRequest.Status newStatus, String message) {
    certificateRequest.setStatus(newStatus);
    if (newStatus == CertificateRequest.Status.ISSUED) {
      NodeCertificate cert = certificateRequest.getNode().getCertificate();
      cert.setIssued(new Date(new java.util.Date().getTime()));
      // TODO this should be configurable, not always 365 days
      cert.setExpiration(new Date(new java.util.Date().getTime() + (1000L * 60 * 60 * 24 * 365)));
      cert.setIssuer(localNodeService.getLocalNode());

      // save cert in this operator/origin node's list
      localNodeService
          .getLocalNode()
          .getIssuedCertificates()
          .add(certificateRequest.getNode().getCertificate());
      // persist local node
      localNodeService.saveLocalNode();
      // persist request
      certificateRequestRepository.save(certificateRequest);
    }

    certificateRequest.setMessage(message);

    restService.sendProcessedCertificateRequest(
        certificateRequest.getNode().getHost(), certificateRequest);

    // TODO dont always just return true
    return true;
  }

  @Override
  public boolean reissueCertificateForNode(Node node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean revokeCertificateForNode(Node node) {
    if (nodeCertificateRepository.findById(node.getCertificate().getId()).isPresent()) {
      nodeCertificateRepository.delete(node.getCertificate());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean verifyCertificate(NodeCertificate certificate) {
    Optional<NodeCertificate> localCert = nodeCertificateRepository.findById(certificate.getId());
    return localCert.map(nodeCertificate -> nodeCertificate.equals(certificate)).orElse(false);
  }

  @Override
  public CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest) {
    return certificateRequestRepository.save(certificateRequest);
  }

  @Override
  public List<CertificateRequest> retrieveAllCertificateRequests() {
    return certificateRequestRepository.findAll();
  }

  @Override
  public Optional<CertificateRequest> findCertificateRequestById(String id) {
    return certificateRequestRepository.findById(id);
  }

  @Override
  public List<HashCollection> retrieveAllHashCollections() {
    return hashCollectionRepository.findAll();
  }

  @Override
  public Optional<HashCollection> retrieveHashCollectionById(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<HashCollection> retrieveHashCollectionByTopic(Topic topic) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<HashCollection> retrieveHashCollectionByArchive(Node topic) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Node> retrieveAllNodes() {
    return nodeRepository.findAll();
  }

  @Override
  public Optional<Node> retrieveNodeById(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node saveNode(Node node) {
    return nodeRepository.save(node);
  }

  @Override
  public boolean deleteNode(Node node) {
    throw new UnsupportedOperationException();
  }
}
