package com.cheche365.cheche.partner.web.filter;

import com.cheche365.cheche.signature.APISignature;
import com.cheche365.cheche.signature.Parameters;
import com.cheche365.cheche.signature.Secrets;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.cheche365.cheche.signature.client.RequestWrapper;

import javax.ws.rs.ext.Providers;

/**
 * Created by zhengwei on 1/7/16.
 */
public class ClientSignatureFilter extends ClientFilter {

    private final Providers providers;
    private final Parameters parameters;
    final Secrets secrets;

    public ClientSignatureFilter(final Providers providers, final Parameters parameters, final Secrets secrets) {
        this.providers = providers;
        this.parameters = parameters;
        this.secrets = secrets;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        if (providers == null || parameters == null || secrets == null) {
            throw new NullPointerException();
        }
        APISignature.sign(new RequestWrapper(request, providers), parameters, secrets);
        return getNext().handle(request);
    }
}
