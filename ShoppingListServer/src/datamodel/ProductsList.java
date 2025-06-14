package datamodel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductsList {

    public int id;
    public String name;
    public boolean isBeingEdited;
    public ArrayList<Integer> UsersID;
    public ArrayList<String> Usernames;

    public ArrayList<Category> categories;

    public ProductsList() {
        this.categories = new ArrayList<>();
        this.UsersID = new ArrayList<>();
        this.Usernames = new ArrayList<>();
    }

    public Category znajdzKategorie(String name) {
        for (Category k : categories) {
            if (k.name.equalsIgnoreCase(name)) {
                return k;
            }
        }
        Category nowa = new Category(name);
        categories.add(nowa);
        return nowa;
    }

    public Product znajdzPozycje(String name, String category) {
        for (Category k : categories) {
            if (k.name.equalsIgnoreCase(category)) {
                for (Product p : k.products) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public void loadFromFile(String filename) throws FileNotFoundException {
        isBeingEdited = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            line = br.readLine();
            String[] header = line.split(";");
            this.id = Integer.parseInt(header[0]);
            this.name = header[1];

            String[] users = header[2].split(",");
            for (String user : users) {
                this.UsersID.add(Integer.parseInt(user));
            }


            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] czesci = line.split(";");
                if (czesci.length < 5) {
                    continue;
                }

                String kategoria = czesci[0].trim();
                String nazwa = czesci[1].trim();
                String jednostka = czesci[2].trim();
                String typ = czesci[3].trim();
                String ilosc = czesci[4].trim();

                double iloscDouble;
                try {
                    iloscDouble = Double.parseDouble(ilosc);
                } catch (NumberFormatException e) {
                    continue;
                }

                Product p = new Product(nazwa, kategoria, jednostka, typ, iloscDouble);
                znajdzKategorie(kategoria).products.add(p);
            }
        } catch (IOException e) {
//            System.err.println("Błąd wczytywania produktów: " + e.getMessage());
        }
        isBeingEdited = false;
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write(id + ";" + name + ";");
            for (int i = 0; i < UsersID.size(); i++) {
                if (i > 0) {
                    writer.write(",");
                }
                writer.write(String.valueOf(UsersID.get(i)));
            }

            writer.newLine();

            for (Category k : categories) {
                for (Product p : k.products) {
                    if (p.getType().equals("int")) {
                        writer.write(p.getCategory() + ";" + p.getName() + ";" + p.getUnit() + ";" + p.getType() + ";" + p.getQuantity() + "\n");
                    } else {
                        writer.write(p.getCategory() + ";" + p.getName() + ";" + p.getUnit() + ";" + p.getType() + ";" + p.getAmount() + "\n");
                    }

                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Błąd zapisywania produktów: " + e.getMessage());
        }
    }

    public void addProduct(Product p) {
        Product poz = znajdzPozycje(p.getName(), p.getCategory());
        if (poz == null) {
            znajdzKategorie(p.getCategory()).products.add(p);
        } else {
            if (poz.getType().equals("int")) {
                poz.setQuantity(poz.getQuantity() + p.getQuantity());
            } else {
                poz.setAmount(poz.getAmount() + p.getAmount());
            }

        }
    }

    public void dropProduct(Product p) {
        znajdzKategorie(p.getCategory()).products.remove(p);
    }

    public void clear() {
        categories.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void synchronizeUsernames(ArrayList<User> allUsers) {
        if (UsersID == null) return;
        Usernames = new ArrayList<>();

        for (User user : allUsers) {
            if (UsersID.contains(user.getId())) {
                Usernames.add(user.getName());
            }
        }
    }

    public String toString(int nr) {

        StringBuilder sb = new StringBuilder();
        sb.append("- - lista " + nr + " - -\n");
        sb.append("ID: " + id + "\n");
        sb.append("Name: " + name + "\n");
        sb.append("UsersId: " + UsersID + "\n");
        sb.append("Usernames: " + Usernames + "\n");
        for(Category k : categories) {
            sb.append(k.name + ":\n");
            for(Product p : k.products) {
                sb.append("\t" + p.name + ": " + p.amount + "/" + p.quantity + "\n");
            }

        }
        sb.append("- - - - - - - -\n");
        return sb.toString();
    }

    public static ArrayList<ProductsList> readProductLists(int max) {
        ArrayList<ProductsList> productsLists = new ArrayList<>();
        for(int i = 0; i < max; i++){
            String filename = "lists/" + i + ".txt";

            ProductsList newList = new ProductsList();
            try{
                newList.loadFromFile(filename);
            }catch (FileNotFoundException e){
                continue;
            }
            if(newList.getName() == null){
                continue;
            }
            productsLists.add(newList);
        }
        return productsLists;
    }

    public static void saveProductLists(ArrayList<ProductsList> productsLists) {
        for(ProductsList p : productsLists) {
            String f = "lists/" + p.getId() + ".txt";
            p.saveToFile(f);
        }
    }

    public static void updateGlobalListsFromUser(User user, ArrayList<ProductsList> allLists) {

        for (ProductsList userList : user.getProductLists()) {
            boolean updated = false;

            for (int i = 0; i < allLists.size(); i++) {
                if (allLists.get(i).getId() == userList.getId()) {
                    allLists.set(i, userList);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                allLists.add(userList);
            }
        }
    }

    public static int getNextAvailableListId(List<ProductsList> existingLists) {
        Set<Integer> usedIds = existingLists.stream()
                .map(ProductsList::getId)
                .collect(Collectors.toSet());

        int id = 1;
        while (usedIds.contains(id)) {
            id++;
        }
        return id;
    }

}
