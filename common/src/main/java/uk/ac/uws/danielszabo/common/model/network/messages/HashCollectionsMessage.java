package uk.ac.uws.danielszabo.common.model.network.messages;

import lombok.*;
import uk.ac.uws.danielszabo.common.model.hash.HashCollection;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HashCollectionsMessage {

  @XmlElementWrapper(name = "hashCollectionList")
  @XmlElement(name = "hashCollection")
  @NonNull
  private List<HashCollection> hashCollectionList;
}
