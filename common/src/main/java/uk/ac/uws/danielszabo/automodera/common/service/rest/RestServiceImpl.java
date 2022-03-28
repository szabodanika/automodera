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

package uk.ac.uws.danielszabo.automodera.common.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.ac.uws.danielszabo.automodera.common.constants.WebPaths;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.Message;
import uk.ac.uws.danielszabo.automodera.common.model.network.message.MessageFactory;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.AddressListMessage;
import uk.ac.uws.danielszabo.automodera.common.model.network.messages.CollectionRepertoire;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeStatus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

  private final MessageFactory messageFactory;

  // TODO move all this configurable and move it to a constants class that can also connect to
  // controllers and spring security config

  private static String PROTOCOL;

  private final RestTemplate restTemplate = restTemplate();

  private final Environment env;

  public RestServiceImpl(MessageFactory messageFactory, RestTemplateBuilder restTemplateBuilder, Environment env) throws Exception {
	this.messageFactory = messageFactory;

	this.env = env;
    if(Arrays.stream(env.getActiveProfiles()).toList().contains("dev")) {
	  PROTOCOL = "http";
    } else {
	  PROTOCOL = "https";
    }
  }

  // requests all the info about a certain node
  @Override
  public Node getNodeByHost(String host) throws TargetNodeUnreachableException {
	ResponseEntity response = postAsXML(host, WebPaths.INFO_PATH, Node.class);
	return (Node) response.getBody();
  }

  // requests only availability from a node, it is a very basic ping
  @Override
  public NodeStatus downloadNodeStatus(String host) {
	ResponseEntity response = null;
	try {
	  response = postAsXML(host, WebPaths.STATUS_PATH, NodeStatus.class);
	  return (NodeStatus) response.getBody();
	} catch (TargetNodeUnreachableException e) {
	  return new NodeStatus(false, false);
	}
  }

  // void because certificate requests are not handled automatically
  @Override
  public void sendCertificateRequest(String operator, CertificateRequest certificateRequest)
	  throws TargetNodeUnreachableException {
	postAsXML(operator, WebPaths.REQUEST_PATH, certificateRequest);
  }

  @Override
  public void sendProcessedCertificateRequest(CertificateRequest certificateRequest)
	  throws TargetNodeUnreachableException {
	postAsXML(certificateRequest.getNode().getHost(), WebPaths.PROCESSED_PATH, certificateRequest);
  }

  @Override
  public NetworkConfiguration getNetworkConfiguration(String origin)
	  throws TargetNodeUnreachableException {
	ResponseEntity response = postAsXML(origin, WebPaths.CONFIG_PATH, NetworkConfiguration.class);
	if (response != null) {
	  return ((NetworkConfiguration) response.getBody());
	} else return null;
  }

  @Override
  public boolean requestCertificateVerification(NodeCertificate certificate)
	  throws TargetNodeUnreachableException {
	ResponseEntity response =
		postAsXML(
			certificate.getIssuer().getHost(),
			WebPaths.CERTVERIFY_PATH,
			certificate,
			Message.class);
	if (response != null) {
	  return ((Message) response.getBody()).getContent().equals("VALID");
	} else return false;
  }

  @Override
  public List<Collection> requestRepertoire(String host) throws TargetNodeUnreachableException {
	ResponseEntity response = postAsXML(host, WebPaths.REPERTOIRE_PATH, CollectionRepertoire.class);
	if (response != null) {
	  return ((CollectionRepertoire) response.getBody()).getCollectionList();
	} else return null;
  }

  @Override
  public void sendCollectionRepertoireToIntegrator(List<Collection> collectionList, String host)
	  throws TargetNodeUnreachableException {
	postAsXML(host, WebPaths.PUBLISH_PATH, new CollectionRepertoire(collectionList));
  }

  @Override
  public List<String> requestAllArchiveAddresses(String host)
	  throws TargetNodeUnreachableException {
	ResponseEntity response = null;
	response = postAsXML(host, WebPaths.ARCHIVE_ADDRESS_LIST_PATH, AddressListMessage.class);
	if (response != null) {
	  return ((AddressListMessage) response.getBody()).getAddressList();
	} else return null;
  }

  @Override
  public List<String> requestAllIntegratorAddresses(String host)
	  throws TargetNodeUnreachableException {
	ResponseEntity response = null;
	response = postAsXML(host, WebPaths.INTEGRATOR_ADDRESS_LIST_PATH, AddressListMessage.class);
	if (response != null) {
	  return ((AddressListMessage) response.getBody()).getAddressList();
	} else return null;
  }

  @Override
  public Collection downloadCollection(String host, String id)
	  throws TargetNodeUnreachableException {
	ResponseEntity response = postAsXML(host, WebPaths.DOWNLOAD_PATH, id, Collection.class);
	if (response != null) {
	  return ((Collection) response.getBody());
	} else return null;
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path)
	  throws TargetNodeUnreachableException {
	return postAsXML(host, path, null, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, Class responseType)
	  throws TargetNodeUnreachableException {
	return postAsXML(host, path, null, responseType);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object)
	  throws TargetNodeUnreachableException {
	return postAsXML(host, path, object, Object.class);
  }

  private <T> ResponseEntity<T> postAsXML(String host, String path, T object, Class responseType)
	  throws TargetNodeUnreachableException {
	URI requestURI = null;

	try {
	  if (host.contains(":")) {
		requestURI = new URI(PROTOCOL, null, host.split(":")[0], Integer.parseInt(host.split(":")[1]), path, null, null);
	  } else {
		requestURI = new URI(PROTOCOL, host, path, null);
	  }
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
	Marshaller marshallerObj = null;
	try {
	  marshallerObj = JAXBContext.newInstance(Message.class).createMarshaller();
	  StringWriter sw = new StringWriter();
	  marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  marshallerObj.marshal(messageFactory.getMessage(object), sw);
	  HttpEntity<String> request = new HttpEntity<>(sw.toString(), headers);
	  return (ResponseEntity<T>)
		  this.restTemplate.exchange(requestURI, HttpMethod.POST, request, responseType);
	} catch (JAXBException jaxbException) {
	  if (jaxbException.toString().contains("known to this context")) {
		log.error(
			"Did you forget to add "
				+ object.getClass().getName()
				+ " to the @SeeAlso annotation on "
				+ Message.class.getName()
				+ "?");
	  }
	  jaxbException.printStackTrace();
	  return null;
	} catch (HttpServerErrorException | HttpClientErrorException exception) {
	  exception.printStackTrace();
	  throw new TargetNodeUnreachableException(
		  host, exception.getStatusCode(), exception.getResponseBodyAsString());
	} catch (ResourceAccessException exception) {
	  exception.printStackTrace();
	  throw new TargetNodeUnreachableException(
		  host, HttpStatus.INTERNAL_SERVER_ERROR, "Target Node Unreachable");
	}
  }

  @Bean
  public RestTemplate restTemplate()
	  throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

	SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
		SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
		NoopHostnameVerifier.INSTANCE);

	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

	requestFactory.setHttpClient(HttpClients.custom().setSSLSocketFactory(scsf).build());

	return new RestTemplate(requestFactory);
  }
}
