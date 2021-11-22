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
import uk.ac.uws.danielszabo.common.model.images.HashCollection;
import uk.ac.uws.danielszabo.common.model.images.Topic;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.archive.repository.HashCollectionRepository;
import uk.ac.uws.danielszabo.hashnet.archive.repository.NodeCertificateRepository;
import uk.ac.uws.danielszabo.hashnet.archive.repository.NodeRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArchiveServiceImpl implements ArchiveService {

  private final NodeCertificateRepository nodeCertificateRepository;

  private final NodeRepository nodeRepository;

  private final HashCollectionRepository hashCollectionRepository;

  public ArchiveServiceImpl(
      NodeCertificateRepository nodeCertificateRepository,
      NodeRepository nodeRepository,
      HashCollectionRepository hashCollectionRepository) {
    this.nodeCertificateRepository = nodeCertificateRepository;
    this.nodeRepository = nodeRepository;
    this.hashCollectionRepository = hashCollectionRepository;
  }

  @Override
  public List<NodeCertificate> retrieveAllCertificates() {
    return nodeCertificateRepository.findAll();
  }

  @Override
  public NodeCertificate createCertificate(Node node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean reissueCertificateForNode(Node node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean revokeCertificateForNode(Node node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean verifyCertificate(NodeCertificate certificate) {
    throw new UnsupportedOperationException();
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
