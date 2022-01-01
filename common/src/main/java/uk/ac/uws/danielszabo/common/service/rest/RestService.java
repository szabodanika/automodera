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

import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.model.network.node.NodeStatus;

import java.util.List;

public interface RestService {

  NodeStatus requestStatus(String host);

  Node getNodeByHost(String host) throws Exception;

  // void because certificate requests are not handled automatically
  void sendCertificateRequest(String operator, CertificateRequest certificateRequest)
      throws Exception;

  void sendProcessedCertificateRequest(CertificateRequest certificateRequest) throws Exception;

  NetworkConfiguration sendNetworkConfigurationRequest(String origin) throws Exception;

  boolean requestCertificateVerification(NodeCertificate certificate) throws Exception;

  List<HashCollection> requestAllHashCollections(String host) throws Exception;

  void publishHashCollections(List<HashCollection> hashCollectionList, String host) throws Exception;

  void sendSubscription(Node node, Node localNode, Topic topic) throws Exception;

  List<String> requestAllArchiveAddresses(String host) throws Exception;

  HashCollection downloadHashCollection(String host, String id) throws Exception;

  String getHostById(String origin, String id) throws Exception;
}
