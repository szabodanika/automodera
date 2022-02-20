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

package uk.ac.uws.danielszabo.automodera.operator.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.operator.OperatorServer;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.AddressListMessage;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.automodera.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.automodera.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.automodera.common.service.network.NetworkService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OperatorServiceFacadeImpl implements OperatorServiceFacade {

    // 1 year
    public static final long CERTIFICATE_EXPIRATION_DATE_OFFSET = (1000L * 60 * 60 * 24 * 365);

    private final LocalNodeService localNodeService;

    private final NetworkService networkService;

    private final HashCollectionService hashCollectionService;

    public OperatorServiceFacadeImpl(
            HashService hashService,
            LocalNodeService localNodeService,
            NetworkService networkService,
            HashCollectionService hashCollectionService) {
        this.localNodeService = localNodeService;
        this.networkService = networkService;
        this.hashCollectionService = hashCollectionService;
    }

    @Override
    public boolean handleCertificateRequest(
            CertificateRequest certificateRequest,
            CertificateRequest.Status newStatus,
            String message)
            throws Exception {
        certificateRequest.setStatus(newStatus);
        if (newStatus == CertificateRequest.Status.ISSUED) {
            NodeCertificate cert = certificateRequest.getNode().getCertificate();
            cert.setIssued(new Date(new java.util.Date().getTime()));
            // TODO this should be configurable, not always 365 days
            cert.setExpiration(new Date(new java.util.Date().getTime() + CERTIFICATE_EXPIRATION_DATE_OFFSET));
            cert.setIssuer(localNodeService.get());

            // send it to the node first - if it's offline,
            // this will throw exception and the updated
            // request will not be saved locally
            networkService.sendProcessedCertificateRequest(certificateRequest);

            // save cert in this operator/origin node's list
            Node localNode = localNodeService.get();
            localNode.getIssuedCertificates().add(certificateRequest.getNode().getCertificate());

            // persist local node
            localNodeService.set(localNode);

            certificateRequest.setMessage(message);

            // persist request
            networkService.saveCertificateRequest(certificateRequest);
        }

        // TODO dont always just return true
        return true;
    }

    @Override
    public boolean reissueCertificateForNode(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean revokeCertificateForNode(Node node) {
        if (networkService.getCertificateById(node.getCertificate().getId()).isPresent()) {
            networkService.removeCertificate(node.getCertificate());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean verifyCertificate(NodeCertificate certificate) {
        // find the certificate in our own database
        Optional<NodeCertificate> localCert = networkService.getCertificateById(certificate.getId());
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
    public List<Collection> retrieveAllHashCollections() {
        return hashCollectionService.findAll();
    }

    @Override
    public Optional<Collection> retrieveHashCollectionById(String id) {
        return hashCollectionService.findById(id);
    }

    @Override
    public List<Collection> retrieveHashCollectionsByTopic(String string) {
        return hashCollectionService.findAllByTopic(string);
    }

    @Override
    public List<Collection> retrieveHashCollectionsByArchive(Node node) throws Exception {
        return networkService.requestCollectionRepertoireFromArchive(node);
    }

    @Override
    public List<Node> findAllNodes() throws Exception {
        return networkService.getAllKnownNodes();
    }

    @Override
    public Optional<Node> findKnownNodeById(String id) {
        return networkService.getKnownNodeById(id);
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
    public AddressListMessage getArchiveAddressesMessage() {
        return new AddressListMessage(
                networkService.getAllKnownNodes().stream()
                        .filter(n -> n.getNodeType() == NodeType.ARCHIVE)
                        .map(Node::getHost)
                        .collect(Collectors.toList()));
    }

    @Override
    public AddressListMessage getIntegratorAddressesMessage() {
        return new AddressListMessage(
                networkService.getAllKnownNodes().stream()
                        .filter(n -> n.getNodeType() == NodeType.INTEGRATOR)
                        .map(Node::getHost)
                        .collect(Collectors.toList()));
    }

    @Override
    public Node getLocalNode() {
        Node node = localNodeService.get();
        if (node != null) {
            Hibernate.initialize(node.getCertificate().getIssuer());
        }
        return node;
    }

    @Override
    public void fetchAllNodeStatus() throws Exception {
        networkService.fetchAllNodeStatus();
    }

    @Override
    public void shutDown() {
        OperatorServer.exit();
    }

    @Override
    public void init(
            String id,
            String displayName,
            String domainName,
            String legalName,
            String adminEmail,
            String addressLine1,
            String addressLine2,
            String postCode,
            String country) {
        localNodeService.init(
                id,
                NodeType.ORIGIN,
                displayName,
                domainName,
                legalName,
                adminEmail,
                addressLine1,
                addressLine2,
                postCode,
                country);
    }
}
