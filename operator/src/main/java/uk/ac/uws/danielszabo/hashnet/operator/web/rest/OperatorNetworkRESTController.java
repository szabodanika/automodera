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
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.common.model.network.message.MessageFactory;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.hashnet.operator.service.OperatorServiceFacade;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("net")
public class OperatorNetworkRESTController {

  private final OperatorServiceFacade operatorServiceFacade;

  private final MessageFactory messageFactory;

  public OperatorNetworkRESTController(
      OperatorServiceFacade operatorServiceFacade, MessageFactory messageFactory) {
    this.operatorServiceFacade = operatorServiceFacade;
    this.messageFactory = messageFactory;
  }

  @PostMapping(
      value = "conf",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity postConfigRequest(HttpServletRequest request) {
    log.info("Network configuration requested by " + request.getRemoteAddr());
    return new ResponseEntity<>(operatorServiceFacade.getNetworkConfiguration(), HttpStatus.OK);
  }

  @PostMapping(
      value = "resolveid",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity postResolveId(@RequestBody Message message, HttpServletRequest request) {
    log.info("Id resolution requested by " + request.getRemoteAddr());
    Optional<Node> optionalNode =
        operatorServiceFacade.findKnownNodeById((String) message.getContent());
    if (optionalNode.isPresent()) {
      return new ResponseEntity<>(
          messageFactory.getMessage(optionalNode.get().getHost()), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }
}
