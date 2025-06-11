import datamodel.ProductsList;
import datamodel.User;
import com.google.gson.Gson;

import java.io.*;
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
        // Inicjalizacja danych
        users = User.readUsers("users.txt");
        for (User user : users) {
            user.readProductLists();
        }
        availableProductList.loadFromFile("lists/0.txt");

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
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Odbierz nazwę użytkownika jako tekst
            String username = in.readLine();
            System.out.println("<- Otrzymano żądanie od: " + username);

            // Znajdź użytkownika
            User user = findUser(username);

            // Zamień na JSON i wyślij
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            out.println(userJson); // wyśle "null" jeśli user == null

            System.out.println(user != null
                    ? "-> Wysłano dane użytkownika: " + user.getName()
                    : "Nie znaleziono użytkownika: " + username
            );

        } catch (IOException e) {
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
