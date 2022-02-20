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

package uk.ac.uws.danielszabo.automodera.operator.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.automodera.operator.service.OperatorServiceFacade;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.Message;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.MessageFactory;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("rest/net/certificate")
public class CertificateRestController {

    private final OperatorServiceFacade operatorServiceFacade;

    private final MessageFactory messageFactory;

    public CertificateRestController(
            OperatorServiceFacade operatorServiceFacade, MessageFactory messageFactory) {
        this.operatorServiceFacade = operatorServiceFacade;
        this.messageFactory = messageFactory;
    }

    @PostMapping(
            value = "request",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity postRequest(@RequestBody Message message) {
        // respond with 403 if local node is inactive
        if (!operatorServiceFacade.getLocalNode().isActive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CertificateRequest certificateRequest = (CertificateRequest) message.getContent();
        log.info("Received certificate signing request from " + certificateRequest.getNode().getId());
        operatorServiceFacade.saveCertificateRequest(certificateRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(
            value = "verify",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity getVerification(@RequestBody Message message, HttpServletRequest request) {// respond with 403 if local node is inactive
        if (!operatorServiceFacade.getLocalNode().isActive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (operatorServiceFacade.verifyCertificate(message.getCertificate())) {
            // retrieve certificate to be verified
            NodeCertificate nodeCertificate = (NodeCertificate) message.getContent();
            // find node the certificate belongs to
            Node node = operatorServiceFacade.findKnownNodeById(nodeCertificate.getId()).orElse(null);
            Boolean result;
            // the node is not found, so we did not issue this certificate. cannot verify that it is valid
            if (node == null) result = false;
            else {
                // we found the node it was issued to, let's verify it
                nodeCertificate.setNode(node);
                result = operatorServiceFacade.verifyCertificate(nodeCertificate);
            }
            log.info(
                    "Received certificate verification request for certificate "
                            + message.getCertificate().getId()
                            + " from "
                            + request.getRemoteAddr()
                            + ": "
                            + (result ? "VALID" : "INVALID"));

            return new ResponseEntity<>(
                    messageFactory.getMessage(result ? "VALID" : "INVALID"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
