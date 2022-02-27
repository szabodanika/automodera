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

package uk.ac.uws.danielszabo.automodera.integrator.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.automodera.common.constants.Topic;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Image;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.automodera.common.service.image.HashCollectionService;
import uk.ac.uws.danielszabo.automodera.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.automodera.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.automodera.integrator.IntegratorServer;
import uk.ac.uws.danielszabo.automodera.integrator.model.Report;
import uk.ac.uws.danielszabo.automodera.integrator.model.IntegrationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class IntegratorServiceFacadeImpl implements IntegratorServiceFacade {

	private final HashService hashService;

	private final LocalNodeService localNodeService;

	private final NetworkService networkService;

	private final HashCollectionService collectionService;

	private final IntegrationContext integrationContext;

	private final ReportService reportService;

	public IntegratorServiceFacadeImpl(
		HashService hashService,
		LocalNodeService localNodeService,
		NetworkService networkService,
		HashCollectionService collectionService,
		IntegrationContext integrationContext,
		ReportService reportService) {
		this.hashService = hashService;
		this.localNodeService = localNodeService;
		this.networkService = networkService;
		this.collectionService = collectionService;
		this.integrationContext = integrationContext;
		this.reportService = reportService;
	}

	@Override
	public boolean verifyCertificate(NodeCertificate certificate) {
		Optional<NodeCertificate> localCert = networkService.getCertificateById((certificate.getId()));
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
	public List<Collection> retrieveAllHashCollections() {
		return collectionService.findAll();
	}

	@Override
	public Optional<Collection> retrieveHashCollectionById(String id) {
		return collectionService.findById(id);
	}

	@Override
	public List<Collection> retrieveHashCollectionByTopic(String string) {
		return collectionService.findAllByTopic(string);
	}

	@Override
	public List<Collection> retrieveHashCollectionByArchive(Node topic) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Node> getAllKnownNodes() {
		return networkService.getAllKnownNodes();
	}

	@Override
	public Optional<Node> getKnownNodeById(String id) {
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
	public List<String> getSubscriptions() {
		return localNodeService.get().getSubscriptions();
	}

	@Override
	public void saveCertificate(NodeCertificate certificate) {
		Node localNode = localNodeService.get();
		certificate.setNode(localNode);
		localNode.setCertificate(certificate);
		localNodeService.set(localNode);
	}

	@Override
	public List<Collection> findAllHashCollections() {
		return collectionService.findAll();
	}

	@Override
	public Optional<Collection> findCollectionById(String id) {
		return collectionService.findById(id);
	}

	@Override
	public boolean checkCertificate(NodeCertificate certificate, String remoteAddr) {
		return networkService.verifyCertificate(certificate, remoteAddr);
	}

	@Transactional
	@Override
	public void addSubscription(String topic) {
		localNodeService.addSubscription(topic);
	}

	@Override
	public List<Node> getAllArchives() {
		return networkService.getAllArchives();
	}

	@Override
	public Collection downloadHashCollection(String host, String id)
		throws TargetNodeUnreachableException {
		return networkService.downloadCollection(host, id);
	}

	@Override
	public void removeSubscription(String topic) {
		localNodeService.removeSubscription(topic);
	}

	@Override
	public Report checkImage(String image, String attachment, String source) throws IOException {

		// calculate hash for the input image
		String hashInput = hashService.pHash(new File(image));

		Image highestMatch = null;
		double highestMatchScore = 0;
		List<String> stringList = new ArrayList<>();

		// for each registered hash collection (i.e. ones we are subscribed to)
//    for (Collection hc : collectionService.findAll()) {
//      // for each image in the collection
//      for (Image i : hc.getImageList()) {
//        // calculate a similarity score between the hashes
//        double score = hashService.simScore(hashInput, i.getHash().toString());
//        // store the image, hash collection and similarity score
//        if (highestMatch == null) highestMatch = i;
//        else if (score > highestMatchScore) {
//          highestMatch = i;
//          highestMatchScore = score;
//        }
//      }
//    }

		for (Collection hc : collectionService.findAll()) {
			// for each image in the collection
			for (Image i : hc.getImageList()) {
				// calculate a similarity score between the hashes
				double score = hashService.getSimilarityToFileWithHash(image, i.getHash());
				// store the image, hash collection and similarity score
				if (highestMatch == null) highestMatch = i;
				else if (score > highestMatchScore) {
					highestMatch = i;
					highestMatchScore = score;
				}
			}
		}



		Report report;
		if (highestMatch == null) {
			report = new Report(null, 0, new ArrayList<>(), attachment, source);
		} else {

			// load collection
			Hibernate.initialize(highestMatch.getCollection());

			Hibernate.initialize(highestMatch.getCollection().getTopicList());

			report =
				new Report(
					highestMatch,
					highestMatchScore,
					highestMatch.getCollection().getTopicList(),
					attachment,
					source);
		}

		return reportService.save(report);
	}

	@Override
	public void requestAllCollectionRepertoires() {
		List<Node> archiveList = networkService.getAllArchives();
		for (Node archive : archiveList) {
			try {
				List<Collection> collectionRepertoire = requestCollectionRepertoireFromArchive(archive);

				processCollectionRepertoire(new CollectionRepertoire(collectionRepertoire));

			} catch (TargetNodeUnreachableException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Collection> requestCollectionRepertoireFromArchive(Node archive)
		throws TargetNodeUnreachableException {
		return networkService.requestCollectionRepertoireFromArchive(archive);
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
			NodeType.INTEGRATOR,
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
	public List<Collection> requestCollectionRepertoireFormArchive(Node n) {
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
	public List<Collection> getCollecitonsByTopic(String topic) {
		return collectionService.findAllByTopic(topic);
	}

	@Override
	public List<CertificateRequest> getAllCertificateRequests() {
		return networkService.findAllCertificateRequests();
	}

	@Override
	public void connectToNetwork(String host) throws Exception {
		networkService.fetchNetworkConfigurationAndSave(host);
		networkService.certificateRequest(
			networkService.getNetworkConfiguration().getOrigin(), localNodeService.get());
	}

	@Override
	public List<Collection> retrieveDownloadedHashCollections() {
		return collectionService.getAllDownloaded();
	}

	@Override
	public List<String> getAllTopics() {
		Set<String> topicList = new HashSet<>();

		for (Node archive : getAllArchives()) {
			try {
				List<Collection> collectionList =
					networkService.requestCollectionRepertoireFromArchive(archive);
				for (Collection hc : collectionList) {
					topicList.addAll(hc.getTopicList());
				}
			} catch (TargetNodeUnreachableException e) {
				e.printStackTrace();
			}
		}
		return topicList.stream().toList();
	}

	@Override
	public Map<String, Map<String, Object>> getAllTopicsWithCollections() {

		Map<String, Map<String, Object>> topics = new HashMap();

		// add all the default topics first
		Map topicMap;
		for (Topic t : Topic.values()) {
			topicMap = new HashMap<>();
			topicMap.put("collections", new ArrayList<>());
			topicMap.put("isSubscribed", localNodeService.get().getSubscriptions().contains(t.getId()));
			topicMap.put("displayName", t.getDisplayName());
			topicMap.put("description", t.getDescription());
			topicMap.put("category", t.getCategory());
			topics.put(t.getId(), topicMap);
		}

		for (Node archive : getAllArchives()) {
			try {
				List<Collection> collectionList =
					networkService.requestCollectionRepertoireFromArchive(archive);
				for (Collection hc : collectionList) {
					for (String t : hc.getTopicList()) {
						if (topics.containsKey(t)) {
							topicMap = topics.get(t);
							List<String> topicHashCollectionIdList = (List<String>) topicMap.get("collections");
							topicHashCollectionIdList.add(hc.getId());
						} else {
							topicMap = new HashMap<>();
							topicMap.put("collections", new ArrayList<>());
							List<String> topicHashCollectionIdList = (List<String>) topicMap.get("collections");
							topicHashCollectionIdList.add(hc.getId());
							topicMap.put("isSubscribed", localNodeService.get().getSubscriptions().contains(t));
							topicMap.put("displayName", t);
							topicMap.put("description", "Custom topic");
							topicMap.put("category", "Not categorised");
							topics.put(t, topicMap);
						}
					}
				}
			} catch (TargetNodeUnreachableException e) {
				// TODO report on how many nodes were unavailable
				e.printStackTrace();
			}
		}

		return topics;
	}

	@Override
	public boolean isSubscribedToAny(List<String> topicList) {
		return topicList.stream().anyMatch(this::isSubscribedTo);
	}

	@Override
	public boolean isSubscribedTo(String topic) {
		return localNodeService.get().getSubscriptions().contains(topic.toLowerCase());
	}

	@Override
	public void processCollectionRepertoire(CollectionRepertoire collectionRepertoire) {
		// get hash collections from repertoire
		for (Collection collection : collectionRepertoire.getCollectionList()) {
			// we only care if we are subscribed
			if (isSubscribedToAny(collection.getTopicList())) {
				Optional<Collection> localHashCollection = findCollectionById(collection.getId());

				// download if we don't have it yet or it has been updated
				if (!localHashCollection.isPresent()
					|| (localHashCollection.get().getVersion() < collection.getVersion())
					|| localHashCollection.get().getImageList().isEmpty()) {
					try {
						collection.setArchive(getKnownNodeById(collection.getArchiveId()).get());
						if (!localHashCollection.isPresent()) {
							log.info(
								"Downloading collection "
									+ collection.getId()
									+ " version "
									+ collection.getVersion()
									+ "  from "
									+ collection.getArchiveId()
									+ "...");
						} else {
							log.info(
								"Upgrading collection "
									+ collection.getId()
									+ " from version "
									+ localHashCollection.get().getVersion()
									+ " to version "
									+ collection.getVersion()
									+ "  from "
									+ collection.getArchiveId()
									+ "...");
						}
						downloadHashCollection(collection.getArchive().getHost(), collection.getId());
					} catch (Exception e) {
						log.error(
							"Failed to retrieve collection "
								+ collection.getId()
								+ " version "
								+ collection.getVersion()
								+ " from "
								+ collection.getArchiveId());
					}
				} else {
					log.info("Collection " + collection.getId() + " is already up to date.");
				}
			}
		}
	}

	@Override
	public NetworkConfiguration fetchNetworkConfigurationAndConnect(String host)
		throws TargetNodeUnreachableException {
		NetworkConfiguration networkConfiguration =
			networkService.fetchNetworkConfigurationAndSave(host);
		if (networkConfiguration != null) {
			networkService.certificateRequest(networkConfiguration.getOrigin(), localNodeService.get());
		}
		return networkConfiguration;
	}

	@Override
	public NetworkConfiguration fetchNetworkConfiguration(String host)
		throws TargetNodeUnreachableException {
		NetworkConfiguration networkConfiguration = networkService.fetchNetworkConfiguration(host);
		return networkConfiguration;
	}

	@Override
	public void clearNetworkConfiguration() {
		networkService.saveNetworkConfiguration(null);
	}

	@Override
	public IntegrationContext getIntegrationConfiguration() {
		return integrationContext;
	}

	@Override
	public List<Report> getAllReports() {
		return reportService.getAll();
	}
}
