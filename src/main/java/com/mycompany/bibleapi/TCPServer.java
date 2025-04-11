/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bibleapi;

/**
 *
 * @author Sound Room
 */
import java.io.*;
import java.net.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class TCPServer {

    //PROBLEM - ONLY GETS THE TEXT FROM THE FIRST VERSE, ERRORS IF MORE THAN 3 VERSES REQUESTED, JSONARRAY BUG?????
    //process:
    //get the reference from the client
    //connect to api
    //get the verse text from the api
    //return it to the client
    public static void main(String argv[]) throws Exception {

        ServerSocket doorbellSocket = new ServerSocket(6789);

        while (true) {

            try {

                //learned from trial and error that these have to be reset and in the while loop so that
                //you don't get the same verse text over and over again
                String clientReference;
                String clientReferenceText = "";

                System.out.println("Waiting for client.......");

                Socket connectSocket = doorbellSocket.accept();

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));

                DataOutputStream outToClient
                        = new DataOutputStream(connectSocket.getOutputStream());

                //HERE IS WHERE STUFF IS TAKEN IN AND MODIFIED
                clientReference = inFromClient.readLine();

                //here is where we get the api and the bible verse text
                //Creating Request
                CloseableHttpClient httpclient = HttpClients.createDefault();

                HttpGet httpget = new HttpGet("https://bible-api.com/" + clientReference);

                URI uri = new URIBuilder(httpget.getURI())
                        .build();

                httpget.setURI(uri);

                // Create a custom response handler
                ResponseHandler<String> responseHandler = (org.apache.http.HttpResponse hr) -> {
                    int status = hr.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = hr.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                };

                //Executing request
                String responseBody = "";

                responseBody = httpclient.execute(httpget, responseHandler);

                //make jsonobject
                JSONObject json = new JSONObject(responseBody);

                //make jsonarray for verses
                JSONArray jsonArray = json.getJSONArray("verses");

                //loop through it and add the verse text to the thing we'll send to the client
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject verse = jsonArray.getJSONObject(i);
                    clientReferenceText = clientReferenceText + verse.getString("text");

                }

                //sending the text to the client
                outToClient.writeBytes(clientReferenceText + "\n");

                //apparently you have to close the socket after you write stuff back.....
                connectSocket.close();

            } catch (IOException | URISyntaxException | JSONException e) {
                System.out.println("Error!");
            }

        }

    }

}
