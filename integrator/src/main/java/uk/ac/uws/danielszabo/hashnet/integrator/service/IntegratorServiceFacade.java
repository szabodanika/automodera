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
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.hashnet.integrator.model.HashReport;

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

    List<HashCollection> retrieveHashCollectionByTopic(String string);

    List<HashCollection> retrieveHashCollectionByArchive(Node topic);

    List<Node> retrieveAllNodes();

    Optional<Node> retrieveNodeById(String id);

    Node saveNode(Node node);

    void deleteNode(Node node);

    List<Subscription> getSubscriptions();

    void saveCertificate(NodeCertificate certificate);

    List<HashCollection> findAllHashCollections();

    Optional<HashCollection> findHashCollectionById(String id);

    boolean checkCertificate(NodeCertificate certificate, String remoteAddr);

    void updateHashCollections() throws Exception;

    Optional<Node> retrieveNodeByHost(String host) throws Exception;

    void addSubscription(String string);

    List<Node> getAllArchives();

    HashCollection downloadHashCollection(String host, String id) throws Exception;

    void removeSubscriptionByTopic(String topic);

    HashReport checkImage(String image) throws IOException;

    Node getLocalNode();

    void shutDown();

    void init(String id, String displayName, String domainName, String legalName, String adminEmail, String addressLine1, String addressLine2, String postCode, String country);

    List<HashCollection> retrieveHashCollectionsByArchive(Node n) throws Exception;

    NetworkConfiguration getNetworkConfiguration();

    List<HashCollection> retrieveHashCollectionsByTopic(String topic) throws Exception;

    List<CertificateRequest> findAllCertificateRequests();

    void connectToNetwork(String host) throws Exception;

    Optional<Node> findKnownNodeById(String nodeId);

    List<HashCollection> retrieveDownloadedHashCollections();
}
