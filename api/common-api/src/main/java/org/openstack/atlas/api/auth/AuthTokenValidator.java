package org.openstack.atlas.api.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.keystone.auth.pojo.exceptions.AuthException;

import java.net.URISyntaxException;

public class AuthTokenValidator {
    private static final Log LOG = LogFactory.getLog(AuthTokenValidator.class);
    private AuthService authService;

    public AuthTokenValidator(AuthService authService) {
        this.authService = authService;
    }

    public String validate(Integer passedAccountId, String authToken) throws AuthException, URISyntaxException {
        LOG.info("Within validate ... about to call AuthService authenticate...");
        return authService.authenticate(passedAccountId, authToken, "cloud");
    }
}
