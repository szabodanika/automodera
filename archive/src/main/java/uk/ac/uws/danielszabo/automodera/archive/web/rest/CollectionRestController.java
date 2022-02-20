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

package uk.ac.uws.danielszabo.automodera.archive.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.uws.danielszabo.automodera.archive.service.ArchiveServiceFacade;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("rest/collections/")
public class CollectionRestController {

    private final ArchiveServiceFacade archiveServiceFacade;

    public CollectionRestController(ArchiveServiceFacade archiveServiceFacade) {
        this.archiveServiceFacade = archiveServiceFacade;
    }

    @PostMapping(
            value = "repertoire",
            produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity postCollections(@RequestBody Message message, HttpServletRequest request) {
        // respond with 403 if local node is inactive
        if (!archiveServiceFacade.getLocalNode().isActive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (archiveServiceFacade.checkCertificate(message.getCertificate(), request.getRemoteAddr())) {
            log.info("Received collection listing request from " + message.getCertificate().getId());
            return new ResponseEntity<>(archiveServiceFacade.getHashCollectionReport(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(
            value = "download",
            produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity postHCDownload(@RequestBody Message message, HttpServletRequest request) {
        // respond with 403 if local node is inactive
        if (!archiveServiceFacade.getLocalNode().isActive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (archiveServiceFacade.checkCertificate(message.getCertificate(), request.getRemoteAddr())) {
            log.info(
                    "Received hash collection download request from " + message.getCertificate().getId());

            String HCId = (String) message.getContent();

            Optional<Collection> optionalHashCollection =
                    archiveServiceFacade.retrieveHashCollectionById(HCId);
            if (optionalHashCollection.isPresent()) {

                return new ResponseEntity<>(optionalHashCollection.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

//    @PostMapping(
//            value = "subscribe",
//            produces = MediaType.APPLICATION_XML_VALUE,
//            consumes = MediaType.APPLICATION_XML_VALUE)
//    public ResponseEntity postSubscribe(@RequestBody Message message, HttpServletRequest request) {
//        // respond with 403 if local node is inactive
//        if (!archiveServiceFacade.getLocalNode().isActive()) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//
//        if (archiveServiceFacade.checkCertificate(message.getCertificate(), request.getRemoteAddr())) {
//
//            try {
//                archiveServiceFacade.storeNodeInfo(request.getRemoteAddr());
//                archiveServiceFacade.sendCollectionRepertoireToIntegrator(subscription);
//            } catch (Exception e) {
//                log.error("Failed to store subscription");
//                e.printStackTrace();
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//            log.info(
//                    "Received subscription request from "
//                            + message.getCertificate().getId()
//                            + " for topic "
//                            + subscription.getTopic());
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//    }
}
