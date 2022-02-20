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

package uk.ac.uws.danielszabo.automodera.common.service.image;

// import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.NodeType;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.repository.HashCollectionRepository;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashService;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashServiceImpl;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceImplTest {
//
//  @Mock private HashCollectionRepository hashCollectionRepository;
//
//  private HashCollectionService hashCollectionService;
//
//  private HashService hashService;
//
//  @BeforeEach
//  public void beforeEach() {
//    hashService = new HashServiceImpl();
//    hashCollectionService = new HashCollectionServiceImpl(hashCollectionRepository, hashService);
//  }
//
//  @Test
//  public void testGenerateHashCollection() throws IOException {
//
//    // default case
//
//    Node testNode =
//        new Node(
//            "testnode1",
//            "Test Node 1",
//            NodeType.INTEGRATOR,
//            "testnode1.test",
//            new Date(new java.util.Date().getTime()),
//            new ArrayList<>(),
//            new ArrayList<>());
//
//    when(hashCollectionRepository.save(any())).then(returnsFirstArg());
//
//    Collection collection =
//        hashCollectionService.generateHashCollection(
//            "./src/test/resources",
//            "test-collection",
//            "Test Collection",
//            "Description",
//            testNode,
//            List.of("topic1"),
//            false);
//
//    assertEquals("test-collection", collection.getId());
//    assertEquals("Test Collection", collection.getName());
//    assertEquals("Description", collection.getDescription());
//    assertEquals(List.of("topic1"), collection.getTopicList());
//    assertEquals(9, collection.getImageList().size());
//    assertTrue(collection.isEnabled());
//    assertNotNull(collection.getCreated());
//    assertNotNull(collection.getUpdated());
//    assertEquals(1, collection.getVersion());
//    assertEquals(collection.getArchiveId(), testNode.getId());
//    assertEquals(collection.getArchive(), testNode);
//
//    // images not accessible
//
//    assertDoesNotThrow(
//        () -> {
//          hashCollectionService.generateHashCollection(
//              "nonexistentpath",
//              "test-collection",
//              "Test Collection",
//              "Description",
//              testNode,
//              List.of("topic1"),
//              false);
//        });
//  }
}
