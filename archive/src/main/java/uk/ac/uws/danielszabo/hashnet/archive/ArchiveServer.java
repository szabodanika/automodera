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

package uk.ac.uws.danielszabo.hashnet.archive;

import javafx.scene.shape.Arc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.uws.danielszabo.common.model.network.NetworkConfiguration;

@Slf4j
@Import({NetworkConfiguration.class})
@SpringBootApplication
@EntityScan(basePackages = {"uk.ac.uws.danielszabo.common", "uk.ac.uws.danielszabo.archive"})
@ComponentScan(basePackages = {"uk.ac.uws.danielszabo"})
@EnableJpaRepositories("uk.ac.uws.danielszabo.common.repository")
public class ArchiveServer {

    private static SpringApplication application;

    private static ConfigurableApplicationContext applicationContext;

    public ArchiveServer(ConfigurableApplicationContext applicationContext) {
        ArchiveServer.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        application = new SpringApplication(ArchiveServer.class);
        application.run(args);
    }

    public static void exit() {
        log.info("Shutting down...");
        applicationContext.close();
        System.exit(0);
    }
}
