package com.brclys.thct.openApiGenSrc.api;


import jakarta.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-29T12:22:03.964972322+01:00[Europe/London]", comments = "Generator version: 7.14.0")
@Controller
@RequestMapping("${openapi.takeHomeTechTask.base-path:}")
public class V1ApiController implements V1Api {

    private final V1ApiDelegate delegate;

    public V1ApiController(@Autowired(required = false) V1ApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new V1ApiDelegate() {});
    }

    @Override
    public V1ApiDelegate getDelegate() {
        return delegate;
    }

}
