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

package uk.ac.uws.danielszabo.automodera.integrator.web.gui;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.integrator.model.Report;
import uk.ac.uws.danielszabo.automodera.integrator.service.IntegratorServiceFacade;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping()
public class IntegratorWebController {

  private final IntegratorServiceFacade integratorServiceFacade;

  private final Environment env;

  public IntegratorWebController(IntegratorServiceFacade integratorServiceFacade, Environment env) {
    this.integratorServiceFacade = integratorServiceFacade;
    this.env = env;
  }

  @GetMapping("")
  public String getIndex(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
    model.addAttribute("date", LocalDateTime.now());

    CertificateRequest certificateRequest =
        integratorServiceFacade.getAllCertificateRequests().stream().findFirst().orElse(null);
    if (certificateRequest != null) {
      switch (certificateRequest.getStatus()) {
        case WAITING -> model.addAttribute("connectionStatus", "Awaiting approval");
        case ISSUED -> model.addAttribute("connectionStatus", "Joined");
        case REJECTED -> model.addAttribute("connectionStatus", "Rejected");
      }
    } else {
      model.addAttribute("connectionStatus", "Not connected");
    }

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-index";
  }

  @PostMapping("status")
  public String postStatus(Model model, @RequestParam String status) {
    if (integratorServiceFacade.getLocalNode() != null) {
      switch (status) {
        case "activate":
          {
            Node node = integratorServiceFacade.getLocalNode();
            node.setOnline(true);
            node.setActive(true);
            integratorServiceFacade.saveNode(node);
            break;
          }
        case "deactivate":
          {
            Node node = integratorServiceFacade.getLocalNode();
            node.setOnline(true);
            node.setActive(false);
            integratorServiceFacade.saveNode(node);
            break;
          }
        case "shutdown":
          {
            Node node = integratorServiceFacade.getLocalNode();
            node.setOnline(false);
            node.setActive(false);
            integratorServiceFacade.saveNode(node);
            integratorServiceFacade.shutDown();
            break;
          }
      }
    }
    return "redirect:/";
  }

  @GetMapping("network")
  public String getNetwork(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("nodes", integratorServiceFacade.getAllKnownNodes());
    model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-network";
  }

  @GetMapping("setup")
  public String getSetup(Model model) {
    if (integratorServiceFacade.getLocalNode() == null) {
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
    if (integratorServiceFacade.getLocalNode() == null) {
      integratorServiceFacade.init(
          id,
          displayName,
          domainName,
          legalName,
          adminEmail,
          addressLine1,
          addressLine2,
          postCode,
          country);
    }
    return "redirect:/";
  }

  @GetMapping("info")
  public String getInfo(Model model, @RequestParam(required = false) String nodeId) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    if (nodeId != null) {
      integratorServiceFacade
          .getKnownNodeById(nodeId)
          .ifPresent(
              n -> {
                model.addAttribute("node", n);
                model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
                try {
                  model.addAttribute(
                      "collections",
                      integratorServiceFacade.requestCollectionRepertoireFormArchive(n));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              });
    } else {
      model.addAttribute("node", integratorServiceFacade.getLocalNode());
    }

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-node";
  }

  //    @GetMapping("network")
  //    public String getNetwork(Model model) throws Exception {
  //        model.addAttribute("node", integratorServiceFacade.getLocalNode());
  //        model.addAttribute("nodes", integratorServiceFacade.retrieveAllNodes());
  //        model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
  //        return "network";
  //    }
  // connect to network
  @PostMapping("network")
  public String postNetwork(Model model, @RequestParam String origin, @RequestParam String action) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }
    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));

    // data for index page
    model.addAttribute("node", integratorServiceFacade.getLocalNode());

    try {
      switch (action) {
        case "test":
          {
            NetworkConfiguration networkConfiguration =
                integratorServiceFacade.fetchNetworkConfiguration(origin);
            if (networkConfiguration == null) {
              model.addAttribute("connectionStatus", "Failed to fetch network configuration");
            }
            model.addAttribute("network", networkConfiguration);
            model.addAttribute("connectionStatus", "Test connection");
            return "common-index";
          }
        case "connect":
          {
            integratorServiceFacade.fetchNetworkConfigurationAndConnect(origin);
            return "redirect:/";
          }
        case "disconnect":
          {
            integratorServiceFacade.clearNetworkConfiguration();
            return "redirect:/";
          }
      }

      // maven build data for footer
      model.addAttribute("buildName", env.getProperty("build.name"));
      model.addAttribute("buildVersion", env.getProperty("build.version"));
      model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    } catch (Exception e) {
      model.addAttribute("connectionStatus", "Failed to fetch network configuration");
      return "common-index";
    }
    return "redirect:/";
  }

  @GetMapping("collection/{arch}/{coll}")
  public String getCollection(
      Model model, @PathVariable("arch") String archive, @PathVariable("coll") String collection) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    Node node = integratorServiceFacade.getKnownNodeById(archive).orElse(null);
    if (node != null) {
      model.addAttribute("node", node);
      model.addAttribute(
          "collection",
          integratorServiceFacade
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
  public String getTopic(Model model, @PathVariable String topic) throws Exception {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("topic", topic);
    model.addAttribute("collections", integratorServiceFacade.getCollecitonsByTopic(topic));

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-topic";
  }

  @GetMapping("collections")
  public String getCollections(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("collections", integratorServiceFacade.retrieveDownloadedHashCollections());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "collections";
  }

  @GetMapping("subscriptions")
  public String getSubscriptions(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("topics", integratorServiceFacade.getAllTopicsWithCollections());
    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());
    model.addAttribute("collections", integratorServiceFacade.retrieveDownloadedHashCollections());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "topics";
  }

  @PostMapping("subscription")
  public String postSubscription(Model model, @RequestParam String topic) {
    integratorServiceFacade.addSubscription(topic);
    return "redirect:/subscriptions";
  }

  @PostMapping("subscriptions/edit")
  public String postSubscriptionsEdit(
      Model model, @RequestParam String action, @RequestParam List<String> selected) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    switch (action) {
      case "filter":
        {
          for (String s : selected) {
            integratorServiceFacade.addSubscription(s);
          }
          break;
        }
      case "unfilter":
        {
          for (String s : selected) {
            integratorServiceFacade.removeSubscription(s);
          }
          break;
        }
    }

    return "redirect:/subscriptions";
  }

  @GetMapping("test")
  public String getTest(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "test";
  }

  @PostMapping("test")
  public String postTest(
      Model model, @RequestParam("image") MultipartFile multipartFile, String attachment)
      throws IOException {
    model.addAttribute("node", integratorServiceFacade.getLocalNode());

    multipartFile.transferTo(Path.of("./temp/" + multipartFile.getOriginalFilename()));
    Report report =
        integratorServiceFacade.checkImage(
            "./temp/" + multipartFile.getOriginalFilename(), attachment, "Web Console");
    model.addAttribute("report", report);
    model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());

    return "test";
  }

  @GetMapping("integration")
  public String getIntegration(Model model) {
    // redirect to setup page if local node is not set
    if (integratorServiceFacade.getLocalNode() == null) {
      return "redirect:/setup";
    }

    model.addAttribute("node", integratorServiceFacade.getLocalNode());
    model.addAttribute("integrationConfig", integratorServiceFacade.getIntegrationConfiguration());
    model.addAttribute("reportHistory", integratorServiceFacade.getAllReports());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "integration";
  }
}
