package datasource;

import models.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CsvUserDataSource implements DataSource<User> {

    private final int ID_INDEX = 0;
    private final int NAME_INDEX = 1;
    private final int CPF_INDEX = 2;
    private final int PHONE_INDEX = 3;
    private final int EMAIL_INDEX = 4;

    private final Map<String, User> users;
    private File csvFile = new File(System.getProperty("user.dir") + "/database.csv");

    public CsvUserDataSource() throws IOException {
        if (!this.csvFile.exists()) {
            this.csvFile.createNewFile();
        }
        this.users = this.getDataFromFile();
    }

    public CsvUserDataSource(File csvFile) throws IOException {
        assert csvFile != null;
        this.csvFile = csvFile;
        if (!this.csvFile.exists()) {
            this.csvFile.createNewFile();
        }
        this.users = this.getDataFromFile();
    }

    private Map<String, User> getDataFromFile() throws FileNotFoundException {
        Map<String, User> users = new HashMap<>();
        Scanner scannerFile = new Scanner(csvFile);

        long currentId = -1;

        while (scannerFile.hasNextLine()) {
            String[] csvData = scannerFile.nextLine().split(",");
            long userId = Long.parseLong(csvData[ID_INDEX]);
            String userName = csvData[NAME_INDEX];
            String userCpf = csvData[CPF_INDEX];
            String userPhone = csvData[PHONE_INDEX];
            String userEmail = csvData[EMAIL_INDEX];
            User newUser = new User(userId, userName, userCpf, userPhone, userEmail);
            users.put(newUser.cpf(), newUser);

            if (currentId < userId) {
                currentId = userId;
            }
        }

        User.setCurrentId(Long.max(++currentId, 1L));

        return users;
    }

    private void sync() throws IOException {
        FileWriter writer = new FileWriter(this.csvFile);
        BufferedWriter buff = new BufferedWriter(writer);
        for (User user: this.users.values()) {
            buff.write(String.format("%d,%s,%s,%s,%s", user.id(), user.name(), user.cpf(), user.phone(), user.email()));
            buff.newLine();
        }
        buff.flush();
        buff.close();
    }

    @Override
    public Map<String, User> getAll() {
        return users;
    }

    @Override
    public boolean add(User user) throws IOException {
        if (this.users.containsKey(user.cpf())) return false;
        this.users.put(user.cpf(), user);
        this.sync();
        return this.users.containsKey(user.cpf());
    }

    @Override
    public User get(String cpf) {
        return this.users.get(cpf);
    }

    @Override
    public boolean remove(String cpf) throws IOException {
        if (!this.users.containsKey(cpf)) return false;
        this.users.remove(cpf);
        this.sync();
        return !this.users.containsKey(cpf);
    }
}
