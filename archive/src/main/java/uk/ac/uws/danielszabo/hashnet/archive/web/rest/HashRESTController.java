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

package uk.ac.uws.danielszabo.hashnet.archive.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;
import uk.ac.uws.danielszabo.hashnet.archive.service.ArchiveServiceFacade;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("hash")
public class HashRESTController {

  private final ArchiveServiceFacade archiveServiceFacade;

  public HashRESTController(ArchiveServiceFacade archiveServiceFacade) {
    this.archiveServiceFacade = archiveServiceFacade;
  }

  @PostMapping(value = "collections",
    produces = MediaType.APPLICATION_XML_VALUE,
    consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity postCollections(@RequestBody Message message, HttpServletRequest request) {
    if (archiveServiceFacade.checkCertificate(message.getCertificate(), request.getRemoteAddr())) {
      log.info("Received collection listing request from " + message.getCertificate().getId());
      return new ResponseEntity<>(archiveServiceFacade.getHashCollectionReport(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "subscribe",
    produces = MediaType.APPLICATION_XML_VALUE,
    consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity postSubscribe(@RequestBody Message message, HttpServletRequest request) {
    if (archiveServiceFacade.checkCertificate(message.getCertificate(), request.getRemoteAddr())) {
      Subscription subscription = (Subscription) message.getContent();
      try {
        archiveServiceFacade.storeNodeInfo(request.getRemoteAddr());
        archiveServiceFacade.saveSubscription(subscription);
      } catch (Exception e) {
        log.error("Failed to store subscription");
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
      log.info("Received subscription request from " + message.getCertificate().getId() + " for topic " + subscription.getTopic().getId());
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

}
