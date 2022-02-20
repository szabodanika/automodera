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

import uk.ac.uws.danielszabo.automodera.common.model.collection.Collection;
import uk.ac.uws.danielszabo.automodera.common.model.network.node.Node;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface HashCollectionService {

    Optional<Collection> findById(String id);

    List<Collection> findAllByTopic(String topic);

    List<Collection> findAll();

    Collection generateHashCollection(
            String path,
            String id,
            String name,
            String description,
            Node archive,
            List<String> strings,
            boolean forceRecalc)
            throws IOException;

    List<Collection> findAllEnabledNoImages();

    List<Collection> findAllEnabledNoImagesByTopic(String topic);

    List<Collection> getAllDownloaded();

    Collection save(Collection collection);

    void delete(Collection collection);
}
