import datamodel.ProductsList;
import datamodel.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static ArrayList<User> users = new ArrayList<>(); // użytkownicy
    static ProductsList availableProductList = new ProductsList();

    static final int SOCKET_PORT = 5000;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();



    public static void main(String[] args) {
//        server data init
        users = User.readUsers("users.txt");
        for (User user : users) {
            user.readProductLists();
        }
        availableProductList.loadFromFile("lists/available.txt");


        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {

            System.out.println("Serwer nasłuchuje na porcie " + SOCKET_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (InterruptedException e) {
                        System.err.println("Błąd - przerwano wątek");
                    }

                });
            }

        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    public static User findUser(String username) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void handleClient(Socket clientSocket) throws InterruptedException {
        try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());) {

            oos.flush();

            String username = (String) ois.readObject();
            System.out.println("Otrzymano żądanie od: " + username);

            // Wyszukiwanie użytkownika w bazie danych
            User user = findUser(username);

            // Wysyłanie obiektu User do klienta
            if (user != null) {
                oos.writeObject(user);
                System.out.println("Wysłano dane użytkownika: " + user.getName());
            } else {
                oos.writeObject(null); // Jeśli użytkownik nie istnieje
                System.out.println("Nie znaleziono użytkownika: " + username);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Błąd podczas zamykania gniazda klienta: " + e.getMessage());
            }
        }
    }
}
