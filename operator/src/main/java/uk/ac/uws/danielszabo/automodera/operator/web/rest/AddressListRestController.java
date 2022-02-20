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
import uk.ac.uws.danielszabo.automodera.common.model.network.message.Message;
import uk.ac.uws.danielszabo.automodera.operator.service.OperatorServiceFacade;

@Slf4j
@RestController
@RequestMapping("rest/net/address_list")
public class AddressListRestController {

  private final OperatorServiceFacade operatorServiceFacade;

  public AddressListRestController(OperatorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @PostMapping(
      value = "/archive",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity getArchiveAddresses(@RequestBody Message message) {

    // respond with 403 if local node is inactive
    if (!operatorServiceFacade.getLocalNode().isActive()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    if (operatorServiceFacade.verifyCertificate(message.getCertificate())) {
      return new ResponseEntity<>(
          operatorServiceFacade.getArchiveAddressesMessage(), HttpStatus.OK);
    } else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  @PostMapping(
      value = "/integrator",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity getIntegratorAddresses(@RequestBody Message message) {

    // respond with 403 if local node is inactive
    if (!operatorServiceFacade.getLocalNode().isActive()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    if (operatorServiceFacade.verifyCertificate(message.getCertificate())) {
      return new ResponseEntity<>(
          operatorServiceFacade.getIntegratorAddressesMessage(), HttpStatus.OK);
    } else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}
