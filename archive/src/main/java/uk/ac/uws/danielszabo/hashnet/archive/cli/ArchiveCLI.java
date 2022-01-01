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

package uk.ac.uws.danielszabo.hashnet.archive.cli;

import jline.internal.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.cli.BaseNodeCLI;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.image.TopicService;
import uk.ac.uws.danielszabo.common.service.network.*;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;
import uk.ac.uws.danielszabo.hashnet.archive.service.ArchiveServiceFacade;

import java.io.IOException;
import java.util.ArrayList;
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
public class ArchiveCLI extends BaseNodeCLI {

  private final ArchiveServiceFacade archiveServiceFacade;

  public ArchiveCLI(
      LocalNodeService localNodeService,
      NetworkService networkService,
      ArchiveServiceFacade archiveServiceFacade,
      SubscriptionService subscriptionService,
      TopicService topicService) {
    super(localNodeService, networkService, topicService);
    this.archiveServiceFacade = archiveServiceFacade;
  }

  /* for example:
  "hash --path /images --id collection1 --name "First Hash Collection" --description "This is just a test" --topics "topic1,topic2,topic3" --force-recalc"
  */
  @ShellMethod("Manage Hash Collections")
  public void hash(
      @ShellOption(defaultValue = "false") boolean list,
      @ShellOption(defaultValue = "false") boolean show,
      @ShellOption(defaultValue = "") String path,
      @ShellOption(defaultValue = "") String id,
      @ShellOption(defaultValue = "") String name,
      @ShellOption(defaultValue = "") String description,
      @ShellOption(defaultValue = "") String topics,
      @ShellOption(defaultValue = "false") boolean forceRecalc) {

    if (list) {
      List<HashCollection> hashCollections;
      if (!(hashCollections = archiveServiceFacade.findAllHashCollections()).isEmpty()) {
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
      archiveServiceFacade.findHashCollectionById(id).ifPresent(h -> log.info(h.toString()));
    } else {

      if (path.isBlank()
          || id.isBlank()
          || name.isBlank()
          || description.isBlank()
          || topics.isBlank()) {
        log.error("Please specify non-empty path, id, name, description and topic list");
        return;
      }

      log.info("Hashing images on path: " + path);
      List<Topic> topicList = new ArrayList<>();

      // parse topics
      Topic t;
      for (String s : topics.split(",")) {
        t = archiveServiceFacade.findTopicById(s);
        if (t == null) {
          // if topic is not found, we do not create the collection
          Log.error("Topic " + s + " does not exist. Please create it before proceeding.");
          return;
        }
        topicList.add(t);
      }

      // now try to generate collection
      // no need to log anything here, the generateHashCollection method will do it
      try {
        archiveServiceFacade.generateHashCollection(
            path, id, name, description, topicList, forceRecalc);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // for example:
  @ShellMethod("Manage Subscriptions")
  public void subs(
      @ShellOption(defaultValue = "false") boolean list,
      @ShellOption(defaultValue = "false") boolean sync) {

    if (list) {
      log.info("Subscriptions");
      List<Subscription> hashCollections;
      try {
        if (!(hashCollections = archiveServiceFacade.getSubscriptions()).isEmpty()) {
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
    } else if (sync) {
      try {
        archiveServiceFacade.syncAllHashCollections();
      } catch (Exception e) {
        log.error("Failed to sync hash collections");
        e.printStackTrace();
      }
    }
  }
}
