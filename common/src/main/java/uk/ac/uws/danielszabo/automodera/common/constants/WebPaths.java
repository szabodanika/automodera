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

public class WebPaths {

  // common rest endpoints for automated communication between nodes

  public static final String REST_BASE_PATH = "/rest";
  public static final String NET_PATH = REST_BASE_PATH + "/net";

  public static final String INFO_PATH = NET_PATH + "/info";
  public static final String STATUS_PATH = NET_PATH + "/status";
  public static final String CONFIG_PATH = NET_PATH + "/config";
  public static final String ADDRESS_LIST_PATH = NET_PATH + "/address_list";
  public static final String ARCHIVE_ADDRESS_LIST_PATH = ADDRESS_LIST_PATH + "/archive";
  public static final String INTEGRATOR_ADDRESS_LIST_PATH = ADDRESS_LIST_PATH + "/integrator";

  public static final String CERTIFICATE_PATH = NET_PATH + "/certificate";
  public static final String REQUEST_PATH = CERTIFICATE_PATH + "/request";
  public static final String PROCESSED_PATH = CERTIFICATE_PATH + "/processedrequest";
  public static final String CERTVERIFY_PATH = CERTIFICATE_PATH + "/verify";

  public static final String COLLECTIONS_PATH = REST_BASE_PATH + "/collections";
  public static final String REPERTOIRE_PATH = COLLECTIONS_PATH + "/repertoire";
  public static final String PUBLISH_PATH = COLLECTIONS_PATH + "/publish";
  public static final String DOWNLOAD_PATH = COLLECTIONS_PATH + "/download";

  // common gui endpoints
  public static final String GUI_BASE_PATH = "/admin";
}
