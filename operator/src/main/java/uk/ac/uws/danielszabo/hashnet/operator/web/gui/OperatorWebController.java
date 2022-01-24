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

package uk.ac.uws.danielszabo.hashnet.operator.web.gui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.operator.OperatorServer;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

import java.time.LocalDateTime;

@Controller
@RequestMapping()
public class OperatorWebController {

  private final OperatorServiceFacade operatorServiceFacade;

  public OperatorWebController(OperatorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @GetMapping("")
  public String getIndex(Model model) {
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }
    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("network", operatorServiceFacade.getNetworkConfiguration());
    model.addAttribute("date", LocalDateTime.now());
    return "common-index";
  }

  @PostMapping("status")
  public String postStatus(Model model, @RequestParam String status) {
    switch (status) {
      case "Active":
        {
          Node node = operatorServiceFacade.getLocalNode();
          node.setOnline(true);
          node.setActive(true);
          operatorServiceFacade.saveNode(node);
          break;
        }
      case "Inactive":
        {
          Node node = operatorServiceFacade.getLocalNode();
          node.setOnline(true);
          node.setActive(false);
          operatorServiceFacade.saveNode(node);
          break;
        }
      case "Terminate":
        {
          Node node = operatorServiceFacade.getLocalNode();
          node.setOnline(false);
          node.setActive(false);
          OperatorServer.exit();
          operatorServiceFacade.saveNode(node);
          operatorServiceFacade.shutDown();
          break;
        }
    }
    return "redirect:/";
  }

  @GetMapping("setup")
  public String getSetup(Model model) {
    return "common-setup";
  }

  @PostMapping("setup")
  public String postSetup(
      Model model,
      @RequestParam String id,
      @RequestParam String displayName,
      @RequestParam String legalName,
      @RequestParam String addressLine1,
      @RequestParam String addressLine2,
      @RequestParam String postCode,
      @RequestParam String country,
      @RequestParam String domainName,
      @RequestParam String adminEmail) {
    operatorServiceFacade.init(
        id,
        displayName,
        domainName,
        legalName,
        adminEmail,
        addressLine1,
        addressLine2,
        postCode,
        country);
    return "redirect:/";
  }

  @GetMapping("info")
  public String getInfo(Model model, @RequestParam(required = false) String nodeId) {
    if (nodeId != null) {
      operatorServiceFacade
          .findKnownNodeById(nodeId)
          .ifPresent(
              n -> {
                model.addAttribute("node", n);
                try {
                  model.addAttribute(
                      "collections", operatorServiceFacade.retrieveHashCollectionsByArchive(n));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              });
    } else {
      model.addAttribute("node", operatorServiceFacade.getLocalNode());
    }
    return "common-node";
  }

  @GetMapping("netinit")
  public String getNetInit(Model model) {
    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    return "operator-netinit";
  }

  @PostMapping("netinit")
  public String postNetInit(
      Model model,
      @RequestParam String displayName,
      @RequestParam String environment,
      @RequestParam String version) {
    NetworkConfiguration networkConfiguration =
        new NetworkConfiguration(
            displayName, environment, version, operatorServiceFacade.getLocalNode().getHost());
    operatorServiceFacade.saveNetworkConfiguration(networkConfiguration);
    return "redirect:/network";
  }

  @GetMapping("network")
  public String getNetwork(Model model) throws Exception {
    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("nodes", operatorServiceFacade.findAllNodes());
    model.addAttribute("network", operatorServiceFacade.getNetworkConfiguration());
    return "common-network";
  }

  @GetMapping("collection")
  public String getCollection(
      Model model, @RequestParam String nodeId, @RequestParam String collectionId) {
    Node node = operatorServiceFacade.findKnownNodeById(nodeId).orElse(null);
    if (node != null) {
      model.addAttribute("node", node);
      model.addAttribute(
          "collection",
          operatorServiceFacade.retrieveHashCollectionById(collectionId).orElse(null));
    }
    return "common-collection";
  }

  @GetMapping("topic")
  public String getTopic(Model model, @RequestParam String topic) {

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("topic", topic);
    model.addAttribute("collections", operatorServiceFacade.retrieveHashCollectionsByTopic(topic));
    return "common-topic";
  }

  @GetMapping("certreqs")
  public String getCertReqs(Model model) {
    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("certreqs", operatorServiceFacade.findAllCertificateRequests());
    return "operator-certreqs";
  }

  @GetMapping("certreq")
  public String getCertReq(Model model, @RequestParam String certreqId) {
    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute(
        "certreq", operatorServiceFacade.findCertificateRequestById(certreqId).orElse(null));
    return "common-certreq";
  }

  @PostMapping("certdecision")
  public String postCertDecision(
      Model model,
      @RequestParam String certReqId,
      @RequestParam String message,
      @RequestParam Boolean decision)
      throws Exception {

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    operatorServiceFacade.handleCertificateRequest(
        operatorServiceFacade.findCertificateRequestById(certReqId).get(),
        decision ? CertificateRequest.Status.ISSUED : CertificateRequest.Status.REJECTED,
        message);

    model.addAttribute(
        "certreq", operatorServiceFacade.findCertificateRequestById(certReqId).orElse(null));

    return "common-certreq";
  }
}
