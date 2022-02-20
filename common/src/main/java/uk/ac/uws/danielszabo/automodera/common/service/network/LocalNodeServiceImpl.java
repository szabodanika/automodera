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
import uk.ac.uws.danielszabo.automodera.common.event.LocalNodeUpdatedEvent;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.LocalNode;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.repository.LocalNodeRepository;
import uk.ac.uws.danielszabo.automodera.common.repository.NodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LocalNodeServiceImpl implements LocalNodeService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final LocalNodeRepository localNodeRepository;

    private final NodeRepository nodeRepository;

    private final NodeFactory nodeFactory;

    public LocalNodeServiceImpl(
            ApplicationEventPublisher applicationEventPublisher,
            LocalNodeRepository localNodeRepository,
            NodeRepository nodeRepository,
            NodeFactory nodeFactory) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.localNodeRepository = localNodeRepository;
        this.nodeRepository = nodeRepository;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Node get() {
        Optional<LocalNode> optionalNode = localNodeRepository.get();
        if (optionalNode.isEmpty()) {
            return null;
        } else {
            Node node = optionalNode.get().getLocal();
            node.setOnline(true);
            return node;
        }
    }

    @Override
    public Node set(Node self) {
        if (self == null) {
            log.error("Cannot overwrite local config with null.");
            return null;
        } else {
            LocalNodeUpdatedEvent event = new LocalNodeUpdatedEvent(this, self);
            if (localNodeRepository.get().isEmpty()) {
                localNodeRepository.save(new LocalNode(self));
            }
            applicationEventPublisher.publishEvent(event);
            return nodeRepository.save(self);
        }
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


    @Override
    public void addSubscription(String topic) {
        Node node = get();

        // need to wrap list in arraylist because spring will
        // return something that does not allow operations
        List<String> subscriptionList = new ArrayList<>(node.getSubscriptions());
        subscriptionList.add(topic);
        node.setSubscriptions(subscriptionList);

        set(node);
    }


    @Override
    public void removeSubscription(String topic) {
        Node node = get();

        // need to wrap list in arraylist because spring will
        // return something that does not allow operations
        List<String> subscriptionList = new ArrayList<>(node.getSubscriptions());
        subscriptionList.remove(topic);
        node.setSubscriptions(subscriptionList);

        set(node);
    }

    @Override
    public List<String> getSubscriptions(String topic) {
        return get().getSubscriptions();
    }
}
