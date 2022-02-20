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

package uk.ac.uws.danielszabo.automodera.archive.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.automodera.archive.service.ArchiveServiceFacade;
import uk.ac.uws.danielszabo.automodera.common.cli.BaseNodeCLI;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.automodera.common.service.network.*;
import uk.ac.uws.danielszabo.automodera.common.service.rest.RestServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Import({
  RestServiceImpl.class,
  HashServiceImpl.class,
  LocalNodeServiceImpl.class,
  NetworkServiceImpl.class
})
@Slf4j
@ShellComponent
public class ArchiveCLI extends BaseNodeCLI {

  private final ArchiveServiceFacade archiveServiceFacade;

  public ArchiveCLI(
      LocalNodeService localNodeService,
      NetworkService networkService,
      ArchiveServiceFacade archiveServiceFacade) {
    super(localNodeService, networkService);
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
      List<Collection> collections;
      if (!(collections = archiveServiceFacade.findAllHashCollections()).isEmpty()) {
        for (Collection hc : collections) {
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
      List<String> topicList = new ArrayList<>();

      // parse topics
      for (String s : topics.split(",")) {
        topicList.add(s);
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

  //  // for example:
  //  @ShellMethod("Manage Subscriptions")
  //  public void subs(
  //      @ShellOption(defaultValue = "false") boolean list,
  //      @ShellOption(defaultValue = "false") boolean sync) {
  //
  //    if (list) {
  //      log.info("Subscriptions");
  //      List<Strin> hashCollections;
  //      try {
  //        if (!(hashCollections = archiveServiceFacade.getSubscriptions()).isEmpty()) {
  //          for (Subscription s : hashCollections) {
  //            // TODO make this a little nicer
  //            log.info(s.toString());
  //          }
  //        } else {
  //          log.info("No subscription found");
  //        }
  //      } catch (Exception e) {
  //        log.error("Failed to retrieve subscriptions");
  //        e.printStackTrace();
  //      }
  //    } else if (sync) {
  //      try {
  //        log.info("Syncing hash collections...");
  //        archiveServiceFacade.sendCollectionRepertoireToAllIntegrators();
  //      } catch (Exception e) {
  //        log.error("Failed to sync hash collections");
  //        e.printStackTrace();
  //      }
  //    }
  //  }
}
