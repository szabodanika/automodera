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

import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.NetworkConfiguration;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.CertificateRequest;
import uk.ac.uws.danielszabo.automodera.common.model.network.cert.NodeCertificate;
import uk.ac.uws.danielszabo.automodera.common.model.network.exception.TargetNodeUnreachableException;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeStatus;

import java.util.List;

public interface RestService {

  NodeStatus downloadNodeStatus(String host);

  Node getNodeByHost(String host) throws TargetNodeUnreachableException;

  void sendCertificateRequest(String operator, CertificateRequest certificateRequest)
      throws TargetNodeUnreachableException;

  void sendProcessedCertificateRequest(CertificateRequest certificateRequest)
      throws TargetNodeUnreachableException;

  NetworkConfiguration getNetworkConfiguration(String origin) throws TargetNodeUnreachableException;

  boolean requestCertificateVerification(NodeCertificate certificate)
      throws TargetNodeUnreachableException;

  List<Collection> requestRepertoire(String host) throws TargetNodeUnreachableException;

  void sendCollectionRepertoireToIntegrator(List<Collection> collectionList, String host)
      throws TargetNodeUnreachableException;

  List<String> requestAllArchiveAddresses(String host) throws TargetNodeUnreachableException;

  List<String> requestAllIntegratorAddresses(String host) throws TargetNodeUnreachableException;

  Collection downloadCollection(String host, String id) throws TargetNodeUnreachableException;
}
