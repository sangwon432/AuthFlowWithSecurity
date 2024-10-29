package com.sangwon.authflowwithsecurity.utilty;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import com.behl.cerberus.configuration.OpenApiConfigurationProperties;
//import com.behl.cerberus.configuration.PublicEndpoint;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Utility class responsible for evaluating the accessibility of API endpoints
 * based on their security configuration. It works in conjunction with the
 * mappings of controller methods annotated with {@link PublicEndpoint}.
 *
 * @see com.behl.overseer.configuration.PublicEndpoint
 * @see com.behl.overseer.configuration.OpenApiConfigurationProperties
 */
@Component
@RequiredArgsConstructor
//@EnableConfigurationProperties({ OpenApiConfigurationProperties.class, WebEndpointProperties.class})
public class ApiEndpointSecurityInspector {

    private final WebEndpointProperties webEndpointProperties;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
//    private final OpenApiConfigurationProperties openApiConfigurationProperties;

    private static final List<String> SWAGGER_V3_PATHS = List.of("/swagger-ui**/**", "/v3/api-docs**/**");

    @Getter
    private List<String> publicGetEndpoints = new ArrayList<String>();
    @Getter
    private List<String> publicPostEndpoints = new ArrayList<String>();
    @Getter
    private List<String> publicPutEndpoints = new ArrayList<String>();

    /**
     * Initializes the class by gathering public endpoints for various HTTP methods.
     * It identifies designated public endpoints within the application's mappings
     * and adds them to separate lists based on their associated HTTP methods.
     * If OpenAPI is enabled, Swagger endpoints are also considered as public.
     */
    @PostConstruct
    public void init() {
        final var handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestInfo, handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
                final var httpMethod = requestInfo.getMethodsCondition().getMethods().iterator().next().asHttpMethod();
                final var apiPaths = requestInfo.getPathPatternsCondition().getPatternValues();

                if (httpMethod.equals(GET)) {
                    publicGetEndpoints.addAll(apiPaths);
                } else if (httpMethod.equals(POST)) {
                    publicPostEndpoints.addAll(apiPaths);
                } else if (httpMethod.equals(PUT)) {
                    publicPutEndpoints.addAll(apiPaths);
                }
            }
        });

//        final var openApiEnabled = openApiConfigurationProperties.getOpenApi().isEnabled();
//        if (Boolean.TRUE.equals(openApiEnabled)) {
//            publicGetEndpoints.addAll(SWAGGER_V3_PATHS);
//        }

        final var actuatorEndpoints = getActuatorEndpoints();
        publicGetEndpoints.addAll(actuatorEndpoints);
    }

    /**
     * Checks if the provided HTTP request is directed towards an unsecured API endpoint.
     *
     * @param request The HTTP request to inspect.
     * @return {@code true} if the request is to an unsecured API endpoint, {@code false} otherwise.
     */
    public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
        final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
        var unsecuredApiPaths = getUnsecuredApiPaths(requestHttpMethod);
        unsecuredApiPaths = Optional.ofNullable(unsecuredApiPaths).orElseGet(ArrayList::new);

        return unsecuredApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
    }

    /**
     * Retrieves the list of unsecured API paths based on the provided HTTP method.
     *
     * @param httpMethod The HTTP method for which unsecured paths are to be retrieved.
     * @return A list of unsecured API paths for the specified HTTP method.s
     */
    private List<String> getUnsecuredApiPaths(@NonNull final HttpMethod httpMethod) {
        switch (httpMethod) {
            case GET:
                return publicGetEndpoints;
            case POST:
                return publicPostEndpoints;
            case PUT:
                return publicPutEndpoints;
            default:
                return Collections.emptyList();
        }
    }

    private List<String> getActuatorEndpoints() {
        final var basePath = webEndpointProperties.getBasePath();
        final var includedEndpoints = webEndpointProperties.getExposure().getInclude();
        final var excludedEndpoints = webEndpointProperties.getExposure().getExclude();

        return includedEndpoints.stream()
                .filter(Predicate.not(excludedEndpoints::contains))
                .flatMap(endpoint -> Stream.of(
                        String.format("%s/%s", basePath, endpoint),
                        String.format("%s/%s/*", basePath, endpoint)))
                .toList();
    }

}
//
//
//import jakarta.annotation.Nonnull;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Predicate;
//import java.util.stream.Stream;
//
//import static org.springframework.http.HttpMethod.*;
//
//
//@Component
//@RequiredArgsConstructor
//@EnableConfigurationProperties({ WebEndpointProperties.class })
//public class ApiEndpointSecurityInspector {
//
//    private final WebEndpointProperties webEndpointProperties;
//    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
//
//    @Getter
//    private List<String> publicGetEndpoints = new ArrayList<String>();
//
//    @Getter
//    private List<String> publicPostEndpoints = new ArrayList<String>();
//
//    @Getter
//    private List<String> publicPutEndpoints = new ArrayList<String>();
//
//    @PostConstruct
//    public void init() {
//        final var handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//        handlerMethods.forEach(((requestInfo, handlerMethod) -> {
//            if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
//                final var httpMethod = requestInfo.getMethodsCondition().getMethods().iterator().next().asHttpMethod();
//                final var apiPaths = requestInfo.getPathPatternsCondition().getPatternValues();
//
//                if (httpMethod.equals(GET)) {
//                    publicGetEndpoints.addAll(apiPaths);
//                } else if (httpMethod.equals(POST)) {
//                    publicPostEndpoints.addAll(apiPaths);
//                } else if (httpMethod.equals(PUT)) {
//                    publicPutEndpoints.addAll(apiPaths);
//                }
//            }
//
//            //Swagger 관련 부분 추가
//
//            final var actuatorEndpoints = getActuatorEndpoints();
//            publicGetEndpoints.addAll(actuatorEndpoints);
//        }));
//    }
//
//
//    // Get으로 허용되는 부분들을 모두 허용해줄 것인가? security configuraiton에 configure 부분 코드
//
//    public Boolean isUnsecureRequest(@Nonnull final HttpServletRequest request) {
//        final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
//        var unsecuredApiPaths = getUnsecuredApiPaths(requestHttpMethod);
//        unsecuredApiPaths = Optional.ofNullable(unsecuredApiPaths).orElseGet(ArrayList::new);
//
//        return unsecuredApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
//    }
//
//    private List<String> getUnsecuredApiPaths(@NotNull final HttpMethod httpMethod) {
//        switch (httpMethod) {
//            case GET:
//                return publicGetEndpoints;
//            case POST:
//                return publicPostEndpoints;
//            case PUT:
//                return publicPutEndpoints;
//            default:
//                return Collections.emptyList();
//
//        }
//    }
//
//    private List<String> getActuatorEndpoints() {
//        final var basePath = webEndpointProperties.getBasePath();
//        final var includedEndpoints = webEndpointProperties.getExposure().getInclude();
//        final var excludedEndpoints = webEndpointProperties.getExposure().getExclude();
//
//        return includedEndpoints.stream()
//                .filter(Predicate.not(excludedEndpoints::contains))
//                .flatMap(endpoint -> Stream.of(
//                        String.format("%s/%s", basePath, endpoint),
//                        String.format("%s/%s/*", basePath, endpoint)))
//                .toList();
//    }
//}
//
