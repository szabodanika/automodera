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
import uk.ac.uws.danielszabo.common.cli.BaseNodeCLIImpl;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.common.service.network.NodeFactory;
import uk.ac.uws.danielszabo.common.service.network.NodeFactoryImpl;
import uk.ac.uws.danielszabo.common.service.rest.RestService;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorService;

@Slf4j
@ShellComponent
public class OperatorCLI extends BaseNodeCLIImpl {

    private final OperatorService operatorService;

    public OperatorCLI(RestService restService, HashService hashService, LocalNodeService localNodeService, NetworkService networkService, OperatorService operatorService) {
        super(restService, hashService, localNodeService, networkService);
        this.operatorService = operatorService;
    }

    // for example:
    // init --type ORIGIN --id origin --name 'HashNet Origin Node' --domain hashnet.danielszabo.me --legal-name 'Daniel Szabo' --admin-email daniel.szabo99@outlook.com --address-line1 '1/2 22 Maxwellton Street' --post-code PA12UB --country Scotland
    @ShellMethod("Initialise local node configuration.")
    @Override
    public void init(String id, NodeType type, String name, String domain, String legalName, String adminEmail, String addressLine1, @ShellOption(defaultValue = "N/A") String addressLine2, String postCode, String country) {
        NodeFactory nodeFactory = new NodeFactoryImpl();
        Node node = null;
        switch (type) {
            case OPERATOR -> node = nodeFactory.getOperatorNode(id, name, domain, legalName, adminEmail, addressLine1, addressLine2, postCode, country);
            case ORIGIN -> node = nodeFactory.getOriginNode(id, name, domain, legalName, adminEmail, addressLine1, addressLine2, postCode, country);
            default -> log.error("This software package can only be used in OPERATOR and ORIGIN mode.");
        }
        if (node != null) localNodeService.saveLocalNode(node);
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
            for (CertificateRequest certReq : operatorService.retrieveAllCertificateRequests()) {
                printMessage.append(certReq).append("\n");
            }
            log.info(printMessage.toString());
        } else if (accept) {
            if (id == null || message == null) {
                log.error("Please specify id and message: --id [id] --message [message]");
            }
            CertificateRequest certificateRequest = operatorService.findCertificateRequestById(id).orElseThrow();
            operatorService.handleCertificateRequest(certificateRequest, CertificateRequest.Status.ISSUED, message);
        } else if (reject) {
            if (id == null || message == null) {
                log.error("Please specify id and message: --id [id] --message [message]");
            }
            CertificateRequest certificateRequest = operatorService.findCertificateRequestById(id).orElseThrow();
            operatorService.handleCertificateRequest(certificateRequest, CertificateRequest.Status.REJECTED, message);
        } else {
            log.error("Please specify action: 'certreq --list|accept|reject --id [id] --message [message]'");
        }
    }

}
