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
import uk.ac.uws.danielszabo.common.service.network.NetworkService;

@Slf4j
@RestController
@RequestMapping("net")
public class OperatorNetworkRESTController {

  private final NetworkService networkService;

  public OperatorNetworkRESTController(NetworkService networkService) {
    this.networkService = networkService;
  }

  @GetMapping(value = "conf", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<NetworkConfiguration> getConfig() {
    return new ResponseEntity<>(networkService.getNetworkConfiguration(), HttpStatus.OK);
  }
}
