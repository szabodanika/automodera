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

package uk.ac.uws.danielszabo.automodera.common.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;
import uk.ac.uws.danielszabo.automodera.common.event.LocalNodeUpdatedEvent;
import uk.ac.uws.danielszabo.automodera.common.event.NetworkConfigurationUpdatedEvent;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.automodera.common.service.network.NetworkService;

@Component
public class CustomPromptProvider implements PromptProvider {

  private NetworkConfiguration networkConfiguration;
  private Node localNode;

  public CustomPromptProvider(NetworkService networkService, LocalNodeService localNodeService) {
    networkConfiguration = networkService.getNetworkConfiguration();
    localNode = localNodeService.get();
  }

  @Override
  public AttributedString getPrompt() {
    String nodeId = localNode == null ? "unknown" : localNode.getId();
    String networkId = networkConfiguration == null ? "unknown" : networkConfiguration.getName();
    return new AttributedString(
        nodeId + "@" + networkId + " > ",
        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
  }

  @EventListener
  public void handleLocalNodeUpdatedEvent(LocalNodeUpdatedEvent event) {
    this.localNode = event.getLocalNode();
  }

  @EventListener
  public void handleNetworkConfigurationUpdatedEvent(NetworkConfigurationUpdatedEvent event) {
    this.networkConfiguration = event.getNetworkConfiguration();
  }
}
