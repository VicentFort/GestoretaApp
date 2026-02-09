package com.vfortro.gestoreta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server localhost = new Server();
        localhost.setUrl("http://localhost:8080");
        localhost.setDescription("Gestoreta APP");

        Server pcLocal = new Server();
        pcLocal.setUrl("http://192.168.1.21:8080");
        pcLocal.setDescription("Gestoreta APP Máquina privada");

        Contact myContact = new Contact();
        myContact.setName("Vicent Fort Tronch");

        Info information = new Info()
                .title("Gestoreta APP Backend")
                .version("2.0")
                .contact(myContact);

        return new OpenAPI().info(information).servers(List.of(pcLocal, localhost));
    }
}
