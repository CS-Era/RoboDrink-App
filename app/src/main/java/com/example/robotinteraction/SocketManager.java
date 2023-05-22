package com.example.robotinteraction;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.util.Log;

public class SocketManager {
    private String serverIp;
    private int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static SocketManager instance = null;

    private MessageListener messageListener;

    private static final String SERVER_IP = "000.000.000";
    private static final int SERVER_PORT = 0000;

    private SocketManager() {
        // Nessun costruttore esterno necessario
    }

    public interface MessageListener {
        void onNewMessage(String message);
    }

    public void setMessageListener(MessageListener listener){
        this.messageListener = listener;
    }

    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        socket = new Socket(SERVER_IP, SERVER_PORT);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("SocketManager", "Failed to connect");


                    }

            }
        }).start();
    }




    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
            instance.connect();
        }
        return instance;
    }


    public String receiveMessage() throws IOException {

        if(in == null){
            throw new IOException("Ho ricevuto un input null dal Server!");
        }
        String line = in.readLine();

        // Log del messaggio ricevuto
        Log.d("SocketManager", "[SERVER] Messaggio ricevuto: " + line);
        if (messageListener != null) {
            messageListener.onNewMessage(line);
        }
        return line;
    }

    public void sendMessage(String message) throws IOException {

        if (out == null) {
            throw new IOException("Il messaggio da inviare è null!");
        }

        out.println(message);

        // Log del messaggio inviato
        Log.d("SocketManager", "[CLIENT] Messaggio inviato: " + message);

        // Se ci sono stati errori nella scrittura, PrintWriter non lancerà un'eccezione,
        // ma potrai comunque controllarlo con checkError().
        if (out.checkError()) {
            throw new IOException();
        }


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            socket.close();
            instance = null;
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public boolean isConnected(){
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void attemptConnection(){
        if(!isConnected()){
            connect();
        }
    }
}