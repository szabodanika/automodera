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

package uk.ac.uws.danielszabo.automodera.common.web.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.ac.uws.danielszabo.automodera.common.constants.WebPaths;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Environment env;

  public WebSecurityConfiguration(Environment env) {
	this.env = env;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	// if web gui is disabled, only allow rest api access
	if (env.getProperty("webgui.enable").equals("false")) {
	  log.info("Web GUI access DISABLED");
	  http.authorizeRequests()
		  .antMatchers(
			  // allow rest controllers
			  WebPaths.REST_BASE_PATH + "/**")
		  .permitAll()
		  .anyRequest()
		  .authenticated()
		  .and()
		  // custom login page
		  .formLogin()
		  .loginPage("/disabled")
		  .permitAll();
	} else if (env.getProperty("webgui.enable").equals("true")) {
	  log.info("Web GUI access ENABLED");
	  http.csrf()
		  .ignoringAntMatchers(
			  // don't request csrf token for rest endpoints
			  WebPaths.REST_BASE_PATH + "/**")
		  .and()
		  .authorizeRequests()
		  .antMatchers(
			  // allow rest controllers
			  WebPaths.REST_BASE_PATH + "/**"
		  ).permitAll()
		  .and()
		  .authorizeRequests()
		  .antMatchers(
			  // and static resources
			  "/font/**",
			  "/image/**",
			  "/bootstrap-5.0.2-dist/**",
			  "/bootstrap-icons-1.7.2/**",
			  "/datatables/**",
			  "/jquery/**"
		  ).permitAll()
		  .and()
		  .authorizeRequests()
		  .antMatchers(
			  // allow only whitelisted ip addresses
			  WebPaths.GUI_BASE_PATH + "/**"
		  )
		  .access("isAuthenticated() and " + Arrays.stream(env.getProperty("webgui.whitelist").split(";")).map(ip -> "hasIpAddress('" + ip + "')").collect(Collectors.joining(" or ")))
		  .and()
		  // custom login page
		  .authorizeRequests()
		  .anyRequest()
		  .access(Arrays.stream(env.getProperty("webgui.whitelist").split(";")).map(ip -> "hasIpAddress('" + ip + "')").collect(Collectors.joining(" or ")))
		  .and()
		  .formLogin()
		  .loginPage("/admin/login")
		  .loginProcessingUrl("/admin/login")
		  .defaultSuccessUrl("/admin")
		  .permitAll()
		  .and()
		  .logout()
		  .logoutUrl("/admin/logout")
		  .logoutSuccessUrl("/admin/login")
		  .permitAll();
	}
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.inMemoryAuthentication()
		.withUser(env.getProperty("webgui.admin.username"))
		.password("{noop}" + env.getProperty("webgui.admin.password"))
		.roles("ADMIN");
  }
}
