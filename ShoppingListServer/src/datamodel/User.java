package datamodel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String name;
    private int id;
    private ArrayList<Integer> productListsID;
    private ArrayList<ProductsList> productLists;


    public User(String name, int id, ArrayList<Integer> productListsID) {
        this.name = name;
        this.id = id;
        this.productListsID = productListsID;
        this.productLists = new ArrayList<>();

    }

    public User() {
        this.productListsID = new ArrayList<>();
        this.productLists = new ArrayList<>();
    }

    // getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<ProductsList> getProductLists() {
        return productLists;
    }
    public void setProductLists(ArrayList<ProductsList> productLists) {
        this.productLists = productLists;
    }

    public ArrayList<Integer> getProductListsID() {
        return productListsID;
    }
    public void setProductListsID(ArrayList<Integer> productListsID) {
        this.productListsID = productListsID;
    }

    public void addProductLists(ProductsList productList) {
        this.productLists.add(productList);
    }

    // file handling
    public static ArrayList<User> readUsers(String filename) {
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {

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
    public String parseUserToFileFormat(){
        StringBuilder sb = new StringBuilder();
        sb.append(name + ";" + id + ";");
        for (int i = 0; i < productListsID.size(); i++) {
            sb.append(productListsID.get(i));
            if (i < productListsID.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
    public static void writeUsersToFile(String filename, ArrayList<User> users) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("#username#id#listy\n\n");
            for(User user : users) {
                writer.write(user.parseUserToFileFormat() + "\n");
            }

        } catch (IOException e) {
            System.err.println("Błąd zapisywania produktów: " + e.getMessage());
        }
    }

    // synch
    public static void refreshUsers(ArrayList<User> users, ArrayList<ProductsList> allLists) {
        Map<Integer, ProductsList> listMap = new HashMap<>();
        for (ProductsList list : allLists) {
            listMap.put(list.getId(), list);
        }

        for (User user : users) {
            ArrayList<ProductsList> refreshedLists = new ArrayList<>();
            for (Integer listId : user.getProductListsID()) {
                ProductsList match = listMap.get(listId);
                if (match != null) {
                    refreshedLists.add(match);
                } else {
                    System.err.println("Brak listy o ID: " + listId + " dla użytkownika: " + user.getName());
                }
            }
            user.setProductLists(refreshedLists);
        }
    }

    // debug
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# # # # # # # # # # #\n");
        sb.append("Name: " + name + "\n");
        sb.append("ID: " + id + "\n");
        sb.append("ProductListsID: " + productListsID + "\n");

        int i = 1;
        for (ProductsList list : productLists) {
            sb.append(list.toString(i));
            i++;
        }
        sb.append("# # # # # # # # # # #\n");
        return sb.toString();
    }

}
