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

package uk.ac.uws.danielszabo.hashnet.integrator.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.cli.BaseNodeCLI;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.network.*;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;
import uk.ac.uws.danielszabo.hashnet.integrator.model.HashReport;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Import({
  RestServiceImpl.class,
  HashServiceImpl.class,
  LocalNodeServiceImpl.class,
  NetworkServiceImpl.class,
  SubscriptionServiceImpl.class
})
@Slf4j
@ShellComponent
public class IntegratorCLI extends BaseNodeCLI {

  private final IntegratorServiceFacade integratorServiceFacade;

  public IntegratorCLI(
      LocalNodeService localNodeService,
      NetworkService networkService,
      IntegratorServiceFacade integratorServiceFacade) {
    super(localNodeService, networkService);
    this.integratorServiceFacade = integratorServiceFacade;
  }

  // for example:
  @ShellMethod("Manage Subscriptions")
  public void subs(
      @ShellOption(defaultValue = "false") boolean list,
      @ShellOption(defaultValue = "false") boolean add,
      @ShellOption(defaultValue = "false") boolean remove,
      @ShellOption(defaultValue = "") String topic) {

    if (list) {
      log.info("Subscriptions");
      List<Subscription> hashCollections;
      try {
        if (!(hashCollections = integratorServiceFacade.getSubscriptions()).isEmpty()) {
          for (Subscription s : hashCollections) {
            // TODO make this a little nicer
            log.info(s.toString());
          }
        } else {
          log.info("No subscription found");
        }
      } catch (Exception e) {
        log.error("Failed to retrieve subscriptions");
        e.printStackTrace();
      }
    } else if (add) {
      if (!topic.isBlank()) {
        try {
          integratorServiceFacade.addSubscription(topic);
        } catch (Exception e) {
          log.error("Failed to communicate subscription with archives");
          e.printStackTrace();
        }
      } else {
        log.error("Please specify non-empty topic ID");
      }
    } else if (remove) {
      if (!topic.isBlank()) {
        integratorServiceFacade.removeSubscriptionByTopic(topic);
        log.info("Successfully unsubscribed from ");
      }
    }
  }

  @ShellMethod("Manage Hash Collections")
  public void hash(
      @ShellOption(defaultValue = "false") boolean list,
      @ShellOption(defaultValue = "false") boolean show,
      @ShellOption(defaultValue = "sync") boolean sync,
      @ShellOption(defaultValue = "") String id) {

    if (list) {
      List<HashCollection> hashCollections;
      if (!(hashCollections = integratorServiceFacade.findAllHashCollections()).isEmpty()) {
        for (HashCollection hc : hashCollections) {
          // TODO make this a little nicer
          log.info(hc.toString());
        }
      } else {
        log.info("No hash collections found");
      }

    } else if (show) {
      if (id.isBlank()) {
        log.error("Please specify non-empty id");
        return;
      }
      integratorServiceFacade.findHashCollectionById(id).ifPresent(h -> log.info(h.toString()));
    } else if (sync) {
      try {
        integratorServiceFacade.updateHashCollections();
      } catch (Exception e) {
        log.error("Failed to update hash collections");
        e.printStackTrace();
      }
    }
  }

  @ShellMethod("Manage Hash Collections")
  public void check(String image, @ShellOption(defaultValue = "false") boolean xml) {
    try {
      if (xml) {
        Marshaller marshallerObj = JAXBContext.newInstance(HashReport.class).createMarshaller();
        StringWriter sw = new StringWriter();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshallerObj.marshal(integratorServiceFacade.checkImage(image), sw);

        System.out.println(sw);

      } else {
        log.info(integratorServiceFacade.checkImage(image).toString());
      }
    } catch (IOException | JAXBException e) {
      e.printStackTrace();
    }
  }

  @ShellMethod("Lists current node info of every archive")
  public void archives() {
    log.info("Archives");
    List<Node> archiveList;
    if (!(archiveList = integratorServiceFacade.getAllArchives()).isEmpty()) {
      for (Node n : archiveList) {
        // TODO make this a little nicer
        log.info(n.toString());
      }
    } else {
      log.info("No archives found");
    }
  }
}
