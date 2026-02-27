package com.d1ff.realtimegateway.interceptor.principal;

import java.security.Principal;

public record StompPrincipal(String name) implements Principal {
    @Override
    public String getName(){
        return name;
    }
}
