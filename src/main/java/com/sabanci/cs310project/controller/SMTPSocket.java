package com.sabanci.cs310project.controller;

import java.io.*;
import java.net.Socket;

public class SMTPSocket {
    private final String smtpServer;
    private final int port;

    public SMTPSocket(String smtpServer, int port) {
        this.smtpServer = smtpServer;
        this.port = port;
    }

    public void sendEmail(String from, String to, String subject, String body) {
        try (Socket socket = new Socket(smtpServer, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Read the initial response from SMTP server
            readResponse(reader);

            // HELO command
            sendCommand(writer, reader, "HELO " + smtpServer);

            // MAIL FROM command
            sendCommand(writer, reader, "MAIL FROM:<" + from + ">");

            // RCPT TO command
            sendCommand(writer, reader, "RCPT TO:<" + to + ">");

            // DATA command
            sendCommand(writer, reader, "DATA");

            // Message headers and body
            writer.write("From: " + from + "\r\n");
            writer.write("To: " + to + "\r\n");
            writer.write("Subject: " + subject + "\r\n");
            writer.write("\r\n");
            writer.write(body);
            writer.write("\r\n.\r\n");
            writer.flush();

            // Read the response for DATA
            readResponse(reader);

            // QUIT command
            sendCommand(writer, reader, "QUIT");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(BufferedWriter writer, BufferedReader reader, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        readResponse(reader);
    }

    private void readResponse(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null && line.startsWith("4") || line.startsWith("5")) {
            throw new IOException("SMTP error: " + line);
        }
    }
}