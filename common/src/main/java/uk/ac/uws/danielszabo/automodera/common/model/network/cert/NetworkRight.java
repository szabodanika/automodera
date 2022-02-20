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

package uk.ac.uws.danielszabo.automodera.common.model.network.cert;

public enum NetworkRight {
  // creating new certificates for nodes
  // only operators should have it
  ISSUE_CERTIFICATE,
  // verifying that a certificate is valid
  // only operators should have it
  VERIFY_CERTIFICATE,
  // sending a certificate to an operator for verification
  // only certified nodes should have it
  CHECK_CERTIFICATE,
  // publishing hash collections
  PUBLISH,
  // subscribe to topics and downloading hash collections
  SUBSCRIBE
}
