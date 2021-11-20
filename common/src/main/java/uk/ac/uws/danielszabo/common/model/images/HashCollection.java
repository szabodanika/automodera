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

package uk.ac.uws.danielszabo.common.model.images;


import lombok.*;
import uk.ac.uws.danielszabo.common.model.nodes.Node;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class HashCollection {

    @Id
    @XmlID
    @NonNull
    private String id;

    @NonNull
    private String name;

    @ManyToOne
    @NonNull
    private Node archive;

    @ManyToMany
    @JoinTable(
            name = "hashcollection_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "hashcollection_id"))
    @XmlElementWrapper(name = "topicList")
    @XmlElement(name="topic")
    @ToString.Exclude
    @NonNull
    private List<Topic> topicList;

    @OneToMany
    @ToString.Exclude
    @XmlElementWrapper(name = "imageList")
    @XmlElement(name="image")
    @NonNull
    private List<Image> imageList;
}
