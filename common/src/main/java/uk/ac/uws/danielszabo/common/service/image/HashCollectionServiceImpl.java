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

package uk.ac.uws.danielszabo.common.service.image;

import dev.brachtendorf.jimagehash.hash.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;
import uk.ac.uws.danielszabo.common.model.hash.Image;
import uk.ac.uws.danielszabo.common.model.hash.Topic;
import uk.ac.uws.danielszabo.common.model.network.node.Node;
import uk.ac.uws.danielszabo.common.repository.HashCollectionRepository;
import uk.ac.uws.danielszabo.common.service.hashing.HashService;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HashCollectionServiceImpl implements HashCollectionService {

  private final HashCollectionRepository hashCollectionRepository;

  private final HashService hashService;

  public HashCollectionServiceImpl(
      HashCollectionRepository hashCollectionRepository, HashService hashService) {
    this.hashCollectionRepository = hashCollectionRepository;
    this.hashService = hashService;
  }

  @Override
  public Optional<HashCollection> findById(String id) {
    return hashCollectionRepository.findById(id);
  }

  @Override
  public List<HashCollection> findAllByTopic(Topic topic) {
    return hashCollectionRepository.findAllByTopicListContains(topic);
  }

  @Override
  public List<HashCollection> findAll() {
    return hashCollectionRepository.findAll();
  }

  @Override
  public HashCollection generateHashCollection(
      String path,
      String id,
      String name,
      String description,
      Node archive,
      List<Topic> topics,
      boolean forceRecalc)
      throws IOException {

    // prepare new hashcollection or load it from repository
    // if it already exists
    HashCollection hashCollection;
    hashCollection =
        hashCollectionRepository
            .findById(id)
            .orElse(
                new HashCollection(
                    id,
                    name,
                    1,
                    new java.sql.Date(new java.util.Date().getTime()),
                    new java.sql.Date(new java.util.Date().getTime()),
                    description,
                    true,
                    archive,
                    archive.getId(),
                    topics,
                    new ArrayList<>()));
    // get all the files
    File directoryPath = new File(path);
    if (!path.endsWith("/")) path += "/";
    String imageFileNames[] = directoryPath.list();

    // check if we actually found files
    if (imageFileNames == null || imageFileNames.length == 0) {
      log.error(
          "Specified folder "
              + path
              + " does not contain files or they cannot be accessed by this process.");
      return null;
    }

    // calculate hash for each image
    // create Image object and add it to list
    log.info("Hashing " + imageFileNames.length + " images for " + id);
    for (int i = 0; i < imageFileNames.length; i++) {

      // log progress at 0%, 20%, 40%, 60%, 80%, 100%?
      //      if (i / (float) imageFileNames.length - 1 % 0.2f == 0) {
      //        log.info("Progress: " + i / (float) imageFileNames.length * 100 + "%");
      //      }

      int finalI = i;
      // only calculate hash if it is not present yet or user wants to recalculate hash for every
      // image
      if (forceRecalc
          || !hashCollection.getImageList().stream()
              .anyMatch(img -> img.getId().equals(imageFileNames[finalI]))) {
        Hash hash = hashService.pHash(new File(path + imageFileNames[i]));
        hashCollection
            .getImageList()
            .add(
                new Image(
                    imageFileNames[i],
                    new File(directoryPath + imageFileNames[i]),
                    new java.sql.Date(new java.util.Date().getTime()),
                    hash.getHashValue(),
                    hashCollection));
      }
    }
    log.info("Hashed " + imageFileNames.length + " images for " + id);

    return hashCollectionRepository.save(hashCollection);
  }

  @Override
  public List<HashCollection> findAllEnabledNoImages() {
    return hashCollectionRepository.findAllProjectedByEnabled(true).stream()
        .map(
            h ->
                new HashCollection(
                    h.getId(),
                    h.getName(),
                    h.getCreated(),
                    h.getUpdated(),
                    h.getDescription(),
                    h.getArchiveId(),
                    h.getTopicList(),
                    new ArrayList<>()))
        .collect(Collectors.toList());
  }

  @Override
  public List<HashCollection> findAllEnabledNoImagesByTopic(Topic topic) {
    return hashCollectionRepository
        .findAllProjectedByEnabledAndTopicListContains(true, topic)
        .stream()
        .map(
            h ->
                new HashCollection(
                    h.getId(),
                    h.getName(),
                    h.getCreated(),
                    h.getUpdated(),
                    h.getDescription(),
                    h.getArchiveId(),
                    h.getTopicList(),
                    new ArrayList<>()))
        .collect(Collectors.toList());
  }
}
