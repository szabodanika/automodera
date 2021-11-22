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

package uk.ac.uws.danielszabo.common.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;
import uk.ac.uws.danielszabo.common.event.LocalNodeUpdatedEvent;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfig;
import uk.ac.uws.danielszabo.common.model.network.node.Node;

@Component
public class CustomPromptProvider implements PromptProvider {

    private Node localNode;

    private final NetworkConfig networkConfig;

    public CustomPromptProvider(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    @Override
    public AttributedString getPrompt() {

        String nodeId = localNode == null ? "unknown" : localNode.getId();
        String networkId = networkConfig == null ? "unknown" : networkConfig.getName();
        return new AttributedString(nodeId + "@" + networkId + " > ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    }

    @EventListener
    public void handle(LocalNodeUpdatedEvent event) {
        this.localNode = event.getLocalNode();
    }
}