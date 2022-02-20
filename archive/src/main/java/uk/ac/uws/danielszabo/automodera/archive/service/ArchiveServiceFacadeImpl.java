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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.automodera.archive.ArchiveServer;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.automodera.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.automodera.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.automodera.common.service.network.NetworkService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArchiveServiceFacadeImpl implements ArchiveServiceFacade {

    private static final String INPUT_BASE_PATH = "./input";

    private final LocalNodeService localNodeService;

    private final NetworkService networkService;

    private final HashCollectionService hashCollectionService;

    public ArchiveServiceFacadeImpl(
            HashService hashService,
            LocalNodeService localNodeService,
            NetworkService networkService,
            HashCollectionService hashCollectionService) {
        this.localNodeService = localNodeService;
        this.networkService = networkService;
        this.hashCollectionService = hashCollectionService;
    }

    @Override
    public CertificateRequest saveCertificateRequest(CertificateRequest certificateRequest) {
        return networkService.saveCertificateRequest(certificateRequest);
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
    public List<Node> retrieveAllNodes() {
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
    public void saveCertificate(NodeCertificate certificate) {
        Node localNode = localNodeService.get();
        certificate.setNode(localNode);
        localNode.setCertificate(certificate);
        localNodeService.set(localNode);
    }

    @Override
    public Collection generateHashCollection(
            String path,
            String id,
            String name,
            String description,
            List<String> strings,
            boolean forceRecalc)
            throws IOException {
        return hashCollectionService.generateHashCollection(
                INPUT_BASE_PATH + path, id, name, description, localNodeService.get(), strings, forceRecalc);
    }

    @Override
    public List<Collection> findAllHashCollections() {
        return hashCollectionService.findAll();
    }

    @Override
    public Optional<Collection> findHashCollectionById(String id) {
        return hashCollectionService.findById(id);
    }

    @Override
    public boolean checkCertificate(NodeCertificate certificate, String remoteAddr) {
        return networkService.verifyCertificate(certificate, remoteAddr);
    }

    @Override
    public CollectionRepertoire getHashCollectionReport() {
        return new CollectionRepertoire(hashCollectionService.findAllEnabledNoImages());
    }

    @Override
    public void storeNodeInfo(String host) throws Exception {
        this.saveNode(networkService.getNodeByHost(host));
    }

    /**
     * Updates integrator list from operator if possible
     * then out current repertoire to each integrator
     *
     * @throws Exception
     */
    @Transactional
    @Override
    public void sendCollectionRepertoireToAllIntegrators() {
        for (Node integrator : this.networkService.getAllIntegrators()) {
            try {
                sendCollectionRepertoireToIntegrator(integrator);
            } catch (TargetNodeUnreachableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends out all enabled collections to integrator without content
     *
     * @param integrator
     * @throws TargetNodeUnreachableException
     */
    @Override
    public void sendCollectionRepertoireToIntegrator(Node integrator) throws TargetNodeUnreachableException {
        List<Collection> collectionList = hashCollectionService.findAllEnabledNoImages();
        networkService.sendCollectionRepertoireToIntegrator(collectionList, integrator.getHost());
    }

    @Override
    public void sendCollectionRepertoireToAllIntegrators(List<String> collections) {
        for (Node integrator : this.networkService.getAllIntegrators()) {
            try {
                sendCollectionRepertoireToIntegrator(collections, integrator);
            } catch (TargetNodeUnreachableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendCollectionRepertoireToIntegrator(List<String> collections, Node integrator) throws TargetNodeUnreachableException {
        List<Collection> collectionList = hashCollectionService.findAllEnabledNoImages();

        collectionList = collectionList.stream().filter(collection -> collections.contains(collection.getId())).collect(Collectors.toList());

        networkService.sendCollectionRepertoireToIntegrator(collectionList, integrator.getHost());
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
    public void shutDown() {
        ArchiveServer.exit();
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
                NodeType.ARCHIVE,
                displayName,
                domainName,
                legalName,
                adminEmail,
                addressLine1,
                addressLine2,
                postCode,
                country);
    }

    @Override
    public List<Collection> retrieveHashCollectionsByArchive(Node n) {
        if (n.equals(getLocalNode())) {
            return findAllHashCollections();
        } else {
            try {
                return networkService.requestCollectionRepertoireFromArchive(n);
            } catch (Exception e) {
                n.setOnline(false);
                n.setActive(false);
                saveNode(n);
                return new ArrayList<>();
            }
        }
    }

    @Override
    public NetworkConfiguration getNetworkConfiguration() {
        return networkService.getNetworkConfiguration();
    }

    @Override
    public List<Collection> retrieveHashCollectionsByTopic(String topic) {
        return hashCollectionService.findAllByTopic(topic);
    }

    @Override
    public List<CertificateRequest> findAllCertificateRequests() {
        return networkService.findAllCertificateRequests();
    }

    @Override
    public NetworkConfiguration fetchNetworkConfigurationAndConnect(String host) throws TargetNodeUnreachableException {
        NetworkConfiguration networkConfiguration = networkService.fetchNetworkConfigurationAndSave(host);
        if (networkConfiguration != null) {
            networkService.certificateRequest(networkConfiguration.getOrigin(), localNodeService.get());
        }
        return networkConfiguration;
    }

    @Override
    public NetworkConfiguration fetchNetworkConfiguration(String host) throws TargetNodeUnreachableException {
        NetworkConfiguration networkConfiguration = networkService.fetchNetworkConfiguration(host);
        return networkConfiguration;
    }

    @Override
    public void clearNetworkConfiguration() {
        networkService.saveNetworkConfiguration(null);
    }

    @Override
    public void enableHashCollectionById(String s) {
        Collection collection = findHashCollectionById(s).orElse(null);
        if (collection != null) {
            collection.setEnabled(true);
            hashCollectionService.save(collection);
        }
    }

    @Override
    public void disableHashCollectionById(String s) {
        Collection collection = findHashCollectionById(s).orElse(null);
        if (collection != null) {
            collection.setEnabled(false);
            hashCollectionService.save(collection);
        }
    }

    @Override
    public void removeHashCollectionById(String s) {
        Collection collection = findHashCollectionById(s).orElse(null);
        if (collection != null) {
            hashCollectionService.delete(collection);
        }
    }
}
