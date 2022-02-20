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

package uk.ac.uws.danielszabo.automodera.common.constants;

/**
 * This class is not to be used in data transfer or data persistence it is merely a collection of
 * topics that are recognised by the platform by default and is used only on the view layer.
 */
public enum Topic {

  // TODO add more topics

  EXAMPLE_TOXIC_TOPIC(
      "EXAMPLE_TOXIC_TOPIC", "Toxic", "Description of the Toxic topic", Category.TOXIC),
  EXAMPLE_INDECENT_TOPIC(
      "EXAMPLE_INDECENT_TOPIC", "Indecent", "Description of the Indecent topic", Category.INDECENT),
  EXAMPLE_ADULT_TOPIC(
      "EXAMPLE_ADULT_TOPIC", "Adult", "Description of the Adult topic", Category.ADULT),
  EXAMPLE_OFFENSIVE_TOPIC(
      "EXAMPLE_OFFENSIVE_TOPIC",
      "Offensive",
      "Description of the Offensive topic",
      Category.OFFENSIVE),
  EXAMPLE_HARMFUL_TOPIC(
      "EXAMPLE_HARMFUL_TOPIC", "Harmful", "Description of the Harmful topic", Category.HARMFUL),
  EXAMPLE_DANGEROUS_TOPIC(
      "EXAMPLE_DANGEROUS_TOPIC",
      "Dangerous",
      "Description of the Dangerous topic",
      Category.DANGEROUS),
  EXAMPLE_ILLEGAL_TOPIC(
      "EXAMPLE_ILLEGAL_TOPIC", "Illegal", "Description of the Illegal topic", Category.ILLEGAL);

  String id;
  String displayName;
  String description;
  Category category;

  Topic(String id, String displayName, String description, Category category) {
    this.id = id;
    this.displayName = displayName;
    this.description = description;
    this.category = category;
  }

  public String getId() {
    return id.toLowerCase();
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public String getCategory() {
    return category.name();
  }
}

enum Category {
  TOXIC,
  INDECENT,
  ADULT,
  OFFENSIVE,
  HARMFUL,
  DANGEROUS,
  ILLEGAL
}
