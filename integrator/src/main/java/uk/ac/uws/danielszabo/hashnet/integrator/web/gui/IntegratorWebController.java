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

package uk.ac.uws.danielszabo.hashnet.integrator.web.gui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.integrator.model.HashReport;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping()
public class IntegratorWebController {

    private final IntegratorServiceFacade integratorServiceFacade;

    public IntegratorWebController(IntegratorServiceFacade integratorServiceFacade) {
        this.integratorServiceFacade = integratorServiceFacade;
    }

    @GetMapping("")
    public String getIndex(Model model) {
        if (integratorServiceFacade.getLocalNode() == null) {
            return "redirect:/setup";
        }
        model.addAttribute("node", integratorServiceFacade.getLocalNode());
        model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
        model.addAttribute("date", LocalDateTime.now());

        CertificateRequest certificateRequest = integratorServiceFacade.findAllCertificateRequests().stream().findFirst().orElse(null);
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
        if (integratorServiceFacade.getLocalNode() != null) {
            switch (status) {
                case "Active": {
                    Node node = integratorServiceFacade.getLocalNode();
                    node.setOnline(true);
                    node.setActive(true);
                    integratorServiceFacade.saveNode(node);
                    break;
                }
                case "Inactive": {
                    Node node = integratorServiceFacade.getLocalNode();
                    node.setOnline(true);
                    node.setActive(false);
                    integratorServiceFacade.saveNode(node);
                    break;
                }
                case "Terminate": {
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

    @GetMapping("setup")
    public String getSetup(Model model) {
        if (integratorServiceFacade.getLocalNode() == null) {
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
        if (integratorServiceFacade.getLocalNode() == null) {
            integratorServiceFacade.init(id,
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
            integratorServiceFacade.findKnownNodeById(nodeId).ifPresent(n ->
                    {
                        model.addAttribute("node", n);
                        model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
                        try {
                            model.addAttribute("collections", integratorServiceFacade.retrieveHashCollectionsByArchive(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } else {
            model.addAttribute("node", integratorServiceFacade.getLocalNode());
        }
        return "node";
    }

//    @GetMapping("network")
//    public String getNetwork(Model model) throws Exception {
//        model.addAttribute("node", integratorServiceFacade.getLocalNode());
//        model.addAttribute("nodes", integratorServiceFacade.retrieveAllNodes());
//        model.addAttribute("network", integratorServiceFacade.getNetworkConfiguration());
//        return "network";
//    }

    @PostMapping("network")
    public String postNetwork(Model model, @RequestParam String origin) throws Exception {
        integratorServiceFacade.connectToNetwork(origin);
        return "redirect:/";
    }

    @GetMapping("collection")
    public String getCollection(Model model, @RequestParam String nodeId, @RequestParam String collectionId) {
        Node node = integratorServiceFacade.findKnownNodeById(nodeId).orElse(null);
        if (node != null) {
            model.addAttribute("node", node);
            model.addAttribute("collection", integratorServiceFacade.retrieveHashCollectionById(collectionId).orElse(null));
        }
        return "collection";
    }

    @GetMapping("topic")
    public String getTopic(Model model, @RequestParam String topic) throws Exception {

        model.addAttribute("node", integratorServiceFacade.getLocalNode());
        model.addAttribute("topic", topic);
        model.addAttribute("collections", integratorServiceFacade.retrieveHashCollectionsByTopic(topic));
        return "topic";
    }

    @GetMapping("collections")
    public String getCollections(Model model) {
        model.addAttribute("node", integratorServiceFacade.getLocalNode());
        model.addAttribute("collections", integratorServiceFacade.retrieveDownloadedHashCollections());
        return "collections";
    }

    @GetMapping("subscriptions")
    public String getSubscriptions(Model model) {
        model.addAttribute("node", integratorServiceFacade.getLocalNode());
        model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());
        return "subscriptions";
    }

    @PostMapping("subscription")
    public String postSubscription(Model model, @RequestParam String topic) {
        integratorServiceFacade.addSubscription(topic);
        model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());
        return "redirect:/subscriptions";
    }

    @GetMapping("test")
    public String getTest(Model model) {
        model.addAttribute("node", integratorServiceFacade.getLocalNode());
        model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());
        return "test";
    }

    @PostMapping("test")
    public String postTest(Model model, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        model.addAttribute("node", integratorServiceFacade.getLocalNode());

        multipartFile.transferTo(Path.of("./temp/" + multipartFile.getOriginalFilename()));
        HashReport hashReport = integratorServiceFacade.checkImage("./temp/" + multipartFile.getOriginalFilename());
        model.addAttribute("hashReport", hashReport);
        model.addAttribute("subscriptions", integratorServiceFacade.getSubscriptions());

        return "test";
    }


}
