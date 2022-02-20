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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.integrator.service.IntegratorServiceFacade;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.Message;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("rest/collections")
public class CollectionsRestController {

    private final IntegratorServiceFacade integratorServiceFacade;

    public CollectionsRestController(IntegratorServiceFacade integratorServiceFacade) {
        this.integratorServiceFacade = integratorServiceFacade;
    }

    @PostMapping(
            value = "publish",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity postPublish(@RequestBody Message message, HttpServletRequest request) {
        // respond with 403 if local node is inactive
        if (!integratorServiceFacade.getLocalNode().isActive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (integratorServiceFacade.checkCertificate(
                message.getCertificate(), request.getRemoteAddr())) {

            CollectionRepertoire collectionRepertoire = (CollectionRepertoire) message.getContent();

            integratorServiceFacade.processCollectionRepertoire(collectionRepertoire);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
