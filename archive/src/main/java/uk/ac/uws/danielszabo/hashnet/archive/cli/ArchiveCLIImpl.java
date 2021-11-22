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

package uk.ac.uws.danielszabo.hashnet.archive.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.cli.BaseNodeCLIImpl;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.network.*;
import uk.ac.uws.danielszabo.common.service.rest.RestService;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;
import uk.ac.uws.danielszabo.hashnet.archive.service.ArchiveService;

@Import({
  RestServiceImpl.class,
  HashServiceImpl.class,
  LocalNodeServiceImpl.class,
  NetworkServiceImpl.class
})
@Slf4j
@ShellComponent
public class ArchiveCLIImpl extends BaseNodeCLIImpl {

  private final ArchiveService archiveService;

  public ArchiveCLIImpl(
      RestService restService,
      HashService hashService,
      LocalNodeService localNodeService,
      NetworkService networkService,
      ArchiveService archiveService) {
    super(restService, hashService, localNodeService, networkService);
    this.archiveService = archiveService;
  }

  // for example:
  // init --id testarchive1 --name 'HashNet Test Archive Node 1' --domain archive1.hashnet.test
  // --legal-name 'HashNet Test Organisation' --admin-email contact@archive.test --address-line1
  // '123 High Street' --post-code AB12CD --country Scotland
  @ShellMethod("Initialise local node configuration.")
  @Override
  public void init(
      String id,
      @ShellOption(defaultValue = "ARCHIVE") NodeType type,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      @ShellOption(defaultValue = "N/A") String addressLine2,
      String postCode,
      String country) {
    if (type != NodeType.ARCHIVE)
      log.warn(
          "You tried to create a non-ARCHIVE node with the init command. I am ignoring this"
              + " parameter.");
    NodeFactory nodeFactory = new NodeFactoryImpl();
    Node node =
        nodeFactory.getArchiveNode(
            id, name, domain, legalName, adminEmail, addressLine1, addressLine2, postCode, country);
    localNodeService.saveLocalNode(node);
  }

  // for example:
  // hashimages --path /images
  @ShellMethod("Manage Hash Collections")
  public void hashcollection(String path) {
    log.info("Hashing images on path: " + path);
  }
}
