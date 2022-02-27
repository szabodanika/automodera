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

package uk.ac.uws.danielszabo.automodera.integrator.model;

import lombok.*;
import org.apache.commons.math3.exception.OutOfRangeException;
import uk.ac.uws.danielszabo.automodera.common.model.collection.Image;
import uk.ac.uws.danielszabo.automodera.common.model.collection.TopicListStringConverter;
import uk.ac.uws.danielszabo.automodera.common.util.SQLDateAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Report {

	// TODO this has to be tuned for each algorithm
	// or each algorithm have to be tuned for these numbers
	public enum MatchProbability {
		NONE(0.75),
		LOW(0.80),
		MODERATE(0.90),
		HIGH(0.95),
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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NonNull
	private Long id;

	//  @Column(name = "highestmatch_id")
	//  @NonNull
	//  private String highestMathchId;
	//
	//  @JoinColumn(name = "highestmatch_id", insertable = false, updatable = false)
	//  @ManyToOne(targetEntity = Image.class, fetch = FetchType.EAGER)
	//  @XmlTransient
	//  @NotFound(action = NotFoundAction.IGNORE)
	//  @NonNull private Image highestMatch;

	@ManyToOne
//	@NonNull

	//TODO re-enable this
	@Transient
	private Image highestMatch;

	@NonNull
	private double highestMatchScore;

	@Enumerated(EnumType.STRING)
	private MatchProbability matchProbability;

	private String attachment;

	private String source;

	@NonNull
	@XmlJavaTypeAdapter(SQLDateAdapter.class)
	private Date date;

	@XmlElementWrapper(name = "topicList")
	@XmlElement(name = "topic")
	@NonNull
	@Convert(converter = TopicListStringConverter.class)
	private List<String> topicList;

	public Report(
		Image highestMatch,
		double highestMatchScore,
		List<String> topicList,
		String attachment,
		String source) {
		this.highestMatchScore = highestMatchScore;
		date = new Date(new java.util.Date().getTime());
		this.matchProbability = Report.MatchProbability.getMatchProbability(highestMatchScore);

		// it would be pointless to store these things if there is no match
		if (matchProbability != MatchProbability.NONE) {
			this.topicList = topicList;
			this.highestMatch = highestMatch;
		}
		this.attachment = attachment;
		this.source = source;
	}
}
