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

package uk.ac.uws.danielszabo.hashnet.archive.web.gui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.archive.service.ArchiveServiceFacade;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
//@RequestMapping("archive")
public class ArchiveWebController {

    private final ArchiveServiceFacade archiveServiceFacade;

    public ArchiveWebController(ArchiveServiceFacade archiveServiceFacade) {
        this.archiveServiceFacade = archiveServiceFacade;
    }

    @GetMapping("")
    public String getIndex(Model model) {
        if (archiveServiceFacade.getLocalNode() == null) {
            return "redirect:/setup";
        }
        model.addAttribute("node", archiveServiceFacade.getLocalNode());
        model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());
        model.addAttribute("date", LocalDateTime.now());

        CertificateRequest certificateRequest = archiveServiceFacade.findAllCertificateRequests().stream().findFirst().orElse(null);
        if (certificateRequest != null) {
            switch (certificateRequest.getStatus()) {
                case WAITING -> model.addAttribute("connectionStatus", "Awaiting approval");
                case ISSUED -> model.addAttribute("connectionStatus", "Joined");
                case REJECTED -> model.addAttribute("connectionStatus", "Rejected");
            }
        } else {
            model.addAttribute("connectionStatus", "Not connected");
        }
        return "index";
    }

    @PostMapping("status")
    public String postStatus(Model model, @RequestParam String status) {
        if (archiveServiceFacade.getLocalNode() != null) {
            switch (status) {
                case "Active": {
                    Node node = archiveServiceFacade.getLocalNode();
                    node.setOnline(true);
                    node.setActive(true);
                    archiveServiceFacade.saveNode(node);
                    break;
                }
                case "Inactive": {
                    Node node = archiveServiceFacade.getLocalNode();
                    node.setOnline(true);
                    node.setActive(false);
                    archiveServiceFacade.saveNode(node);
                    break;
                }
                case "Terminate": {
                    Node node = archiveServiceFacade.getLocalNode();
                    node.setOnline(false);
                    node.setActive(false);
                    archiveServiceFacade.saveNode(node);
                    archiveServiceFacade.shutDown();
                    break;
                }
            }
        }
        return "redirect:/";
    }

    @GetMapping("setup")
    public String getSetup(Model model) {
        if (archiveServiceFacade.getLocalNode() == null) {
            return "setup";
        }
        return "redirect:";
    }

    @PostMapping("setup")
    public String postSetup(Model model,
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
            archiveServiceFacade.init(id,
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

    @GetMapping("info")
    public String getInfo(Model model, @RequestParam(required = false) String nodeId) {
        if (nodeId != null) {
            archiveServiceFacade.findKnownNodeById(nodeId).ifPresent(n ->
                    {
                        model.addAttribute("node", n);
                        model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());
                        try {
                            model.addAttribute("collections", archiveServiceFacade.retrieveHashCollectionsByArchive(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } else {
            model.addAttribute("node", archiveServiceFacade.getLocalNode());
        }
        return "node";
    }

    @GetMapping("network")
    public String getNetwork(Model model) throws Exception {
        model.addAttribute("node", archiveServiceFacade.getLocalNode());
        model.addAttribute("nodes", archiveServiceFacade.retrieveAllNodes());
        model.addAttribute("network", archiveServiceFacade.getNetworkConfiguration());
        return "network";
    }

    @PostMapping("network")
    public String postNetwork(Model model, @RequestParam String origin) throws Exception {
        archiveServiceFacade.connectToNetwork(origin);
        return "network";
    }

    @GetMapping("collection")
    public String getCollection(Model model, @RequestParam String nodeId, @RequestParam String collectionId) {
        Node node = archiveServiceFacade.findKnownNodeById(nodeId).orElse(null);
        if (node != null) {
            model.addAttribute("node", node);
            model.addAttribute("collection", archiveServiceFacade.retrieveHashCollectionById(collectionId).orElse(null));
        }
        return "collection";
    }

    @GetMapping("topic")
    public String getTopic(Model model, @RequestParam String topic) throws Exception {

        model.addAttribute("node", archiveServiceFacade.getLocalNode());
        model.addAttribute("topic", topic);
        model.addAttribute("collections", archiveServiceFacade.retrieveHashCollectionsByTopic(topic));
        return "topic";
    }

    @GetMapping("collections")
    public String getCollections(Model model) {
        model.addAttribute("node", archiveServiceFacade.getLocalNode());
        model.addAttribute("collections", archiveServiceFacade.retrieveAllHashCollections());
        return "collections";
    }


    @GetMapping("publish")
    public String getPublish(Model model) {
        model.addAttribute("node", archiveServiceFacade.getLocalNode());
//        model.addAttribute("topics", archiveServiceFacade.getAllTopics());
        return "publish";
    }

    @PostMapping("publish")
    public String postPublish(Model model,
                              @RequestParam String id,
                              @RequestParam String displayName,
                              @RequestParam String description,
                              @RequestParam String topics,
                              @RequestParam String path) throws IOException {
        archiveServiceFacade.generateHashCollection(
                path, id, displayName, description, Arrays.asList(topics.split(",")), true);
        return "redirect:/collections";
    }

}
