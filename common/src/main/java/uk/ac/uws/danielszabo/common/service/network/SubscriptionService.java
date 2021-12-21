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

import uk.ac.uws.danielszabo.common.model.network.node.Subscription;

import java.util.List;

// on an archive node, this will handle the nodes that are subscribed to the local node
// on an integrator node, this will handle the archive nodes that the local node is subscribed to
public interface SubscriptionService {

  List<Subscription> getSubscriptions();

  Subscription save(Subscription subscription);

  boolean remove(Subscription subscription);
}
