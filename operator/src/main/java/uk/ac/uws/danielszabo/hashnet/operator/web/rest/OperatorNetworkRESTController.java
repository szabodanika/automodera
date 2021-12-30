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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("net")
public class OperatorNetworkRESTController {

  private final OperatorServiceFacade operatorServiceFacade;

  public OperatorNetworkRESTController(OperatorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @PostMapping(value = "conf",
    consumes = MediaType.APPLICATION_XML_VALUE,
    produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<NetworkConfiguration> postConfigRequest(HttpServletRequest request) {
    log.info("Network configuration requested by " + request.getRemoteAddr());
    return new ResponseEntity<>(operatorServiceFacade.getNetworkConfiguration(), HttpStatus.OK);
  }
}
