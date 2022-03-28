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

package uk.ac.uws.danielszabo.automodera.integrator.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.ac.uws.danielszabo.automodera.integrator.model.IntegrationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class IntegrationInterceptor implements HandlerInterceptor {

  private final IntegrationContext integrationContext;

  private int requests;

  public IntegrationInterceptor(IntegrationContext integrationContext) {
    this.integrationContext = integrationContext;

    new Thread(
        () -> {
          while (true) {
            requests = integrationContext.getRequestLimitCount();
            try {
              this.wait(integrationContext.getRequestLimitPeriod() * 1000L);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (requests == 0) {
      IntegrationContext.throttled = true;
    }

    if (!integrationContext.isActive()
        || IntegrationContext.throttled
        || integrationContext.getEnabledInputAddresses().contains(request.getRemoteAddr())) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    requests--;

    return HandlerInterceptor.super.preHandle(request, response, handler);
  }
}
