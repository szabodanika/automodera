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

package uk.ac.uws.danielszabo.hashnet.archive.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.hashnet.archive.service.ArchiveService;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Enumeration;

@Slf4j
@RestController
@RequestMapping("/cert")
public class CertRESTController {

  private final ArchiveService archiveService;

  private final LocalNodeService localNodeService;

  public CertRESTController(ArchiveService archiveService, LocalNodeService localNodeService) {
    this.archiveService = archiveService;
    this.localNodeService = localNodeService;
  }

  @PostMapping(value = "/processedrequest", consumes = "application/XML")
  public void postRequest(
      HttpServletRequest request, @RequestBody CertificateRequest certificateRequest) {
    System.out.println(request.getMethod());
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      System.out.println(
          "Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
    }
    Enumeration<String> params = request.getParameterNames();
    while (params.hasMoreElements()) {
      String paramName = params.nextElement();
      System.out.println(
          "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
    }
    try {
      Marshaller marshaller = JAXBContext.newInstance(CertificateRequest.class).createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(certificateRequest, System.out);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    switch (certificateRequest.getStatus()) {
      case WAITING -> log.error("Received invalid processed certificate request: status = WAITING");
      case ISSUED -> {
        localNodeService
            .getLocalNode()
            .setCertificate(certificateRequest.getNode().getCertificate());
        localNodeService.saveLocalNode();
        log.info(
            "Certificate request signed by "
                + certificateRequest.getNode().getCertificate().getIssuer().getId()
                + " and saved locally.");
      }
      case REJECTED -> // TODO option to disable software completely if a request is rejected
      log.info("Certificate request rejected: " + certificateRequest.getMessage());
    }
  }
}
