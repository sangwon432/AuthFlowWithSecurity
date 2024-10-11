package com.sangwon.authflowwithsecurity.utilty;


import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.springframework.http.HttpMethod.*;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({ WebEndpointProperties.class })
public class ApiEndpointSecurityInspector {

    private final WebEndpointProperties webEndpointProperties;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Getter
    private List<String> publicGetEndpoints = new ArrayList<String>();

    @Getter
    private List<String> publicPostEndpoints = new ArrayList<String>();

    @Getter
    private List<String> publicPutEndpoints = new ArrayList<String>();

    @PostConstruct
    public void init() {
        final var handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach(((requestInfo, handlerMethod) -> {
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

            //Swagger 관련 부분 추가

            final var actuatorEndpoints = getActuatorEndpoints();
            publicGetEndpoints.addAll(actuatorEndpoints);
        }));
    }

    // Get으로 허용되는 부분들을 모두 허용해줄 것인가? security configuraiton에 configure 부분 코드

    public Boolean isUnsecureRequest(@Nonnull final HttpServletRequest request) {
        final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
        var unsecuredApiPaths = getUnsecuredApiPaths(requestHttpMethod);
        unsecuredApiPaths = Optional.ofNullable(unsecuredApiPaths).orElseGet(ArrayList::new);

        return unsecuredApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
    }

    private List<String> getUnsecuredApiPaths(@NotNull final HttpMethod httpMethod) {
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

