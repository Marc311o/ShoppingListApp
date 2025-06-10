import datamodel.ProductsList;
import datamodel.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    static ArrayList<User> users = new ArrayList<>(); // użytkownicy
    static ProductsList availableProductList = new ProductsList();
    static final int SOCKET_PORT = 5000;


    public static void main(String[] args) {
//        server data init
        users = User.readUsers("users.txt");
        for (User user : users) {
            user.readProductLists();
        }
        availableProductList.loadFromFile("lists/available.txt");


//        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
//            System.out.printf("Serwer nasłuchuje na porcie %d...\n", SOCKET_PORT);
//
//            while (true) {
//                Socket socket = serverSocket.accept(); // Akceptowanie połączenia
//                System.out.println("Połączenie nawiązane!");
//
//                // Obsługa klienta
//                try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
//
//                    // Odbieranie nazwy użytkownika (username)
//                    String username = (String) ois.readObject();
//                    System.out.println("Otrzymano username: " + username);
//
//                    // Wyszukiwanie użytkownika w bazie danych
//                    User user = findUser(username);
//
//                    // Wysyłanie obiektu User do klienta
//                    if (user != null) {
//                        try{
//                            oos.writeObject(user);
//                            System.out.println("Wysłano dane użytkownika: " + user);
//                        }catch (IOException e){
//                            System.err.println("blad");
//
//                        }
//
//                    } else {
//                        oos.writeObject(null); // Jeśli użytkownik nie istnieje
//                        System.out.println("Nie znaleziono użytkownika: " + username);
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static User findUser(String username) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
