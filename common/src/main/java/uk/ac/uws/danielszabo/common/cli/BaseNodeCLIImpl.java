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

package uk.ac.uws.danielszabo.common.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.common.service.hashing.HashServiceImpl;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeService;
import uk.ac.uws.danielszabo.common.service.network.LocalNodeServiceImpl;
import uk.ac.uws.danielszabo.common.service.network.NetworkService;
import uk.ac.uws.danielszabo.common.service.network.NetworkServiceImpl;
import uk.ac.uws.danielszabo.common.service.rest.RestService;
import uk.ac.uws.danielszabo.common.service.rest.RestServiceImpl;

@Import({RestServiceImpl.class, HashServiceImpl.class, LocalNodeServiceImpl.class, NetworkServiceImpl.class, CustomPromptProvider.class})
@Slf4j
@ShellComponent
public abstract class BaseNodeCLIImpl implements BaseNodeCLI {

    protected final RestService restService;

    protected final HashService hashService;

    protected final LocalNodeService localNodeService;

    protected final NetworkService networkService;

    public BaseNodeCLIImpl(RestService restService, HashService hashService, LocalNodeService localNodeService, NetworkService networkService) {
        this.restService = restService;
        this.hashService = hashService;
        this.localNodeService = localNodeService;
        this.networkService = networkService;
    }

    @ShellMethod("Load node configuration.")
    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void load() {
        if (localNodeService.getLocalNode() != null) {
            log.info("Loaded node configuration for " + localNodeService.getLocalNode().getName());
        } else {
            log.warn("No node configuration available. Use the 'init' command to create one.");
        }
    }

    @ShellMethod("Show network and build info.")
    @Override
    public void info() {
        if (localNodeService.getLocalNode() != null) {
            log.info("""
                                        
                    Network:\t%s
                    Version:\t%s
                    Environment:\t%s
                    Operator Nodes:\t%s
                                        
                    Local Node
                    Id:\t%s
                    Name:\t%s
                    Type:\t%s
                    Created at:\t%s
                    Host:\t%s
                    Certificate:\t%s
                                        
                    """.formatted(
                    networkService.getNetworkName(),
                    networkService.getNetworkVersion(),
                    networkService.getNetworkEnvironment(),
                    networkService.getOperatorAddresses(),
                    localNodeService.getLocalNode().getId(),
                    localNodeService.getLocalNode().getName(),
                    localNodeService.getLocalNode().getNodeType(),
                    localNodeService.getLocalNode().getCreatedAt(),
                    localNodeService.getLocalNode().getHost(),
                    localNodeService.getLocalNode().getCertificate()));

        } else {
            log.info("""
                                        
                    Network:\t%s
                    Version:\t%s
                    Environment:\t%s
                    Operator Nodes:\t%s
                                        
                    Local Node not initialised yet.
                    Use the 'init' or 'help init' command to get started.
                                        
                    """.formatted(
                    networkService.getNetworkName(),
                    networkService.getNetworkVersion(),
                    networkService.getNetworkEnvironment(),
                    networkService.getOperatorAddresses()));
        }
    }

    @ShellMethod("Manage local certificate.")
    @Override
    public void localcert(
            @ShellOption(defaultValue = "false") boolean show,
            @ShellOption(defaultValue = "false") boolean request,
            @ShellOption(defaultValue = "false") boolean reissue) {

        if (show) {
            log.info(localNodeService.getLocalNode().getCertificate().toString());
        } else if (request) {
            networkService.certificateRequest();
        } else if (reissue) {
            // TODO separate request and reissue
            networkService.certificateRequest();
        } else {
            log.error("Please specify action: 'localcert show|request|reissue'");
        }
    }
}

