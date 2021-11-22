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
import uk.ac.uws.danielszabo.common.model.network.node.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Slf4j
@Service
public class LocalNodeServiceImpl implements LocalNodeService {

  private static final String CONFIG_XML_PATH = "./data/netconf.xml";

  private final ApplicationEventPublisher applicationEventPublisher;

  private Node localNode;

  public LocalNodeServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  private void setLocalNode(Node localNode) {
    LocalNodeUpdatedEvent event = new LocalNodeUpdatedEvent(this, localNode);
    applicationEventPublisher.publishEvent(event);
    this.localNode = localNode;
  }

  @Override
  public Node getLocalNode() {
    if (this.localNode == null) {
      try {
        File file = new File(CONFIG_XML_PATH);
        JAXBContext jaxbContext = JAXBContext.newInstance(Node.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        this.setLocalNode((Node) unmarshaller.unmarshal(file));
        log.info("Successfully loaded " + CONFIG_XML_PATH);
        return this.localNode;
      } catch (Exception e) {
        log.error("Failed to load " + CONFIG_XML_PATH + " " + e.getMessage());
        return null;
      }
    } else return this.localNode;
  }

  @Override
  public Node saveLocalNode(Node self) {
    if (self == null) {
      log.warn("Cannot overwrite local config with null.");
      return null;
    } else {
      try {
        JAXBContext jaxbContext = null;
        jaxbContext = JAXBContext.newInstance(Node.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(self, new File(CONFIG_XML_PATH));
        this.setLocalNode(self);
        log.info("Saved " + CONFIG_XML_PATH);
        return self;
      } catch (Exception e) {
        log.error(
            "Failed to save "
                + CONFIG_XML_PATH
                + ": Message: "
                + e.getMessage()
                + " Cause: "
                + e.getCause());
        return null;
      }
    }
  }

  @Override
  public Node saveLocalNode() {
    return saveLocalNode(localNode);
  }
}
