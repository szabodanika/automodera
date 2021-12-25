package uk.ac.uws.danielszabo.common.model.hash;

import lombok.*;
import uk.ac.uws.danielszabo.common.model.network.node.Node;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HashCollectionsReport {

  @XmlElementWrapper(name = "hashCollectionList")
  @XmlElement(name = "hashCollection")
  @NonNull
  private List<HashCollection> hashCollectionList;

}
