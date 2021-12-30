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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("hash")
public class HashRESTController {

  private final IntegratorServiceFacade integratorServiceFacade;

  public HashRESTController(IntegratorServiceFacade integratorServiceFacade) {
    this.integratorServiceFacade = integratorServiceFacade;
  }

  @PostMapping(value = "collections",
    consumes = MediaType.APPLICATION_XML_VALUE,
    produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity postCollections(@RequestBody Message message, HttpServletRequest request) {
    if (integratorServiceFacade.checkCertificate(
        message.getCertificate(), request.getRemoteAddr())) {
      return new ResponseEntity<>(integratorServiceFacade.getHashCollectionReport(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
