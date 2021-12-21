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

import jline.internal.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.common.model.network.message.MessageFactory;
import uk.ac.uws.danielszabo.common.model.network.node.NodeStatus;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class RestServiceImpl implements RestService {

  private final MessageFactory messageFactory;

  // TODO this has to be configurable
  // or at the very least changed to https
  private static final String PROTOCOL = "http";

  private final RestTemplate restTemplate;

  public RestServiceImpl(MessageFactory messageFactory, RestTemplateBuilder restTemplateBuilder) {
    this.messageFactory = messageFactory;
    this.restTemplate = restTemplateBuilder.build();
  }

  // requests all the info about a certain node
  @Override
  public Node getNodeByHost(String host) {
    ResponseEntity response =
      postAsXML(host, "/net/info", Node.class);
    return (Node) response.getBody();
  }

  // requests only availability from a node, it is a very basic ping
  @Override
  public NodeStatus requestStatus(String host) {
    ResponseEntity response =
      postAsXML(host, "/net/status", NodeStatus.class);
    return (NodeStatus) response.getBody();
  }

  // void because certificate requests are not handled automatically
  @Override
  public void sendCertificateRequest(String operator, CertificateRequest certificateRequest) {
    postAsXML(operator, "/cert/request", certificateRequest);
  }

  @Override
  public void sendProcessedCertificateRequest(CertificateRequest certificateRequest) {
    postAsXML(certificateRequest.getNode().getHost(), "/cert/processedrequest", certificateRequest);
  }

  @Override
  public NetworkConfiguration sendNetworkConfigurationRequest(String origin) {
    NetworkConfiguration networkConfiguration =
        getObject(origin, "/net/conf", NetworkConfiguration.class);
    return networkConfiguration;
  }

  @Override
  public boolean requestCertificateVerification(NodeCertificate certificate) {
    ResponseEntity response =
        postAsXML(certificate.getIssuer().getHost(), "/cert/verify", NodeStatus.class);
    return (boolean) response.getBody();
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path) {
    return postAsXML(host, path, null, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, Class responseType) {
    return postAsXML(host, path, null, responseType);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object) {
    return postAsXML(host, path, object, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object, Class responseType) {
    URI requestURI = null;

    try {
      requestURI = new URI(PROTOCOL, host, path, null);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    // create headers
    HttpHeaders headers = new HttpHeaders();
    // set `content-type` header
    headers.setContentType(MediaType.APPLICATION_XML);

    // build the request
    try {
      // create XML marshaller
      Marshaller marshallerObj = JAXBContext.newInstance(Message.class).createMarshaller();
      StringWriter sw = new StringWriter();
      marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshallerObj.marshal(messageFactory.getMessage(object), sw);

      HttpEntity<String> request = new HttpEntity<>(sw.toString(), headers);

      return (ResponseEntity<T>) this.restTemplate.postForEntity(requestURI, request, responseType);

    } catch (JAXBException e) {
      e.printStackTrace();
      return null;
    }
  }

  private <T> T getObject(String host, String path, Class c) {

    URI requestURI = null;
    try {
      requestURI = new URI(PROTOCOL, host, path, null);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }

    try {
      return (T) this.restTemplate.getForObject(requestURI, c);
    } catch (ResourceAccessException e) {
      Log.error(e.getCause());
      return null;
    }
  }
}
