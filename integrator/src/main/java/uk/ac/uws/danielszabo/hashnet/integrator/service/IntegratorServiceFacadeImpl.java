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

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Image;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.common.service.network.SubscriptionService;
import uk.ac.uws.danielszabo.hashnet.integrator.IntegratorServer;
import uk.ac.uws.danielszabo.hashnet.integrator.model.HashReport;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IntegratorServiceFacadeImpl implements IntegratorServiceFacade {

    private final HashService hashService;

    private final LocalNodeService localNodeService;

    private final NetworkService networkService;

    private final SubscriptionService subscriptionService;

    private final HashCollectionService hashCollectionService;


    public IntegratorServiceFacadeImpl(
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
    public List<HashCollection> retrieveHashCollectionByTopic(String string) {
        return hashCollectionService.findAllByTopic(string);
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
    public List<HashCollection> findAllHashCollections() {
        return hashCollectionService.findAll();
    }

    @Override
    public Optional<HashCollection> findHashCollectionById(String id) {
        return hashCollectionService.findById(id);
    }

    @Override
    public boolean checkCertificate(NodeCertificate certificate, String remoteAddr) {
        return networkService.checkCertificate(certificate, remoteAddr);
    }

    @Transactional
    @Override
    public void addSubscription(String topic) {

        // sending subscription to every archive that has hash collections in that topic

        // for each archive
        for (Node archive : getAllArchives()) {
            try {
                // request info of all their hash collections (without hashes)
                List<HashCollection> hashCollectionList =
                        networkService.requestAllHashCollectionsByArchive(archive);
                // for each hash collection
                for (HashCollection hc : hashCollectionList) {
                    // if it is tagged with the given topic
                    if (hc.getTopicList().contains(topic)) {

                        // let the archive know that we want to subscribe
                        Subscription subscription =
                                new Subscription(archive.getId(),
                                        localNodeService.get().getId(),
                                        topic);
                        // TODO later on wait for archive to accept subscription request maybe
                        subscriptionService.save(subscription);
                        networkService.sendSubscription(archive, topic);
                    }
                }
            } catch (TargetNodeUnreachableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Node> getAllArchives() {
        return networkService.getAllArchives();
    }

    @Override
    public HashCollection downloadHashCollection(String host, String id) throws TargetNodeUnreachableException {
        return networkService.downloadHashCollection(host, id);
    }

    @Override
    public void removeSubscriptionByTopic(String topic) {
        subscriptionService.removeByTopic(topic);
    }

    @Transactional
    @Override
    public HashReport checkImage(String image) throws IOException {

        // calculate hash for the input image
        Hash hashInput = hashService.pHash(new File(image));

        Image highestMatch = null;
        double highestMatchScore = 0;
        List<String> stringList = new ArrayList<>();

        // for each registered hash collection (i.e. ones we are subscribed to)
        for (HashCollection hc : hashCollectionService.findAll()) {
            // for each image in the collection
            for (Image i : hc.getImageList()) {
                // calculate a similarity score between the hashes
                double score =
                        hashService.simScore(
                                hashInput, new Hash(i.getHash(), 32, new PerceptiveHash(32).algorithmId()));
                // store the image, hash collection and similarity score
                if (highestMatch == null) highestMatch = i;
                else if (score > highestMatchScore) {
                    highestMatch = i;
                    highestMatchScore = score;
                }
            }
        }

        // load hashcollection
        Hibernate.initialize(highestMatch.getHashCollection());
        Hibernate.initialize(highestMatch.getHashCollection().getTopicList());

        return new HashReport(
                highestMatch, highestMatchScore, highestMatch.getHashCollection().getTopicList());
    }

    @Override
    public void updateHashCollections() throws Exception {
        for (Subscription subscription : subscriptionService.getSubscriptions()) {
            List<HashCollection> hashCollectionList =
                    networkService.requestAllHashCollectionsByArchive(subscription.getPublisher());
            for (HashCollection hashCollection : hashCollectionList) {
                networkService.downloadHashCollection(
                        subscription.getPublisherId(), hashCollection.getId());
            }
        }
    }

    @Override
    public Optional<Node> retrieveNodeByHost(String host) throws Exception {
        return Optional.of(networkService.getNodeByHost(host));
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
        IntegratorServer.exit();
    }

    @Override
    public void init(String id, String displayName, String domainName, String legalName, String adminEmail, String addressLine1, String addressLine2, String postCode, String country) {
        localNodeService.init(id, NodeType.ARCHIVE, displayName, domainName, legalName, adminEmail, addressLine1, addressLine2, postCode, country);
    }

    @Override
    public List<HashCollection> retrieveHashCollectionsByArchive(Node n) {
        if (n.equals(getLocalNode())) {
            return findAllHashCollections();
        } else {
            try {
                return networkService.requestAllHashCollectionsByArchive(n);
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
    public List<HashCollection> retrieveHashCollectionsByTopic(String topic) {
        return hashCollectionService.findAllByTopic(topic);
    }

    @Override
    public List<CertificateRequest> findAllCertificateRequests() {
        return networkService.findAllCertificateRequests();
    }

    @Override
    public void connectToNetwork(String host) throws Exception {
        networkService.getNetworkConfigurationFromOrigin(host);
        networkService.certificateRequest(networkService.getNetworkConfiguration().getOrigin(), localNodeService.get());
    }

    @Override
    public Optional<Node> findKnownNodeById(String id) {
        return networkService.findKnownNodeById(id);
    }

    @Override
    public List<HashCollection> retrieveDownloadedHashCollections() {
        return hashCollectionService.findAllDownloaded();
    }
}
