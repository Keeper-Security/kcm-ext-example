/*
 * Copyright (C) 2024 Keeper Security, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.keepersecurity.kcm;

import java.util.Collections;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.TokenInjectingUserContext;
import org.apache.guacamole.net.auth.UserContext;

/**
 * Example extension for KCM/Guacamole that demonstrates the basics of using
 * decoration to inject arbitrary parameter tokens. This example injects a
 * token named "EXAMPLE_TOKEN" that contains the user's username.
 */
public class ExampleAuthenticationProvider extends AbstractAuthenticationProvider {

    @Override
    public String getIdentifier() {

        //
        // All authentication providers must have a unique identifier so that
        // they can be separately addressed by the REST API of the webapp.
        //
        // This also feeds into the naming of the translation namespace used to
        // define the human-readable name of the extension within the webapp
        // UI. The identifier returned here will given the prefix "DATA_SOURCE_"
        // and will be transformed into UPPER_CASE_WITH_UNDERSCORES, thus
        // producing "DATA_SOURCE_KCM_EXT_EXAMPLE" (see the contents of
        // "src/main/resources/translations/en.json").
        //
        return "kcm-ext-example";

    }

    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {

        //
        // To inject arbitrary parameter tokens into connection configurations,
        // an extension can decorate the UserContext of other extensions. A
        // "UserContext" is the set of data exposed by an extension and is
        // scoped to just that extension. The decorate() function will receive
        // the UserContexts of all other extensions that apply to the user's
        // session, allowing this extension to override the behavior of each.
        //
        // The TokenInjectingUserContext class exists to make this specific use
        // case easier and involve less boilerplate code.
        //

        return new TokenInjectingUserContext(context) {

            @Override
            protected Map<String, String> getTokens(Connection connection) throws GuacamoleException {

                //
                // This function is invoked whenever a user is trying to
                // connect to the given Connection. This is specifically
                // relevant to connections that are not being accessed through
                // a balancing connection group (see below).
                //
                // Here, you would pull whatever information is relevant from
                // the Connection object, populate a Map with tokens as you see
                // fit, and then return that Map. The tokens defined within the
                // Map will be made available to the Connection.
                //
     
                // Pull the username of the current user (the identifier of the
                // user that authenticated) and inject it as the value of a
                // parameter token named "EXAMPLE_TOKEN".
                String username = authenticatedUser.getIdentifier();
                return Collections.singletonMap("EXAMPLE_TOKEN", username);

            }

            // If needed, the TokenInjectingUserContext base class also
            // provides a getTokens() function that can be overridden to
            // inject tokens that apply to Connections within a balancing
            // ConnectionGroup.
            
        };
        
    }

}
