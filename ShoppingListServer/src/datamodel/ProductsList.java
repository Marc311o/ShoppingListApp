package datamodel;

import java.io.*;
import java.util.*;
import java.util.function.Function;
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



    public Category findCategory(String name) {
        for (Category k : categories) {
            if (k.name.equalsIgnoreCase(name)) {
                return k;
            }
        }
        Category nowa = new Category(name);
        categories.add(nowa);
        return nowa;
    }
    public Product findProduct(String name, String category) {
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


    public void addProduct(Product p) {
        Product poz = findProduct(p.getName(), p.getCategory());
        if (poz == null) {
            findCategory(p.getCategory()).products.add(p);
        } else {
            if (poz.getType().equals("int")) {
                poz.setQuantity(poz.getQuantity() + p.getQuantity());
            } else {
                poz.setAmount(poz.getAmount() + p.getAmount());
            }

        }
    }
    public void dropProduct(Product p) {
        findCategory(p.getCategory()).products.remove(p);
    }
    public void clear() {
        categories.clear();
    }


    // getters and setters
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

    public ArrayList<String> getUsernames() {
        return Usernames;
    }
    public void setUsernames(ArrayList<String> usernames) {
        Usernames = usernames;
    }

    public ArrayList<Integer> getUsersID() {
        return UsersID;
    }
    public void setUsersID(ArrayList<Integer> usersID) {
        UsersID = usersID;
    }


    // file handling
    public static void saveProductLists(ArrayList<ProductsList> productsLists) {
        for(ProductsList p : productsLists) {
            String f = "lists/" + p.getId() + ".txt";
            p.saveToFile(f);
        }
    }
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write(id + ";" + name + ";");
            if(name.equals("dostepne")) {
                writer.write("0\n");
            }
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
                findCategory(kategoria).products.add(p);
            }
        } catch (IOException e) {
//            System.err.println("Błąd wczytywania produktów: " + e.getMessage());
        }
        isBeingEdited = false;
    }


    // SYNCH METODS
    // lists -> users
    public static void assignListsToUsers(ArrayList<User> users, ArrayList<ProductsList> allLists) {
        // mapa ID użytkownika → obiekt User
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
            user.getProductListsID().clear();
            user.getProductLists().clear();
        }

        for (ProductsList list : allLists) {
            for (Integer userId : list.getUsersID()) {
                User user = userMap.get(userId);
                if (user != null) {
                    if (!user.getProductListsID().contains(list.getId())) {
                        user.getProductListsID().add(list.getId());
                    }
                    if (!user.getProductLists().contains(list)) {
                        user.addProductLists(list);
                    }
                } else {
                    System.err.println("Brak użytkownika o ID: " + userId + " (lista ID: " + list.getId() + ")");
                }
            }
        }
    }
    // users -> lists
//    public static void updateGlobalListsFromUser(User user, ArrayList<ProductsList> allLists) {
//        for (ProductsList userList : user.getProductLists()) {
//            boolean updated = false;
//
//            for (int i = 0; i < allLists.size(); i++) {
//                if (allLists.get(i).getId() == userList.getId()) {
//                    allLists.set(i, userList);
//                    updated = true;
//                    break;
//                }
//            }
//
//            if (!updated) {
//                allLists.add(userList);
//            }
//        }
//    }

    public static void updateGlobalListsFromUser(User user, List<ProductsList> allLists) {
        int userId = user.getId();

        Map<Integer, ProductsList> userMap = user.getProductLists().stream()
                .collect(Collectors.toMap(ProductsList::getId, Function.identity()));

        Iterator<ProductsList> it = allLists.iterator();
        while (it.hasNext()) {
            ProductsList global = it.next();
            int gid = global.getId();

            if (userMap.containsKey(gid)) {
                // użytkownik ma listę → aktualizujemy zawartość
                ProductsList updated = userMap.get(gid);
                global.setName(updated.getName());
                global.setCategories(new ArrayList<>(updated.getCategories()));
                global.setUsersID(new ArrayList<>(updated.getUsersID()));
                global.setUsernames(new ArrayList<>(updated.getUsernames()));
            } else {
                // użytkownik usunął listę → usuwamy jego ID
                if (global.getUsersID().removeIf(id -> id == userId)) {
                    if (global.getUsersID().isEmpty()) {
                        it.remove();
                    }
                }
            }
        }

        // Dodajemy listy, które użytkownik ma, ale ich nie ma globalnie
        userMap.keySet().stream()
                .filter(id -> allLists.stream().noneMatch(gl -> gl.getId() == id))
                .map(userMap::get)
                .forEach(allLists::add);
    }



    // usersid -> usernames
    public void synchronizeUsernames(ArrayList<User> allUsers) {
        if (UsersID == null) return;
        Usernames = new ArrayList<>();

        Map<Integer, String> idToName = new HashMap<>();
        for (User user : allUsers) {
            idToName.put(user.getId(), user.getName());
        }

        for (Integer id : UsersID) {
            String name = idToName.get(id);
            if (name != null) {
                Usernames.add(name);
            }
        }
    }
    // usernames -> usersid
    public void synchronizeIDs(ArrayList<User> allUsers) {
        UsersID = new ArrayList<>();

        Map<String, Integer> nameToId = new HashMap<>();
        for (User user : allUsers) {
            nameToId.put(user.getName(), user.getId());
        }

        for (String name : Usernames) {
            Integer id = nameToId.get(name);
            if (id != null) {
                UsersID.add(id);
            }
        }
    }


    // other
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


    // debugging
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
}
