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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.automodera.common.event.NetworkConfigurationUpdatedEvent;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeStatus;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.repository.*;
import uk.ac.uws.danielszabo.automodera.common.service.rest.RestService;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class NetworkServiceImpl implements NetworkService {

  protected final ApplicationEventPublisher applicationEventPublisher;

  protected final RestService restService;

  protected final NetworkConfigurationRepository networkConfigurationRepository;

  protected final CertificateRequestRepository certificateRequestRepository;

  protected final NodeRepository nodeRepository;

  protected final LocalNodeRepository localNodeRepository;

  private final HashCollectionRepository hashCollectionRepository;

  public NetworkServiceImpl(
      ApplicationEventPublisher applicationEventPublisher,
      NetworkConfigurationRepository networkConfigurationRepository,
      RestService restService,
      CertificateRequestRepository certificateRequestRepository,
      NodeRepository nodeRepository,
      LocalNodeRepository localNodeRepository,
      HashCollectionRepository hashCollectionRepository) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.networkConfigurationRepository = networkConfigurationRepository;
    this.restService = restService;
    this.certificateRequestRepository = certificateRequestRepository;
    this.nodeRepository = nodeRepository;
    this.localNodeRepository = localNodeRepository;
    this.hashCollectionRepository = hashCollectionRepository;
  }

  @Override
  public NetworkConfiguration getNetworkConfiguration() {
    return networkConfigurationRepository.get().orElse(null);
  }

  @Override
  public NetworkConfiguration saveNetworkConfiguration(NetworkConfiguration networkConfiguration) {
    if (networkConfiguration == null) {
      log.error("Cannot overwrite network config with null.");
      return null;
    } else {
      NetworkConfigurationUpdatedEvent event =
          new NetworkConfigurationUpdatedEvent(this, networkConfiguration);
      applicationEventPublisher.publishEvent(event);
      return networkConfigurationRepository.save(networkConfiguration);
    }
  }

  /**
   * If this is certificate authority (operator or origin) node then we check our local database,
   * otherwise we request confirmation from the issuer. TODO later on this should work with
   * cryptography instead of web requests - what if CA node is
   *
   * @param certificate
   * @param remoteAddr
   * @return
   */
  @Override
  public boolean verifyCertificate(NodeCertificate certificate, String remoteAddr) {
    try {
      // check that certificate was sent by the node it was issued to
      if (InetAddress.getByName(certificate.getHost().split(":")[0])
          .getHostAddress()
          .equals(remoteAddr)) {
        // check if we are verifying an origin node certificate (self certificate)
        if (certificate.getHost().equals(networkConfigurationRepository.get().get().getOrigin())) {
          // it will be OK as long as it comes from the correct address
          return true;
        } else if (localNodeRepository
            // this certificate was issued by the local node
            .get()
            .get()
            .getLocal()
            .getId()
            .equals(certificate.getIssuer().getId())) {
          boolean result =
              localNodeRepository
                  .get()
                  .get()
                  .getLocal()
                  .getIssuedCertificates()
                  .contains(certificate);

          log.info(
              "Verifying certificate "
                  + certificate.getId()
                  + " locally: "
                  + (result ? "VALID" : "INVALID"));
          return result;
        } else {
          // this certificate was issued by someone else
          // so we ask the issuer to check it
          boolean result = restService.requestCertificateVerification(certificate);
          log.info(
              "Verifying certificate "
                  + certificate.getId()
                  + " at "
                  + certificate.getIssuer().getHost()
                  + " "
                  + (result ? "VALID" : "INVALID"));
          return result;
        }
      } else {
        log.info(
            "Verifying certificate "
                + certificate.getId()
                + " at "
                + certificate.getIssuer().getHost()
                + " INVALID (sent from incorrect address) ");
        return false;
      }
    } catch (Exception e) {
      log.error("Failed to verify certificate " + certificate.getId() + " " + e);
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Optional<NodeCertificate> getCertificateById(String id) {
    Node node = nodeRepository.findById(id).orElse(null);
    if (node == null) return Optional.empty();
    return Optional.ofNullable(node.getCertificate());
  }

  @Override
  public NodeStatus downloadNodeStatus(Node node) {
    NodeStatus status = restService.downloadNodeStatus(node.getHost());
    node.setOnline(status.isOnline());
    node.setActive(status.isActive());
    saveNode(node);
    return status;
  }

  @Override
  public NodeStatus downloadNodeStatus(String address) {
    return restService.downloadNodeStatus(address);
  }

  @Override
  public List<Node> getAllNodes() {
    return nodeRepository.findAll();
  }

  @Override
  public Optional<Node> getKnownNodeById(String id) {
    Optional<Node> optionalNode = nodeRepository.findById(id);
    optionalNode.ifPresent(this::downloadNodeStatus);
    return optionalNode;
  }

  @Override
  public Node saveNode(Node node) {
    return nodeRepository.save(node);
  }

  @Override
  public void removeNode(Node node) {
    nodeRepository.delete(node);
  }

  @Override
  public Node getLocalNode() {
    return localNodeRepository.get().get().getLocal();
  }

  @Override
  public CertificateRequest certificateRequest(String origin, Node localNode)
      throws TargetNodeUnreachableException {
    if (certificateRequestRepository.findAll().isEmpty()) {
      CertificateRequest certReq =
          new CertificateRequest(
              localNode.getId() + "-" + (int) (new Random().nextDouble() * Integer.MAX_VALUE),
              localNode);
      restService.sendCertificateRequest(origin, certReq);
      certificateRequestRepository.save(certReq);
      return certReq;
    } else {
      log.error("Did not send certificate request because one is already in progress");
      return null;
    }
  }

  @Override
  public Node getNodeByHost(String host) throws TargetNodeUnreachableException {
    return nodeRepository.save(restService.getNodeByHost(host));
  }

  @Override
  public List<Collection> requestCollectionRepertoireFromArchive(Node node)
      throws TargetNodeUnreachableException {
    List<Collection> collectionList = restService.requestRepertoire(node.getHost());
    node.setCollectionList(collectionList);
    hashCollectionRepository.saveAll(collectionList);
    nodeRepository.save(node);
    return collectionList;
  }

  @Override
  public void fetchAllNodeStatus() {
    for (Node node : nodeRepository.findAll()) {
      if (!node.equals(getLocalNode())) {
        downloadNodeStatus(node);
      }
    }
  }

  @Override
  public Collection downloadCollection(String host, String id)
      throws TargetNodeUnreachableException {

    Collection collection = this.restService.downloadCollection(host, id);

    // patching in a missing reference that gets lost due to XML representation
    collection.getImageList().forEach(image -> image.setCollection(collection));

    return hashCollectionRepository.save(collection);
  }

  @Override
  public void sendCollectionRepertoireToIntegrator(
      List<Collection> collectionList, String subscriberHost)
      throws TargetNodeUnreachableException {
    restService.sendCollectionRepertoireToIntegrator(collectionList, subscriberHost);
  }

  /**
   * Tries to update local archive repository from operator node, if fails it will use the local
   * repository list cache.
   *
   * <p>It downloads only the addresses from the operator, the actual node info will be fetched from
   * archives directly.
   *
   * @return list of archives after attempting to update it from operator node
   */
  @Override
  public List<Node> getAllArchives() {
    try {
      updateArchiveRepository();
    } catch (Exception e) {
      log.error("Failed to update archive repository: " + e.getMessage());
    }
    return nodeRepository.findAllByNodeType(NodeType.ARCHIVE);
  }

  /**
   * Tries to update local integrator repository from operator node, if fails it will use the local
   * repository list cache.
   *
   * <p>It downloads only the addresses from the operator, the actual node info will be fetched from
   * integrators directly.
   *
   * @return list of integrators after attempting to update it from operator node
   */
  @Override
  public List<Node> getAllIntegrators() {
    try {
      updateIntegratorRepository();
    } catch (Exception e) {
      log.error("Failed to update integrator repository: " + e.getMessage());
    }
    return nodeRepository.findAllByNodeType(NodeType.INTEGRATOR);
  }

  @Override
  public Optional<CertificateRequest> findCertificateRequestById(String id) {
    return certificateRequestRepository.findById(id);
  }

  @Override
  public NetworkConfiguration fetchNetworkConfigurationAndSave(String originHost)
      throws TargetNodeUnreachableException {
    NetworkConfiguration networkConfiguration;
    if ((networkConfiguration = networkConfigurationRepository.get().orElse(null)) != null) {
      log.error("You are already connected to a network: " + networkConfiguration.getName());
    } else {
      networkConfiguration = restService.getNetworkConfiguration(originHost);
      if (networkConfiguration != null) {
        saveNetworkConfiguration(networkConfiguration);
      }
    }
    return networkConfiguration;
  }

  @Override
  public NetworkConfiguration fetchNetworkConfiguration(String originHost)
      throws TargetNodeUnreachableException {
    return restService.getNetworkConfiguration(originHost);
  }

  @Override
  public List<Node> getAllKnownNodes() {
    fetchAllNodeStatus();
    return nodeRepository.findAll();
  }

  @Override
  public List<CertificateRequest> findAllCertificateRequests() {
    return certificateRequestRepository.findAll();
  }

  @Override
  public CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest) {
    return certificateRequestRepository.save(certificateRequest);
  }

  @Override
  public void sendProcessedCertificateRequest(CertificateRequest certificateRequest)
      throws Exception {
    restService.sendProcessedCertificateRequest(certificateRequest);
  }

  @Override
  public void removeCertificate(NodeCertificate certificate) {
    throw new UnsupportedOperationException();
  }

  private List<String> getAllArchiveAddresses() throws TargetNodeUnreachableException {
    log.info("Requesting list of all archive addresses from origin");
    return this.restService.requestAllArchiveAddresses(
        networkConfigurationRepository.get().get().getOrigin());
  }

  private List<String> getAllIntegratorAddresses() throws TargetNodeUnreachableException {
    log.info("Requesting list of all archive addresses from origin");
    return this.restService.requestAllIntegratorAddresses(
        networkConfigurationRepository.get().get().getOrigin());
  }

  private void updateArchiveRepository() throws TargetNodeUnreachableException {
    for (String host : getAllArchiveAddresses()) {
      try {
        nodeRepository.save(restService.getNodeByHost(host));
      } catch (TargetNodeUnreachableException e) {
        e.printStackTrace();
      }
    }
  }

  private void updateIntegratorRepository() throws TargetNodeUnreachableException {
    for (String host : getAllIntegratorAddresses()) {
      try {
        nodeRepository.save(restService.getNodeByHost(host));
      } catch (TargetNodeUnreachableException e) {
        e.printStackTrace();
      }
    }
  }
}
