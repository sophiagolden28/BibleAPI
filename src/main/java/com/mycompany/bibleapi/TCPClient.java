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

class TCPClient {
    
    //IF NOTHING HAPPENS, CHECK THE SERVER FOR AN ERROR MESSAGE

    //resolved the problem where it would keep the same verse even after rerunning the client
    //fixed the problem where only one verse will print (there was a problem reading in only one line)
    public static void main(String argv[]) throws Exception {
        String reference;
        String referenceText = "";

        BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));

        Socket connectSocket = new Socket("127.0.0.1", 6789);

        DataOutputStream outToServer
                = new DataOutputStream(connectSocket.getOutputStream());

        BufferedReader inFromServer
                = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));

        //TAKE IN VERSE FORMATTED TEXT FROM INPUT after telling them how to format it
        System.out.println("Which Bible verse would you like the text for?\n(Format your answer like this: book+chapter:verse (example: john+1:1))");
        reference = inFromUser.readLine();

        //give the server the reference
        outToServer.writeBytes(reference + "\n");

        //try-catch in case they don't put the format in right or something
        try {

            //ERROR WAS ONLY READING IN ONLY THE ONE LINE, now using a loop to get all the lines  
            
            //get the lines
            String line;
            while ((line = inFromServer.readLine()) != null) {
                referenceText += line + "\n";
            }

            //referenceText = inFromServer.readLine();
            //print the text
            System.out.println("\nVerse text found:\n" + referenceText);

            connectSocket.close();

        } catch (Exception e) {

            System.out.println("Error: Verse Not Found");
            connectSocket.close();

        }

    }
}
