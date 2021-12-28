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
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.image.TopicService;
import uk.ac.uws.danielszabo.common.service.network.*;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import java.util.List;
import java.util.Optional;

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
      IntegratorServiceFacade integratorServiceFacade,
      SubscriptionService subscriptionService,
      TopicService topicService) {
    super(localNodeService, networkService, topicService);
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
      if (!(hashCollections = integratorServiceFacade.getSubscriptions()).isEmpty()) {
        for (Subscription s : hashCollections) {
          // TODO make this a little nicer
          log.info(s.toString());
        }
      } else {
        log.info("No subscription found");
      }
    } else if (add) {
      if (!topic.isBlank()) {
        Optional<Topic> optionalTopic = null;
        try {
          optionalTopic = integratorServiceFacade.findTopicById(topic);
        } catch (Exception e) {
          log.error("Failed to retrieve topic " + topic + ": " + e.getMessage());
          return;
        }
        if (optionalTopic.isPresent()) {
          integratorServiceFacade.addSubscription(optionalTopic.get());
        } else {
          log.error("Topic not found");
        }
      } else {
        log.error("Please specify non-empty topic ID");
      }
    }
    //    else if (remove) {
    //      if (!id.isBlank()) {
    //        if (integratorServiceFacade.removeSubscriptionByArchiveId(id)) {
    //          log.info("Successfully unsubscribed from ");
    //        }
    //
    //      } else if (!host.isBlank()) {
    //        Optional<Node> optionalArchive = integratorServiceFacade.retrieveNodeByHost(host);
    //        if (optionalArchive.isPresent()) {
    //          integratorServiceFacade.addSubscription(optionalArchive.get());
    //        } else {
    //          log.error("Failed to reach node " + host);
    //        }
    //      } else {
    //        log.error("Please specify non-empty id or host");
    //      }
    //    }
  }

  @ShellMethod("Manage Hash Collections")
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
