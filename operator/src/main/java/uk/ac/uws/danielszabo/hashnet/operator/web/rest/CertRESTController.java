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

package uk.ac.uws.danielszabo.hashnet.operator.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("cert")
public class CertRESTController {

  private final OperatorServiceFacade operatorServiceFacade;

  public CertRESTController(OperatorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @PostMapping(value = "request",
    consumes = MediaType.APPLICATION_XML_VALUE,
    produces = MediaType.APPLICATION_XML_VALUE)
  public void postRequest(
    @RequestBody Message message) {
    CertificateRequest certificateRequest = (CertificateRequest) message.getContent();
    log.info("Received certificate signing request from " + certificateRequest.getNode().getId());
    operatorServiceFacade.saveCertificateRequest(certificateRequest);
  }

  @PostMapping(value = "verify",
    consumes = MediaType.APPLICATION_XML_VALUE,
    produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity getVerification(@RequestBody Message message, HttpServletRequest request) {
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

      return new ResponseEntity<>(new Message(result ? "VALID" : "INVALID", operatorServiceFacade.getLocalNode().getCertificate()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

}
