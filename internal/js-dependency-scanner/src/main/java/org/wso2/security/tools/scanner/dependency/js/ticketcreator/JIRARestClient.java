/*
 *
 *   Copyright (c) 2018, WSO2 Inc., WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.security.tools.scanner.dependency.js.ticketcreator;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.wso2.security.tools.scanner.dependency.js.exception.TicketCreatorException;

import java.io.File;
import java.io.IOException;
import javax.naming.AuthenticationException;

/**
 * REST client class to invoke JIRA API calls. This class responsible to invoke
 * 1. Get API.
 * 2. Post API.
 * 3. Post comment API.
 * 4. Update ticket with file API.
 */
public class JIRARestClient {

    private static final Logger log = Logger.getLogger(JIRARestClient.class);

    /**
     * Invoke Get method
     *
     * @param auth credentials info of JIRA
     * @param url  url to be invoked.
     * @return response entity of get method.
     * @throws AuthenticationException Exception occurred while authenticate the JIRA
     */
    public String invokeGetMethod(String auth, String url)
            throws AuthenticationException {
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.header("Authorization", "Basic " + auth)
                .type("application/json")
                .accept("application/json").get(ClientResponse.class);
        int statusCode = response.getStatus();
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new AuthenticationException("Invalid Username or Password");
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            throw new AuthenticationException("Forbidden");
        }
        return response.getEntity(String.class);
    }

    /**
     * Invoke Post Method to create issue Ticket.
     *
     * @param auth credentials info of JIRA
     * @param url  url to be invoked.
     * @param data data of API call.
     * @return response entity.
     * @throws AuthenticationException Exception occurred while authenticate the JIRA.
     */
    public String invokePostMethod(String auth, String url, String data)
            throws AuthenticationException {
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
                .accept("application/json").post(ClientResponse.class, data);
        int statusCode = response.getStatus();
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new AuthenticationException("Invalid Username or Password");
        }
        return response.getEntity(String.class);
    }

    /**
     * Invoke Post method to add comment to created ticket.
     *
     * @param auth credentials info of JIRA.
     * @param url  url to be invoked.
     * @param data data of API call.
     * @throws AuthenticationException Exception occurred while authenticate the JIRA.
     */
    public void invokePostComment(String auth, String url, String data)
            throws AuthenticationException {
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        WebResource.Builder builder = webResource.header("Authorization", "Basic " + auth);
        builder = builder.type("application/json");
        builder = builder.accept("application/json");
        ClientResponse response = builder.post(ClientResponse.class, data);
        int statusCode = response.getStatus();
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new AuthenticationException("Invalid Username or Password");
        }
    }


    /**
     * Invoke put method to attach file for particular ticket.
     *
     * @param auth credentials info of JIRA.
     * @param url  url to be invoked.
     * @param path path of the file to be attached.
     * @throws TicketCreatorException Exception occurred while attaching the file with ticket.
     */
    public void invokePutMethodWithFile(String auth, String url, String path) throws TicketCreatorException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("X-Atlassian-Token", "nocheck");
        httppost.setHeader("Authorization", "Basic " + auth);
        File fileToUpload = new File(path);
        FileBody fileBody = new FileBody(fileToUpload);
        HttpEntity entity = MultipartEntityBuilder.create().addPart("file", fileBody).build();
        httppost.setEntity(entity);
        CloseableHttpResponse response;
        try {
            response = httpclient.execute(httppost);
            log.info("[JS_SEC_DAILY_SCAN] File attached with ticket : " + response.toString());
        } catch (IOException e) {
            throw new TicketCreatorException("File upload failed while attaching the scan report with issue ticket: " +
                    path, e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("Exception occurred while closing the http connection", e);
            }
        }
    }

}
