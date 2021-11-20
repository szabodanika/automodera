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

package uk.ac.uws.danielszabo.hashnet.operator.webservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.uws.danielszabo.common.model.images.HashCollection;
import uk.ac.uws.danielszabo.common.model.images.Image;
import uk.ac.uws.danielszabo.common.model.images.Topic;
import uk.ac.uws.danielszabo.common.model.nodes.NetworkRights;
import uk.ac.uws.danielszabo.common.model.nodes.Node;
import uk.ac.uws.danielszabo.common.model.nodes.NodeCertificate;
import uk.ac.uws.danielszabo.common.model.nodes.NodeType;

import java.io.File;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MainWebServiceController {

  @RequestMapping(value = "", produces = "application/XML")
  public HashCollection getIndex() {
    System.out.println("ASD1");
    log.info("index is called");

    // topics
    Topic topic1 = new Topic("t1", "Topic1", new ArrayList<>());
    Topic topic2 = new Topic("t2", "Topic1", new ArrayList<>());
    Topic topic3 = new Topic("t3", "Topic1", new ArrayList<>());

    List<Topic> topicList = new ArrayList<>();

    topicList.add(topic1);
    topicList.add(topic2);
    topicList.add(topic3);

    // images

    Image testImage1 =
        new Image(
            "123-001",
            new File("images/image1.jpeg"),
            new Date(new java.util.Date().getTime()),
            new BigInteger("1234567"),
            new HashCollection());
    Image testImage2 =
        new Image(
            "123-002",
            new File("images/image2.jpeg"),
            new Date(new java.util.Date().getTime()),
            new BigInteger("32133"),
            new HashCollection());

    List<Image> imageList = new ArrayList<>();

    imageList.add(testImage1);
    imageList.add(testImage2);

    // archive

    List<NetworkRights> networkRightsList = new ArrayList<>();
    networkRightsList.add(NetworkRights.ISSUE_CERTIFICATE);
    networkRightsList.add(NetworkRights.VERIFY_CERTIFICATE);
    networkRightsList.add(NetworkRights.CHECK_CERTIFICATE);

    Node testOperator =
        new Node(
            "testOperator1",
            "Test Operator",
            NodeType.OPERATOR,
            "Test Address 1",
            new NodeCertificate(),
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());

    NodeCertificate testCertificate =
        new NodeCertificate(
            "cert1",
            testOperator,
            new Date(new java.util.Date().getTime()),
            new Date(new java.util.Date().getTime()),
            new Node(), // temporary, replaced in line 110
            "address.com",
            topicList,
            networkRightsList);

    Node testArchive =
        new Node(
            "testArchive1",
            "Test Archive",
            NodeType.ARCHIVE,
            "Test Address 2",
            testCertificate,
            new Date(new java.util.Date().getTime()),
            new ArrayList<>(),
            new ArrayList<>());

    testCertificate.setNode(testArchive);

    // collection

    HashCollection testHashCollection =
        new HashCollection("12", "Test Hash Collection", testArchive, topicList, imageList);

    return testHashCollection;
  }
}
