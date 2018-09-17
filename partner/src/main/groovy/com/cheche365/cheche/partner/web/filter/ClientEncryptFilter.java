package com.cheche365.cheche.partner.web.filter;

import com.cheche365.cheche.partner.utils.PartnerEncryptUtil;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class ClientEncryptFilter extends ClientFilter {

    private String password;

    public ClientEncryptFilter(String password) {
        this.password = password;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        String encryptedText = PartnerEncryptUtil.encrypt(String.valueOf(request.getEntity()), password);
        request.setEntity(encryptedText);
        return getNext().handle(request);
    }
}
