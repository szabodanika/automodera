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

package uk.ac.uws.danielszabo.automodera.operator.web.gui;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.automodera.common.constants.WebPaths;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.operator.OperatorServer;
import uk.ac.uws.danielszabo.automodera.operator.service.OperatorServiceFacade;

import java.time.LocalDateTime;

@Controller
@RequestMapping(WebPaths.GUI_BASE_PATH)
public class OperatorWebController {

  private final Environment env;

  private final OperatorServiceFacade operatorServiceFacade;

  public OperatorWebController(Environment env, OperatorServiceFacade operatorServiceFacade) {
    this.env = env;
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @GetMapping("")
  public String getIndex(Model model) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("network", operatorServiceFacade.getNetworkConfiguration());
    model.addAttribute("date", LocalDateTime.now());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-index";
  }

  @PostMapping("status")
  public String postStatus(Model model, @RequestParam String status) {
    switch (status) {
      case "activate":
        {
          Node node = operatorServiceFacade.getLocalNode();
          node.setOnline(true);
          node.setActive(true);
          operatorServiceFacade.saveNode(node);
          break;
        }
      case "deactivate":
        {
          Node node = operatorServiceFacade.getLocalNode();
          node.setOnline(true);
          node.setActive(false);
          operatorServiceFacade.saveNode(node);
          break;
        }
      case "shutdown":
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
    if (operatorServiceFacade.getLocalNode() == null) {
      // maven build data for footer
      model.addAttribute("buildName", env.getProperty("build.name"));
      model.addAttribute("buildVersion", env.getProperty("build.version"));
      model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
      return "common-setup";
    }
    return "redirect:/";
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

  @GetMapping("info/{nodeId}")
  public String getInfo(Model model, @PathVariable String nodeId) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

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

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-node";
  }

  @GetMapping("netinit")
  public String getNetInit(Model model) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "netinit";
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
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    // redirect to network setup page if needed
    if (operatorServiceFacade.getNetworkConfiguration() == null) {
      return "redirect:/netinit";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("nodes", operatorServiceFacade.findAllNodes());
    model.addAttribute("network", operatorServiceFacade.getNetworkConfiguration());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-network";
  }

  @GetMapping("collection/{arch}/{coll}")
  public String getCollection(
      Model model, @PathVariable("arch") String archive, @PathVariable("coll") String collection) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }
    // redirect to network setup page if needed
    if (operatorServiceFacade.getNetworkConfiguration() == null) {
      return "redirect:/netinit";
    }
    Node node = operatorServiceFacade.findKnownNodeById(archive).orElse(null);
    if (node != null) {
      model.addAttribute("node", node);
      model.addAttribute(
          "collection",
          operatorServiceFacade
              .retrieveHashCollectionById(archive + "/" + collection)
              .orElse(null));
    }

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-collection";
  }

  @GetMapping("topic/{topic}")
  public String getTopic(Model model, @PathVariable String topic) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("topic", topic);
    model.addAttribute("collections", operatorServiceFacade.retrieveHashCollectionsByTopic(topic));

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-topic";
  }

  @GetMapping("certificates")
  public String getCertificates(Model model) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute("certreqs", operatorServiceFacade.findAllCertificateRequests());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "certreqs";
  }

  @GetMapping("certreq/{certReqId}")
  public String getCertReq(Model model, @PathVariable String certReqId) {
    // redirect to setup page if local node is not set
    if (operatorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", operatorServiceFacade.getLocalNode());
    model.addAttribute(
        "certreq", operatorServiceFacade.findCertificateRequestById(certReqId).orElse(null));

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "certreq";
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

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "certreq";
  }
}
