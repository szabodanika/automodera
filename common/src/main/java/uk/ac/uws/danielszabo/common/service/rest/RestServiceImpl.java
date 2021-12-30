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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.messages.ArchiveAddressesMessage;
import uk.ac.uws.danielszabo.common.model.network.messages.HashCollectionsMessage;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.message.Message;
import uk.ac.uws.danielszabo.common.model.network.message.MessageFactory;
import uk.ac.uws.danielszabo.common.model.network.node.NodeStatus;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.Subscription;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
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
  public Node getNodeByHost(String host) throws Exception {
    ResponseEntity response = postAsXML(host, "/net/info", Node.class);
    return (Node) response.getBody();
  }

  // requests only availability from a node, it is a very basic ping
  @Override
  public NodeStatus requestStatus(String host) {
    ResponseEntity response = null;
    try {
      response = postAsXML(host, "/net/status", NodeStatus.class);
      return (NodeStatus) response.getBody();
    } catch (Exception e) {
      return new NodeStatus(false, false);
    }
  }

  // void because certificate requests are not handled automatically
  @Override
  public void sendCertificateRequest(String operator, CertificateRequest certificateRequest) throws Exception {
    postAsXML(operator, "/cert/request", certificateRequest);
  }

  @Override
  public void sendProcessedCertificateRequest(CertificateRequest certificateRequest) throws Exception {
    postAsXML(certificateRequest.getNode().getHost(), "/cert/processedrequest", certificateRequest);
  }

  @Override
  public NetworkConfiguration sendNetworkConfigurationRequest(String origin) throws Exception {
    ResponseEntity response =
      postAsXML(origin, "/net/conf", NetworkConfiguration.class);
    if (response != null) {
      return ((NetworkConfiguration) response.getBody());
    } else
      return null;
  }

  @Override
  public boolean requestCertificateVerification(NodeCertificate certificate) throws Exception {
    ResponseEntity response =
      postAsXML(certificate.getIssuer().getHost(), "/cert/verify", certificate, Message.class);
    if (response != null) {
      return ((Message) response.getBody()).getContent().equals("VALID");
    } else
      return false;
  }

  @Override
  public List<HashCollection> requestAllHashCollections(String host) throws Exception {
    ResponseEntity response = postAsXML(host, "/hash/collections", HashCollectionsMessage.class);
    if (response != null) {
      return ((HashCollectionsMessage) response.getBody()).getHashCollectionList();
    } else
      return null;
  }

  @Override
  public void sendSubscription(Node node, Node localNode, Topic topic) throws Exception {
    postAsXML(node.getHost(), "/hash/subscribe", new Subscription(topic, node.getId(), localNode.getId()));
  }

  @Override
  public List<String> requestAllArchiveAddresses(String host) {
    ResponseEntity response = null;
    try {
      response = postAsXML(host, "/arch/list", ArchiveAddressesMessage.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (response != null) {
      return ((ArchiveAddressesMessage) response.getBody()).getArchiveAddresses();
    } else
      return null;
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path) throws Exception {
    return postAsXML(host, path, null, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, Class responseType) throws Exception {
    return postAsXML(host, path, null, responseType);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object) throws Exception {
    return postAsXML(host, path, object, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object, Class responseType) throws Exception {
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
    headers.setAccept(List.of(new MediaType[]{MediaType.APPLICATION_XML}));

    // build the request

    // create XML marshaller
    Marshaller marshallerObj = JAXBContext.newInstance(Message.class).createMarshaller();
    StringWriter sw = new StringWriter();
    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    try {
      marshallerObj.marshal(messageFactory.getMessage(object), sw);
    } catch (JAXBException jaxbException) {
      if (jaxbException.toString().contains("known to this context")) {
        log.error("Did you forget to add " + object.getClass().getName() + " to the @SeeAlso annotation on " + Message.class.getName() + "?");
      }
      jaxbException.printStackTrace();
      return null;
    }

    HttpEntity<String> request = new HttpEntity<>(sw.toString(), headers);

    return (ResponseEntity<T>) this.restTemplate.exchange(requestURI, HttpMethod.POST,  request, responseType);

  }

}

