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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Image;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;
import uk.ac.uws.danielszabo.automodera.common.repository.HashCollectionRepository;
import uk.ac.uws.danielszabo.automodera.common.service.hashing.HashService;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HashCollectionServiceImpl implements HashCollectionService {

  private static final String[] ALLOWED_FILE_EXTENSIONS = {
    "jpg", "jpeg", "png",
  };

  private final HashCollectionRepository hashCollectionRepository;

  private final HashService hashService;

  public HashCollectionServiceImpl(
      HashCollectionRepository hashCollectionRepository, HashService hashService) {
    this.hashCollectionRepository = hashCollectionRepository;
    this.hashService = hashService;
  }

  @Override
  public Optional<Collection> findById(String id) {
    return hashCollectionRepository.findById(id);
  }

  @Override
  public List<Collection> findAllByTopic(String string) {
    return hashCollectionRepository.findAll().stream()
        .filter(hashCollection -> hashCollection.getTopicList().contains(string))
        .collect(Collectors.toList());
  }

  @Override
  public List<Collection> findAll() {
    return hashCollectionRepository.findAll();
  }

  @Override
  public Collection generateHashCollection(
      String path,
      String id,
      String name,
      String description,
      Node archive,
      List<String> strings,
      boolean forceRecalc)
      throws IOException {

    // prepare new hashcollection or load it from repository
    // if it already exists
    Collection collection;
    collection =
        hashCollectionRepository
            .findById(id)
            .orElse(
                new Collection(
                    id,
                    name,
                    1,
                    new java.sql.Date(new java.util.Date().getTime()),
                    new java.sql.Date(new java.util.Date().getTime()),
                    description,
                    true,
                    archive,
                    archive.getId(),
                    strings,
                    new ArrayList<>()));
    // get all the files
    File directoryPath = new File(path);
    if (!path.endsWith("/")) path += "/";
    String imageFileNames[] = directoryPath.list();

    // check if we actually found files
    if (imageFileNames == null) {
      log.error(
          "Specified folder "
              + path
              + " does not contain compatible image files or they cannot be accessed by this"
              + " process.");
      return null;
    }

    List<String> filteredFileNames = new ArrayList<>();
    // filter for only image files
    filename:
    for (String f : imageFileNames) {
      for (String e : ALLOWED_FILE_EXTENSIONS) {
        if (f.toLowerCase().endsWith(e)) {
          filteredFileNames.add(f);
          continue filename;
        }
      }
    }

    // check if we actually found files
    if (filteredFileNames.isEmpty()) {
      log.error(
          "Specified folder "
              + path
              + " does not contain compatible image files or they cannot be accessed by this"
              + " process.");
      return null;
    }

    // calculate hash for each image
    // create Image object and add it to list
    log.info("Hashing " + filteredFileNames.size() + " images for " + id);
    for (int i = 0; i < filteredFileNames.size(); i++) {

      // log progress at 0%, 20%, 40%, 60%, 80%, 100%?
      //      if (i / (float) imageFileNames.length - 1 % 0.2f == 0) {
      //        log.info("Progress: " + i / (float) imageFileNames.length * 100 + "%");
      //      }

      int finalI = i;
      // only calculate hash if it is not present yet or user wants to recalculate hash for every
      // image
      if (forceRecalc
          || !collection.getImageList().stream()
              .anyMatch(img -> img.getId().equals(imageFileNames[finalI]))) {
        String hash = hashService.pHash(new File(path + filteredFileNames.get(i)));
        collection
            .getImageList()
            .add(
                new Image(
                    filteredFileNames.get(i),
                    new File(directoryPath + filteredFileNames.get(i)),
                    new java.sql.Date(new java.util.Date().getTime()),
                    hash,
                    collection));
      }
    }
    log.info("Hashed " + filteredFileNames.size() + " images for " + id);

    return hashCollectionRepository.save(collection);
  }

  @Override
  public List<Collection> findAllEnabledNoImages() {
    return hashCollectionRepository.findAllProjectedByEnabled(true).stream()
        .map(
            h ->
                new Collection(
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
  public List<Collection> findAllEnabledNoImagesByTopic(String string) {
    return hashCollectionRepository.findAllProjectedByEnabled(true).stream()
        .filter(hashCollectionInfo -> hashCollectionInfo.getTopicList().contains(string))
        .map(
            h ->
                new Collection(
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
  public List<Collection> getAllDownloaded() {
    return hashCollectionRepository.findAllByImageListIsNotEmpty();
  }

  @Override
  public Collection save(Collection collection) {
    return hashCollectionRepository.save(collection);
  }

  @Override
  public void delete(Collection collection) {
    hashCollectionRepository.delete(collection);
  }
}
