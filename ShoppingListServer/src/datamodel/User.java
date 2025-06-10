package datamodel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String name;
    final private int id;

    private ArrayList<Integer> productListsID;
    private ArrayList<ProductsList> productLists;

    public User(String name, int id, ArrayList<Integer> productListsID) {
        this.name = name;
        this.id = id;
        this.productListsID = productListsID;
        this.productLists = new ArrayList<>();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getProductListsID() {
        return productListsID;
    }

    public void setProductListsID(ArrayList<Integer> productListsID) {
        this.productListsID = productListsID;
    }

    public static ArrayList<User> readUsers(String filename) {
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {

                // pomijapuste linie i komentarze
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length == 3) {

                    String name = parts[0]; // username
                    int id = Integer.parseInt(parts[1]); // id
                    ArrayList<Integer> productListsID = new ArrayList<>();

                    String[] listIDs = parts[2].split(","); // id list
                    for (String listID : listIDs) {
                        productListsID.add(Integer.parseInt(listID.trim()));
                    }

                    User user = new User(name, id, productListsID);
                    users.add(user);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Błąd podczas wczytywania pliku: " + e.getMessage());
        }
        return users;
    }

    public void readProductLists() {
        productLists = new ArrayList<>();

        for (int id : productListsID) {
            ProductsList list = new ProductsList();
            String filename = "lists/" + id + ".txt";
            list.loadFromFile(filename);
            productLists.add(list);
        }
    }
}
