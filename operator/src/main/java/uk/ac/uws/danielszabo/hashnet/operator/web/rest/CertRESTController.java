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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

@Slf4j
@RestController
@RequestMapping("cert")
public class CertRESTController {

  private final OperatorServiceFacade operatorServiceFacade;

  public CertRESTController(OperatorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @PostMapping(value = "request", consumes = "application/XML")
  public void postRequest(
      // used for debugging the request
      // HttpServletRequest request,
      @RequestBody Message message) {

    CertificateRequest certificateRequest = (CertificateRequest) message.getContent();
    log.info("Received certificate signing request from " + certificateRequest.getNode().getId());
    operatorServiceFacade.saveCertificateRequest(certificateRequest);

    //        this commented bit prints the entire request and the contents

    //        System.out.println(request.getMethod());
    //        request.
    //        Enumeration<String> headerNames = request.getHeaderNames();
    //        while(headerNames.hasMoreElements()) {
    //            String headerName = headerNames.nextElement();
    //            System.out.println("Header Name - " + headerName + ", Value - " +
    // request.getHeader(headerName));
    //        }
    //        Enumeration<String> params = request.getParameterNames();
    //        while(params.hasMoreElements()){
    //            String paramName = params.nextElement();
    //            System.out.println("Parameter Name - "+paramName+", Value -
    // "+request.getParameter(paramName));
    //        }
    //        try {
    //            Marshaller marshaller =
    // JAXBContext.newInstance(CertificateRequest.class).createMarshaller();
    //            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    //            marshaller.marshal(certificateRequest, System.out);
    //        } catch (JAXBException e) {
    //            e.printStackTrace();
    //        }

  }

  @PostMapping(value = "verify", consumes = "application/XML")
  public ResponseEntity getVerification(@RequestBody Message message) {
    if (operatorServiceFacade.verifyCertificate(message.getCertificate())) {
      NodeCertificate nodeCertificate = (NodeCertificate) message.getContent();
      log.info(
          "Received certificate verification request from " + nodeCertificate.getNode().getId());
      return new ResponseEntity<>(
          operatorServiceFacade.verifyCertificate(nodeCertificate), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
