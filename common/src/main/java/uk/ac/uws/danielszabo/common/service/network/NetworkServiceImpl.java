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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.event.NetworkConfigurationUpdatedEvent;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeStatus;
import uk.ac.uws.danielszabo.common.repository.CertificateRequestRepository;
import uk.ac.uws.danielszabo.common.repository.LocalNodeRepository;
import uk.ac.uws.danielszabo.common.repository.NetworkConfigurationRepository;
import uk.ac.uws.danielszabo.common.repository.NodeRepository;
import uk.ac.uws.danielszabo.common.service.rest.RestService;

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

  public NetworkServiceImpl(
    ApplicationEventPublisher applicationEventPublisher,
    NetworkConfigurationRepository networkConfigurationRepository,
    RestService restService,
    CertificateRequestRepository certificateRequestRepository,
    NodeRepository nodeRepository,
    LocalNodeRepository localNodeRepository) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.networkConfigurationRepository = networkConfigurationRepository;
    this.restService = restService;
    this.certificateRequestRepository = certificateRequestRepository;
    this.nodeRepository = nodeRepository;
    this.localNodeRepository = localNodeRepository;
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

  @Override
  public boolean checkCertificate(NodeCertificate certificate, String remoteAddr) {
    // if this is certificate authority (operator or origin) node
    // then we check our local database, otherwise we request confirmation
    // from the issuer.
    // TODO later on this should work with cryptography instead of web requests - what if CA node is offline?

    try {
      // check that certificate was sent by the node it was issued to
      if (InetAddress.getByName(certificate.getHost()).getHostAddress().equals(remoteAddr)) {
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
          .equals(certificate.getIssuer().getId())
        ) {
          boolean result = localNodeRepository
            .get()
            .get()
            .getLocal()
            .getIssuedCertificates()
            .contains(certificate);

          log.info("Verifying certificate " + certificate.getId() + " locally: " + (result ? "VALID" : "INVALID"));
          return result;
        } else {
          // this certificate was issued by someone else
          // so we ask the issuer to check it
          boolean result = restService.requestCertificateVerification(certificate);
          log.info("Verifying certificate " + certificate.getId() + " at "
            + certificate.getIssuer().getHost() + " " + (result ? "VALID" : "INVALID"));
          return result;
        }
      } else {
        log.info("Verifying certificate " + certificate.getId() + " at "
          + certificate.getIssuer().getHost() + " INVALID (sent from incorrect address) ");
        return false;
      }
    } catch (Exception e) {
      log.error("Failed to verify certificate " + certificate.getId() + " " + e);
      return false;
    }
  }

  @Override
  public Optional<NodeCertificate> findCertificateById(String id) {
    Node node = nodeRepository.findById(id).orElse(null);
    if (node == null) return Optional.empty();
    return Optional.of(node.getCertificate());
  }

  @Override
  public NodeStatus getNodeStatus(String address) {
    return restService.requestStatus(address);
  }

  @Override
  public List<Node> findAllNodes() {
    return nodeRepository.findAll();
  }

  @Override
  public Optional<Node> findKnownNodeById(String id) {
    return nodeRepository.findById(id);
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
  public CertificateRequest certificateRequest(String origin, Node localNode) {
    if (certificateRequestRepository.findAll().isEmpty()) {
      CertificateRequest certReq =
        new CertificateRequest(localNode.getId() + "-" + new Random().nextInt(), localNode);
      certificateRequestRepository.save(certReq);
      restService.sendCertificateRequest(origin, certReq);
      return certReq;
    } else {
      log.error("Did not send certificate request because one is already in progress");
      return null;
    }
  }

  @Override
  public Node requestNodeInfo(String host) {
    return restService.getNodeByHost(host);
  }

  @Override
  public Optional<CertificateRequest> findCertificateRequestById(String id) {
    return certificateRequestRepository.findById(id);
  }

  @Override
  public void getNetworkConfigurationFromOrigin(String originHost) {
    NetworkConfiguration networkConfiguration;
    if ((networkConfiguration = networkConfigurationRepository.get().orElse(null)) != null) {
      log.error("You are already connected to a network: " + networkConfiguration.getName());
    } else {
      networkConfiguration = restService.sendNetworkConfigurationRequest(originHost);
      if (networkConfiguration != null) {
        saveNetworkConfiguration(networkConfiguration);
      }
    }
  }

  @Override
  public List<Node> getAllKnownNodes() {
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
  public void sendProcessedCertificateRequest(CertificateRequest certificateRequest) {
    restService.sendProcessedCertificateRequest(certificateRequest);
  }

  @Override
  public void removeCertificate(NodeCertificate certificate) {
    throw new UnsupportedOperationException();
  }
}
