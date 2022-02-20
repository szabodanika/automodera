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

import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;

import java.util.List;

public interface LocalNodeService {

    Node get();

    Node set(Node self);

    Node init(
            String id,
            NodeType nodeType,
            String name,
            String domain,
            String legalName,
            String adminEmail,
            String addressLine1,
            String addressLine2,
            String postCode,
            String country);
    /**
     * This method will add the subscription
     * and persist the changes on the local node
     *
     * @param topic
     */
    void addSubscription(String topic);

    /**
     * This method will add the subscription
     * and persist the changes on the local node
     *
     * @param topic
     */
    void removeSubscription(String topic);

    List<String> getSubscriptions(String topic);
}
