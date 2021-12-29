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

package uk.ac.uws.danielszabo.hashnet.integrator.web.gui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.uws.danielszabo.hashnet.integrator.service.IntegratorServiceFacade;

import java.time.LocalDateTime;

@Controller
@RequestMapping("archive")
public class IntegratorWebController {

  private final IntegratorServiceFacade operatorServiceFacade;

  public IntegratorWebController(IntegratorServiceFacade operatorServiceFacade) {
    this.operatorServiceFacade = operatorServiceFacade;
  }

  @GetMapping("")
  public String getIndex(Model model) {
    model.addAttribute("date", LocalDateTime.now());
    return "index";
  }
}