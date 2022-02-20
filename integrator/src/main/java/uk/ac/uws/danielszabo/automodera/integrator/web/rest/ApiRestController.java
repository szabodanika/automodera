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

package uk.ac.uws.danielszabo.automodera.integrator.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.uws.danielszabo.automodera.integrator.model.Report;
import uk.ac.uws.danielszabo.automodera.integrator.service.IntegratorServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("rest/api")
public class ApiRestController {

  private final IntegratorServiceFacade integratorServiceFacade;

  public ApiRestController(IntegratorServiceFacade integratorServiceFacade) {
    this.integratorServiceFacade = integratorServiceFacade;
  }

  @CrossOrigin
  @PostMapping("check")
  public ResponseEntity postTest(
      Model model,
      @RequestParam("image") MultipartFile multipartFile,
      @RequestParam("attachment") String attachment,
      HttpServletRequest httpServletRequest)
      throws IOException, JAXBException {

    // respond with 403 if local node is inactive
    if (!integratorServiceFacade.getLocalNode().isActive()) {
      return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    multipartFile.transferTo(Path.of("./temp/" + multipartFile.getOriginalFilename()));
    Report report =
        integratorServiceFacade.checkImage(
            "./temp/" + multipartFile.getOriginalFilename(),
            attachment,
            httpServletRequest.getRemoteAddr());

    Marshaller marshallerObj = JAXBContext.newInstance(Report.class).createMarshaller();
    StringWriter sw = new StringWriter();
    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    marshallerObj.marshal(report, sw);

    return new ResponseEntity(sw.toString(), HttpStatus.OK);
  }
}
