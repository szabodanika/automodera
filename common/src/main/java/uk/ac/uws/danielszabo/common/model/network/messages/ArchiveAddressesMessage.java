package uk.ac.uws.danielszabo.common.model.network.messages;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArchiveAddressesMessage {

  @NonNull private List<String> ArchiveAddresses;
}
