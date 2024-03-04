package de.fred.microservices.apigateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f
                                .addRequestHeader("testheader", "testheadervalue")
                                .addRequestParameter("test", "testvalue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p
                        .path("/currency-exchange/**")  //anstatt /currency-exchange/currency-exchange/path einzugeben, möchte ich nur über /currency-exchange/path die URL aufrufen
                        .uri("lb://currency-exchange")) // Loadbalancing auf dem Path vom namen, der in Eureka hinterlegt ist
                .route(p -> p
                        .path("/currency-conversion/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p
                        .path("/currency-conversion-feign/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p
                        .path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath(
                                "/currency-conversion-new/(?<segment>.*)", //reguläre Expression, um nachkommende Segmente zu definieren...
                                "/currency-conversion-feign/${segment}" // ... und hier wieder einzufügen
                        ))
                        .uri("lb://currency-conversion")
                )
                .build();
    }
}