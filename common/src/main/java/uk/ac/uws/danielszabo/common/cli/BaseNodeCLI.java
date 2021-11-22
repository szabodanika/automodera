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

package uk.ac.uws.danielszabo.common.cli;

import org.springframework.shell.standard.ShellOption;
import uk.ac.uws.danielszabo.common.model.network.node.NodeType;

public interface BaseNodeCLI {

  void load();

  void info();

  void init(
      String id,
      NodeType type,
      String name,
      String domain,
      String legalName,
      String adminEmail,
      String addressLine1,
      @ShellOption(defaultValue = "N/A") String addressLine2,
      String postCode,
      String country);

  void localcert(
      @ShellOption(defaultValue = "false") boolean show,
      @ShellOption(defaultValue = "false") boolean request,
      @ShellOption(defaultValue = "false") boolean reissue);
}
