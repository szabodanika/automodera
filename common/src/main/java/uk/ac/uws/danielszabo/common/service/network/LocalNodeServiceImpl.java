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
import uk.ac.uws.danielszabo.common.event.LocalNodeUpdatedEvent;
import uk.ac.uws.danielszabo.common.model.network.node.LocalNode;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.common.repository.LocalNodeRepository;

import java.util.Optional;

@Slf4j
@Service
public class LocalNodeServiceImpl implements LocalNodeService {

  private final ApplicationEventPublisher applicationEventPublisher;

  private final LocalNodeRepository localNodeRepository;

  private final NodeFactory nodeFactory;

  private Node localNode;

  public LocalNodeServiceImpl(
      ApplicationEventPublisher applicationEventPublisher,
      LocalNodeRepository localNodeRepository,
      NodeFactory nodeFactory) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.localNodeRepository = localNodeRepository;
    this.nodeFactory = nodeFactory;
  }

  @Override
  public Node get() {
    if (this.localNode == null) {
      Optional<LocalNode> optionalNode = localNodeRepository.get();
      optionalNode.ifPresent(node -> this.localNode = node.getLocal());
    }
    return this.localNode;
  }

  @Override
  public Node set(Node self) {
    if (self == null) {
      log.error("Cannot overwrite local config with null.");
      return null;
    } else {
      LocalNodeUpdatedEvent event = new LocalNodeUpdatedEvent(this, self);
      applicationEventPublisher.publishEvent(event);
      return this.localNode = localNodeRepository.save(new LocalNode(self)).getLocal();
    }
  }

  @Override
  public Node set() {
    return set(localNode);
  }

  @Override
  public Node init(
      String id,
      NodeType nodeType,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      String addressLine2,
      String postCode,
      String country) {
    Node node;
    switch (nodeType) {
      case INTEGRATOR -> node =
          nodeFactory.getIntegratorNode(
              id,
              name,
              domain,
              legalName,
              adminEmail,
              addressLine1,
              addressLine2,
              postCode,
              country);
      case ARCHIVE -> node =
          nodeFactory.getArchiveNode(
              id,
              name,
              domain,
              legalName,
              adminEmail,
              addressLine1,
              addressLine2,
              postCode,
              country);
      case OPERATOR -> node =
          nodeFactory.getOperatorNode(
              id,
              name,
              domain,
              legalName,
              adminEmail,
              addressLine1,
              addressLine2,
              postCode,
              country);
      case ORIGIN -> node =
          nodeFactory.getOriginNode(
              id,
              name,
              domain,
              legalName,
              adminEmail,
              addressLine1,
              addressLine2,
              postCode,
              country);
      default -> throw new IllegalStateException("Unexpected value: " + nodeType);
    }
    set(node);
    return node;
  }
}
