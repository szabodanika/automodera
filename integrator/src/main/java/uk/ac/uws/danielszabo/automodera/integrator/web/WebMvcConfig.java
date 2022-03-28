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


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.ac.uws.danielszabo.automodera.common.constants.WebPaths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final IntegrationInterceptor interceptor;

  public WebMvcConfig(IntegrationInterceptor interceptor) {
	this.interceptor = interceptor;
  }


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(interceptor).addPathPatterns(WebPaths.REST_BASE_PATH + "/api");
  }
}
