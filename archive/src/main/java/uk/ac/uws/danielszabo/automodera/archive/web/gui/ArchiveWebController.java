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

package uk.ac.uws.danielszabo.automodera.archive.web.gui;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.automodera.archive.service.ArchiveServiceFacade;
import uk.ac.uws.danielszabo.automodera.common.constants.Topic;
import uk.ac.uws.danielszabo.automodera.common.constants.WebPaths;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(WebPaths.GUI_BASE_PATH)
public class ArchiveWebController {

  private final ArchiveServiceFacade archiveServiceFacade;

  private final Environment env;

  public ArchiveWebController(ArchiveServiceFacade archiveServiceFacade, Environment env) {
    this.archiveServiceFacade = archiveServiceFacade;
    this.env = env;
  }

  @GetMapping("")
  public String getIndex(Model model) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    model.addAttribute("node", archiveServiceFacade.getLocalNode());
    model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());
    model.addAttribute("date", LocalDateTime.now());

    CertificateRequest certificateRequest =
        archiveServiceFacade.findAllCertificateRequests().stream().findFirst().orElse(null);
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
    if (archiveServiceFacade.getLocalNode() != null) {
      switch (status) {
        case "activate":
          {
            Node node = archiveServiceFacade.getLocalNode();
            node.setOnline(true);
            node.setActive(true);
            archiveServiceFacade.saveNode(node);
            break;
          }
        case "deactivate":
          {
            Node node = archiveServiceFacade.getLocalNode();
            node.setOnline(true);
            node.setActive(false);
            archiveServiceFacade.saveNode(node);
            break;
          }
        case "shutdown":
          {
            Node node = archiveServiceFacade.getLocalNode();
            node.setOnline(false);
            node.setActive(false);
            archiveServiceFacade.saveNode(node);
            archiveServiceFacade.shutDown();
            break;
          }
      }
    }
    return "redirect:/admin/";
  }

  @GetMapping("setup")
  public String getSetup(Model model) {
    if (archiveServiceFacade.getLocalNode() == null) {
      // maven build data for footer
      model.addAttribute("buildName", env.getProperty("build.name"));
      model.addAttribute("buildVersion", env.getProperty("build.version"));
      model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
      return "common-setup";
    }
    return "redirect:/admin/";
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
    if (archiveServiceFacade.getLocalNode() == null) {
      archiveServiceFacade.init(
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
    return "redirect:";
  }

  @GetMapping("info/{nodeId}")
  public String getInfo(Model model, @PathVariable(required = false) String nodeId) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    if (nodeId != null) {
      archiveServiceFacade
          .findKnownNodeById(nodeId)
          .ifPresent(
              n -> {
                model.addAttribute("node", n);
                model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());
                if(n.getNodeType() == NodeType.ARCHIVE) {
                  try {
                    model.addAttribute(
                        "collections", archiveServiceFacade.retrieveHashCollectionsByArchive(n));
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              });
    } else {
      model.addAttribute("node", archiveServiceFacade.getLocalNode());
    }

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-node";
  }

  @GetMapping("network")
  public String getNetwork(Model model) throws Exception {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    model.addAttribute("node", archiveServiceFacade.getLocalNode());
    model.addAttribute("nodes", archiveServiceFacade.retrieveAllNodes());
    model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-network";
  }

  // connect to network
  @PostMapping("network")
  public String postNetwork(Model model, @RequestParam String origin, @RequestParam String action) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }
    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));

    // data for index page
    model.addAttribute("node", archiveServiceFacade.getLocalNode());

    try {
      switch (action) {
        case "test":
          {
            NetworkConfiguration networkConfiguration =
                archiveServiceFacade.fetchNetworkConfiguration(origin);
            if (networkConfiguration == null) {
              model.addAttribute("connectionStatus", "Failed to fetch network configuration");
            }
            model.addAttribute("network", networkConfiguration);
            model.addAttribute("connectionStatus", "Test connection");
            return "common-index";
          }
        case "connect":
          {
            archiveServiceFacade.fetchNetworkConfigurationAndConnect(origin);
            return "redirect:/admin/";
          }
        case "disconnect":
          {
            archiveServiceFacade.clearNetworkConfiguration();
            return "redirect:/admin/";
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
    return "redirect:/admin/";
  }

  @GetMapping("collection/{arch}/{coll}")
  public String getCollection(
      Model model, @PathVariable("arch") String archive, @PathVariable("coll") String collection) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    //
    //        model.addAttribute("collection",
    //                archiveServiceFacade.retrieveHashCollectionById(id);

    Node node = archiveServiceFacade.findKnownNodeById(archive).orElse(null);
    if (node != null) {
      model.addAttribute("node", node);
      model.addAttribute(
          "collection",
          archiveServiceFacade.retrieveHashCollectionById(archive + "/" + collection).orElse(null));
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
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    model.addAttribute("node", archiveServiceFacade.getLocalNode());
    model.addAttribute("topic", topic);
    model.addAttribute("collections", archiveServiceFacade.retrieveHashCollectionsByTopic(topic));

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "common-topic";
  }

  @GetMapping("collections")
  public String getCollections(Model model) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    model.addAttribute("node", archiveServiceFacade.getLocalNode());
    model.addAttribute("collections", archiveServiceFacade.retrieveAllHashCollections());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "collections";
  }

  @PostMapping("collections/edit")
  public String getCollections(
      Model model, @RequestParam String action, @RequestParam List<String> selected) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    switch (action) {
      case "publish":
        {
          archiveServiceFacade.sendCollectionRepertoireToAllIntegrators(selected);
          break;
        }
      case "enable":
        {
          for (String s : selected) {
            archiveServiceFacade.enableHashCollectionById(s);
          }
          break;
        }
      case "disable":
        {
          for (String s : selected) {
            archiveServiceFacade.disableHashCollectionById(s);
          }
          break;
        }
      case "delete":
        {
          for (String s : selected) {
            archiveServiceFacade.removeHashCollectionById(s);
          }
          break;
        }
    }

    return "redirect:/admin/collections";
  }

  @GetMapping("publish")
  public String getPublish(Model model) {
    // redirect to setup page if local node is not set
    if (archiveServiceFacade.getLocalNode() == null) {
      return "redirect:/admin/setup";
    }

    Path absolutePath = Paths.get("input").toAbsolutePath();

    try (Stream<Path> paths = Files.walk(absolutePath)) {

      record Dir(String directory, List<String> files, long sizekB, boolean linked) {}

      List<Dir> dirList = new ArrayList<>();

      String directory = null;
      List<String> files = new ArrayList<>();
      long sizeMB = 0;

      for (Path p : paths.collect(Collectors.toList())) {
        String localPath = p.toString().substring(absolutePath.toString().length());
        if (localPath.isBlank()) continue;
        if (Files.isDirectory(p)) {
          if (directory != null) {
            dirList.add(
                new Dir(
                    directory,
                    files,
                    sizeMB,
                    archiveServiceFacade
                        .retrieveHashCollectionById(
                            archiveServiceFacade.getLocalNode().getId() + directory)
                        .isPresent()));
          }
          directory = localPath;
          files = new ArrayList<>();
          sizeMB = 0;
        } else if (Files.isRegularFile(p)) {
          sizeMB += p.toFile().length() / 1000000;
          files.add(localPath + ";" + p.toFile().length() / 1000000 + "MB");
        }
      }
      dirList.add(
          new Dir(
              directory,
              files,
              sizeMB,
              archiveServiceFacade
                  .retrieveHashCollectionById(
                      archiveServiceFacade.getLocalNode().getId() + directory)
                  .isPresent()));
      model.addAttribute("dirList", dirList);
    } catch (IOException e) {
      e.printStackTrace();
    }

    model.addAttribute("topics", Topic.values());

    //            model.addAttribute("files", files);
    //            model.addAttribute("fileCounts", fileCounts);
    //            model.addAttribute("directories",
    // directories.stream().skip(1).collect(Collectors.toList()));
    model.addAttribute("rootPath", absolutePath);

    model.addAttribute("node", archiveServiceFacade.getLocalNode());
    //        model.addAttribute("topics", archiveServiceFacade.getAllTopics());

    // maven build data for footer
    model.addAttribute("buildName", env.getProperty("build.name"));
    model.addAttribute("buildVersion", env.getProperty("build.version"));
    model.addAttribute("buildTimestamp", env.getProperty("build.timestamp"));
    return "publish";
  }

  @PostMapping("publish")
  public String postPublish(
      Model model,
      @RequestParam String displayName,
      @RequestParam String description,
      @RequestParam String topics,
      @RequestParam String path)
      throws IOException {
    archiveServiceFacade.generateHashCollection(
        path,
        archiveServiceFacade.getLocalNode().getId() + path,
        displayName,
        description,
        Arrays.asList(topics.split(",")),
        true);
    return "redirect:/admin/collections";
  }
}
