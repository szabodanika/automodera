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

package uk.ac.uws.danielszabo.automodera.common.model.network;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
// @Configuration
// @EnableConfigurationProperties
// @ConfigurationProperties("network")
// @ConditionalOnResource()
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class NetworkConfiguration {

  public static final String ENV_DEV = "dev", ENV_PROD = "prod";

  @Id private Long id = 0L;

  @NonNull @NotNull private String name;

  @NonNull @NotNull private String environment;

  @NonNull @NotNull private String version;

  @NonNull @NotNull private String origin;

  @ElementCollection(fetch = FetchType.EAGER)
  @NonNull
  private List<String> operators = new ArrayList<>();
}
