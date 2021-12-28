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

package uk.ac.uws.danielszabo.hashnet.operator.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.cli.BaseNodeCLI;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.service.image.TopicService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

import java.util.function.Supplier;

@Slf4j
@ShellComponent
public class OperatorCLI extends BaseNodeCLI {

  private final OperatorServiceFacade operatorServiceFacade;

  public OperatorCLI(
    LocalNodeService localNodeService,
    NetworkService networkService,
    OperatorServiceFacade operatorServiceFacade,
    TopicService topicService) {
    super(localNodeService, networkService, topicService);
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @ShellMethod("Manage certificate requests.")
  public void certreq(
    @ShellOption(defaultValue = "false") boolean list,
    @ShellOption(defaultValue = "false") boolean accept,
    @ShellOption(defaultValue = "false") boolean reject,
    @ShellOption(defaultValue = "null") String id,
    @ShellOption(defaultValue = "null") String message) {
    if (list) {
      StringBuilder printMessage = new StringBuilder();
      for (CertificateRequest certReq : operatorServiceFacade.findAllCertificateRequests()) {
        printMessage.append(certReq).append("\n");
      }
      log.info(printMessage.toString());
    } else if (accept) {
      if (id == null || message == null) {
        log.error("Please specify id and message: --id [id] --message [message]");
      }
      try {
        CertificateRequest certificateRequest =
          operatorServiceFacade.findCertificateRequestById(id).orElseThrow((Supplier<Exception>) () -> new RuntimeException("Certificate Request " + id + " not found"));
        operatorServiceFacade.handleCertificateRequest(
          certificateRequest, CertificateRequest.Status.ISSUED, message);
      } catch (Exception e) {
        log.error("Failed to handle certificate request: " + e.getMessage());
      }
    } else if (reject) {
      if (id == null || message == null) {
        log.error("Please specify id and message: --id [id] --message [message]");
      }
      CertificateRequest certificateRequest =
        operatorServiceFacade.findCertificateRequestById(id).orElseThrow();
      try {
        operatorServiceFacade.handleCertificateRequest(
          certificateRequest, CertificateRequest.Status.REJECTED, message);
      } catch (Exception e) {
        log.error("Failed to handle certificate request: " + e.getMessage());
      }
    } else {
      log.error("Please specify one of the following: --list, --accept, --reject");
    }
  }

  // example:
  // netinit --name 'HashNetPrototype' --environment dev --version 0.1 --origin origin.hashnet.test
  @ShellMethod("Initialise network configuration.")
  public void netinit(String name, String environment, String version, String origin) {

    NetworkConfiguration networkConfiguration =
      new NetworkConfiguration(name, environment, version, origin);

    operatorServiceFacade.saveNetworkConfiguration(networkConfiguration);
  }

  // example:
  // hash --list --id testarchive1
  @ShellMethod("Initialise network configuration.")
  public void hash(@ShellOption(defaultValue = "false") boolean list, String id) {
    if (list) {
      if (id == null) {
        log.error("Please specify non-empty id");
        return;
      }

      Node node = operatorServiceFacade.findKnownNodeById(id).orElse(null);
      if (node != null) {
        try {
          log.info(operatorServiceFacade.retrieveHashCollectionByArchive(node).toString());
        } catch (Exception e) {
          log.error("Failed to retrieve hash collection from archive " + id + ": " + e.getMessage());
        }
      } else {
        log.error("Specified node is unknown: " + id);
      }
    } else {
      log.error("Please specify one of the following: --list");
    }
  }
}
