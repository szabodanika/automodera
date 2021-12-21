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

import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.repository.SubscriptionRepository;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;

  public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @Override
  public List<Subscription> getSubscriptions() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Subscription save(Subscription subscription) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Subscription subscription) {
    throw new UnsupportedOperationException();
  }
}