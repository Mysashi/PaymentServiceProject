package com.authserver.gateway.route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class OrderServiceRoute {

    @Value("${order.route}")
    private String orderRoute;

    @Bean
    public RouterFunction<ServerResponse> orderRoutes() {
        String baseApi = "/api/orders";// No trailing slash needed here
        return route().POST(baseApi, http())
                .POST(baseApi + "/{id}/pay", http())
                .GET(baseApi + "/{id}", http())
                .before(uri(orderRoute))
                .build();
    }

}
