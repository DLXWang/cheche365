package com.cheche365.cheche.externalapi.filter

import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientRequest
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.filter.ClientFilter

/**
 * Created by zhengwei on 07/02/2018.
 */
class ShortUrlHeaderFilter extends ClientFilter {

    static final String ACCESS_TOKEN = 'uaKDsqoGOj|1548950399|793f9bdfb751d433bc7986e29b8a53b2'

    @Override
    ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        request.headers.add('access-token', ACCESS_TOKEN)
        return getNext().handle(request)
    }
}
