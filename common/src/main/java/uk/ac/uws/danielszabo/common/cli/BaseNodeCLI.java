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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.image.TopicService;
import uk.ac.uws.danielszabo.common.service.network.*;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;

import java.util.ArrayList;

@Import({
  RestServiceImpl.class,
  HashServiceImpl.class,
  LocalNodeServiceImpl.class,
  NetworkServiceImpl.class,
  CustomPromptProvider.class
})
@Slf4j
@ShellComponent
public abstract class BaseNodeCLI {

  // these services need to be private
  // because the subclasses will be using their own service facades

  // service used to handle local node configuration and certificate
  private final LocalNodeService localNodeService;

  // service used to communicate with the network
  private final NetworkService networkService;

  // service used to handle locally known topics
  private final TopicService topicService;

  protected BaseNodeCLI(
      LocalNodeService localNodeService, NetworkService networkService, TopicService topicService) {
    this.localNodeService = localNodeService;
    this.networkService = networkService;
    this.topicService = topicService;
  }

  // example:
  // connect --origin origin.hashnet.test
  @ShellMethod("Connect to network by addressing origin node.")
  public void connect(String origin) {
    networkService.getNetworkConfigurationFromOrigin(origin);
  }

  @ShellMethod("Load node configuration.")
  public void load() {
    if (localNodeService.get() != null) {
      log.info("Loaded node configuration for " + localNodeService.get().getName());
    } else {
      log.warn("No node configuration available. Use the 'init' command to create one.");
    }
  }

  @ShellMethod("Manage known topics.")
  public void topic(
      @ShellOption(defaultValue = "false") boolean list,
      @ShellOption(defaultValue = "false") boolean create,
      @ShellOption(defaultValue = "false") boolean show,
      @ShellOption(defaultValue = "") String id,
      @ShellOption(defaultValue = "") String name) {
    if (list) {
      for (Topic t : topicService.findAll()) {
        log.info(t.toString());
      }
    } else if (create) {
      if (id.isBlank() || name.isBlank()) {
        log.error("Please specify non-empty id and name");
        return;
      }
      Topic topic = new Topic(id, name, new ArrayList<>());
      topicService.save(topic);
      log.info("Saved topic " + topic.getId());
    } else if (show) {
      if (id.isBlank()) {
        log.error("Please specify non-empty id");
        return;
      }
      topicService.findById(id).ifPresent(topic -> log.info(topic.toString()));
    } else {
      log.error("Please specify one of the following: --list, --create, --show");
    }
  }

  @ShellMethod("Show network and build info.")
  public void info() {
    if (localNodeService.get() != null) {
      log.info(
          """

                    Network:\t%s
                    Version:\t%s
                    Environment:\t%s
                    Operator Nodes:\t%s
                    Origin Node:\t%s

                    Local Node
                    Id:\t%s
                    Name:\t%s
                    Type:\t%s
                    Created at:\t%s
                    Host:\t%s
                    Certificate:\t%s

                    """
              .formatted(
                  networkService.getNetworkConfiguration().getName(),
                  networkService.getNetworkConfiguration().getVersion(),
                  networkService.getNetworkConfiguration().getEnvironment(),
                  networkService.getNetworkConfiguration().getOperators(),
                  networkService.getNetworkConfiguration().getOrigin(),
                  localNodeService.get().getId(),
                  localNodeService.get().getName(),
                  localNodeService.get().getNodeType(),
                  localNodeService.get().getCreatedAt(),
                  localNodeService.get().getHost(),
                  localNodeService.get().getCertificate()));

    } else {
      log.info(
          """

                    Network:\t%s
                    Version:\t%s
                    Environment:\t%s
                    Operator Nodes:\t%s
                    Origin Node:\t%s


                    Local Node not initialised yet.
                    Use the 'init' or 'help init' command to get started.

                    """
              .formatted(
                  networkService.getNetworkConfiguration().getName(),
                  networkService.getNetworkConfiguration().getVersion(),
                  networkService.getNetworkConfiguration().getEnvironment(),
                  networkService.getNetworkConfiguration().getOperators(),
                  networkService.getNetworkConfiguration().getOrigin()));
    }
  }

  @ShellMethod("Manage local certificate.")
  public void cert(
      @ShellOption(defaultValue = "false") boolean show,
      @ShellOption(defaultValue = "false") boolean request,
      @ShellOption(defaultValue = "false") boolean reissue) {
    if (show) {
      log.info(localNodeService.get().getCertificate().toString());
    } else if (request || reissue) {
      // TODO separate request and reissue

      // TODO instead of always using the origin, we should find the closest operator from the list
      String operator = networkService.getNetworkConfiguration().getOrigin();
      if(networkService.certificateRequest(operator, localNodeService.get()) != null) {
        log.info("Sent certificate request to " + operator);
      } else {
        log.error("Failed to send certificate request to " + operator);
      }
    } else {
      log.error("Please specify one of the following: --show, --request, --reissue");
    }
  }

  // example: node --status --host origin.hashnet.test
  @ShellMethod("Request status and data of remote nodes.")
  public void node(
      @ShellOption(defaultValue = "false") boolean status,
      @ShellOption(defaultValue = "false") boolean info,
      @ShellOption(defaultValue = "") String id,
      @ShellOption(defaultValue = "") String host) {

    // user wants to request status
    if (status) {
      // either id or host has to be specified
      if (id.isBlank() && host.isBlank()) {
        log.error("Please specify non-empty id or host");
        return;
      }
      // host is specified, we can send the request right away
      if (!host.isBlank()) {
        log.info(networkService.getNodeStatus(host).toString());
      } else if (!id.isBlank()) {
        // id is specified, we need to check if we know the host of this node yet
        // before we send a request
        Node node = networkService.findKnownNodeById(id).orElse(null);
        if (node != null) {
          log.info(networkService.getNodeStatus(node.getHost()).toString());
        } else {
          log.error("Specified node is unknown: " + id);
        }
      }
    } else if (info) {
      // user wants to request complete node info
      // either id or host has to be specified
      if (id.isBlank() && host.isBlank()) {
        log.error("Please specify non-empty id or host");
        return;
      }
      if (!host.isBlank()) {
        log.info("Requesting node info from " + host);
        log.info(networkService.requestNodeInfo(host).toString());
        // host is specified, we can send the request right away
      } else if (!id.isBlank()) {
        // id is specified, we need to check if we know the host of this node yet
        // before we send a request
        Node node = networkService.findKnownNodeById(id).orElse(null);
        if (node != null) {
          log.info("Requesting node info from " + node.getHost());
          log.info(networkService.requestNodeInfo(node.getHost()).toString());
        } else {
          log.error("Specified node is unknown: " + id);
        }
      }

    } else {
      log.error("Please specify one of the following: --status, --info");
    }
  }

  // for example:
  // init --type ORIGIN --id origin --name 'HashNet Origin Node' --domain hashnet.danielszabo.me
  // --legal-name 'Daniel Szabo' --admin-email daniel.szabo99@outlook.com --address-line1 '1/2 22
  // Maxwellton Street' --post-code PA12UB --country Scotland

  // init --type ARCHIVE --id testarchive1 --name 'HashNet Test Archive Node 1' --domain
  // archive1.hashnet.test --legal-name 'HashNet Test Organisation' --admin-email contact@archive.test --address-line1
  // '123 High Street' --post-code AB12CD --country Scotland
  @ShellMethod("Initialise local node configuration.")
  public void init(
      String id,
      NodeType type,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      @ShellOption(defaultValue = "N/A") String addressLine2,
      String postCode,
      String country) {
    localNodeService.init(
        id,
        type,
        name,
        domain,
        legalName,
        adminEmail,
        addressLine1,
        addressLine2,
        postCode,
        country);
  }
}
