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

package uk.ac.uws.danielszabo.common.service.network;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.message.NodeStatus;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfig;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.service.rest.RestService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.sql.Date;
import java.util.List;

@Slf4j
@Service
public class NetworkServiceImpl implements NetworkService {

    private final LocalNodeService localNodeService;

    private final NetworkConfig networkConfig;

    private final RestService restService;

    public NetworkServiceImpl(NetworkConfig networkConfig, LocalNodeService localNodeService, RestService restService) {
        this.networkConfig = networkConfig;
        this.localNodeService = localNodeService;
        this.restService = restService;
    }

    @Override
    public String getNetworkName() {
        return networkConfig.getName();
    }

    @Override
    public String getNetworkEnvironment() {
        return networkConfig.getEnvironment();
    }

    @Override
    public String getNetworkVersion() {
        return networkConfig.getVersion();
    }

    @Override
    public List<String> getOperatorAddresses() {
        return networkConfig.getOperators();
    }

    @Override
    public boolean checkCertificate(NodeCertificate certificate) {
        return false;
    }

    @Override
    public NodeStatus getNodeStatus(String address) {
        return null;
    }

    @Override
    public CertificateRequest certificateRequest() {
        if (localNodeService.getLocalNode() != null) {
            // TODO instead of sending all the cert requests to the origin
            // an operator should be selected from the list
            CertificateRequest certReq = new CertificateRequest(localNodeService.getLocalNode());

            try {
                Marshaller marshaller = JAXBContext.newInstance(CertificateRequest.class).createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(certReq, System.out);
            } catch (JAXBException e) {
                e.printStackTrace();
            }

            restService.sendCertificateRequest(networkConfig.getOrigin(), certReq);
            log.info("Sent certificate request to " + networkConfig.getOrigin() + ": " + certReq);
            return certReq;
        } else {
            log.error("Cannot send certificate request. Create local node configuration first!");
            throw new IllegalStateException("Cannot create certificate request without a local node configuration.");
        }
    }

}
