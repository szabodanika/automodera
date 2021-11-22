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

package uk.ac.uws.danielszabo.common.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.uws.danielszabo.common.model.message.NodeStatus;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    // TODO this has to be configurable
    // or at the very least changed to https
    private static final String PROTOCOL = "http";

    private final RestTemplate restTemplate;

    public RestServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public NodeStatus requestStatus(String host) {

        URI requestURI = null;
        try {
            requestURI = new URI(PROTOCOL, host, "/net/status", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // use `exchange` method for HTTP call
        NodeStatus status = this.restTemplate.getForObject(requestURI, NodeStatus.class);

        return status;
    }

    @Override
    public Node requestNodeInfo(String host) {
        URI requestURI = null;

        try {
            requestURI = new URI(PROTOCOL, host, "/net/info", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // use `exchange` method for HTTP call
        Node node = this.restTemplate.getForObject(requestURI, Node.class);

        return node;
    }


    // void because certificate requests are not handled automatically
    @Override
    public void sendCertificateRequest(String host, CertificateRequest certificateRequest) {
        URI requestURI = null;

        try {
            requestURI = new URI(PROTOCOL, host, "/cert/request", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_XML);

        // build the request
        HttpEntity request = new HttpEntity<>(certificateRequest, headers);

        this.restTemplate.postForEntity(requestURI, request, NodeCertificate.class);
    }

    @Override
    public void sendProcessedCertificateRequest(String host, CertificateRequest certificateRequest) {
        URI requestURI = null;

        try {
            requestURI = new URI(PROTOCOL, host, "/cert/processedrequest", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_XML);

        // build the request
        HttpEntity request = new HttpEntity<>(certificateRequest, headers);

        // use `exchange` method for HTTP call
        this.restTemplate.postForEntity(requestURI, request, NodeCertificate.class);
    }

}
