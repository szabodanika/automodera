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

package uk.ac.uws.danielszabo.hashnet.integrator.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.uws.danielszabo.hashnet.integrator.model.HashReport;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("api")
public class HasherRESTController {

  private final IntegratorServiceFacade integratorServiceFacade;

  public HasherRESTController(IntegratorServiceFacade integratorServiceFacade) {
    this.integratorServiceFacade = integratorServiceFacade;
  }

  @CrossOrigin
  @PostMapping("check")
  public String postTest(Model model, @RequestParam("image") MultipartFile multipartFile)
      throws IOException, JAXBException {
    multipartFile.transferTo(Path.of("./temp/" + multipartFile.getOriginalFilename()));
    HashReport hashReport =
        integratorServiceFacade.checkImage("./temp/" + multipartFile.getOriginalFilename());

    Marshaller marshallerObj = JAXBContext.newInstance(HashReport.class).createMarshaller();
    StringWriter sw = new StringWriter();
    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    marshallerObj.marshal(hashReport, sw);

    return sw.toString();
  }
}
