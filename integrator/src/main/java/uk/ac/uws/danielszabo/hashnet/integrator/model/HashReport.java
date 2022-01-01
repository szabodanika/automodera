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

package uk.ac.uws.danielszabo.hashnet.integrator.model;

import lombok.*;
import org.apache.commons.math3.exception.OutOfRangeException;
import uk.ac.uws.danielszabo.common.model.hash.Image;
import uk.ac.uws.danielszabo.common.model.hash.Topic;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HashReport {

  public enum MatchProbability {
    NONE(0.2),
    LOW(0.4),
    MODERATE(0.6),
    HIGH(0.8),
    EXACT(1);

    public double maxValue;

    MatchProbability(double maxValue) {
      this.maxValue = maxValue;
    }

    public static MatchProbability getMatchProbability(double highestMatchScore) {
      for (MatchProbability matchProbability : MatchProbability.values()) {
        if (highestMatchScore <= matchProbability.maxValue) {
          return matchProbability;
        }
      }
      throw new OutOfRangeException(highestMatchScore, 0d, 1d);
    }

  }

  @NonNull
  private Image highestMatch;

  @NonNull
  private double highestMatchScore;

  @Enumerated(EnumType.STRING)
  private MatchProbability matchProbability;

  @XmlElementWrapper(name = "topicList")
  @XmlElement(name = "topic")
  @XmlIDREF
  @NonNull
  private List<Topic> topicList;

  public HashReport(Image highestMatch, double highestMatchScore, List<Topic> topicList) {
    this.highestMatch = highestMatch;
    this.highestMatchScore = highestMatchScore;
    this.topicList = topicList;
    this.matchProbability = HashReport.MatchProbability.getMatchProbability(highestMatchScore);
  }

}
