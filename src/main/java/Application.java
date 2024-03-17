import datasource.CsvUserDataSource;
import models.User;
import util.CPFValidator;
import util.TableBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Application {

    private static final TableBuilder tableBuilder = new TableBuilder()
            .setName("Lista atual de usuários")
            .setBorders(TableBuilder.Borders.HEADER_ROW_PLAIN)
            .setAlignment(TableBuilder.Alignment.CENTER)
            .addHeaders("Id", "Nome", "CPF", "Telefone", "E-mail");

    private static final Pattern phonePattern = Pattern.compile("(\\(\\d{2}\\)) \\d{5}-\\d{4}");
    private static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static CsvUserDataSource dataSource;
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        dataSource = new CsvUserDataSource();

        boolean keep = true;
        while (keep) {
            System.out.println("1) Listar usuários");
            System.out.println("2) Buscar usuário por CPF");
            System.out.println("3) Adicionar usuário");
            System.out.println("4) Remover usuário");
            System.out.println("5) Encerrar sessão");
            System.out.print("Qual operação deseja executar? ");
            String option = input.nextLine();
            switch (option) {
                case "1" -> listAllUsers();
                case "2" -> searchUserByCpf();
                case "3" -> addNewUser();
                case "4" -> removeUser();
                case "5" -> keep = false;
                default -> System.out.println("Digite um valor válido");
            }

            System.out.println();
        }
    }

    private static void addNewUser() {
        System.out.println();

        System.out.print("Digite um nome: ");
        String name = input.nextLine().trim().replace(",", "");
        boolean isValidName = !name.isBlank();
        while (!isValidName) {
            System.out.print("Digite um nome válido: ");
            name = input.nextLine();
            isValidName = !name.isBlank();
        }

        System.out.print("Digite um CPF: ");
        String cpf = input.nextLine().trim();
        boolean isValidCpf = CPFValidator.isCPF(cpf);
        while (!isValidCpf) {
            System.out.print("Digite um CPF válido: ");
            cpf = input.nextLine();
            isValidCpf = CPFValidator.isCPF(cpf);
        }

        System.out.print("Digite um telefone (xx) xxxxx-xxxx: ");
        String phone = input.nextLine().trim();
        boolean isValidPhone = phonePattern.matcher(phone).matches();
        while (!isValidPhone) {
            System.out.print("Digite um telefone válido (xx) xxxxx-xxxx: ");
            phone = input.nextLine();
            isValidPhone = phonePattern.matcher(phone).matches();
        }

        System.out.print("Digite um e-mail: ");
        String email = input.nextLine().trim();
        boolean isValidEmail = emailPattern.matcher(email).matches();
        while (!isValidEmail) {
            System.out.print("Digite um e-mail válido: ");
            email = input.nextLine();
            isValidEmail = emailPattern.matcher(email).matches();
        }

        User user = new User(User.genId(), name, cpf, phone, email);
        try {
            if (dataSource.add(user)) {
                System.out.println("Novo usuário adicionado");
                System.out.println();
                String[][] row = new String[1][5];
                row[0] = new String[]{String.valueOf(user.id()), user.name(), user.cpf(), user.phone(), user.email()};
                tableBuilder.setValues(row);
                System.out.println(tableBuilder.build());
            } else {
                System.out.println("Não foi possível adicionar novo usuário.");
                System.out.println("Verifique se o usuário já não está cadastrado no sistema.");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu ume erro ao sincronizar o banco de dados.");
        }
    }

    private static void listAllUsers() {
        Map<String, User> map = dataSource.getAll();
        if (!map.isEmpty()) {
            Collection<User> users = dataSource.getAll().values();
            String[][] rows = new String[users.size()][5];

            int index = 0;
            for (User user : users) {
                rows[index] = new String[]{String.valueOf(user.id()), user.name(), user.cpf(), user.phone(), user.email()};
                index++;
            }

            tableBuilder.setValues(rows);

            System.out.println();
            System.out.println(tableBuilder.build());
        } else {
            System.out.println();
            System.out.println("Não existem registros de usuários");
        }
    }
    private static void searchUserByCpf() {
        System.out.print("Digite o CPF: ");
        String cpf = input.nextLine();
        System.out.println();

        User user = dataSource.get(cpf);

        if (user != null) {
            String[][] row = new String[1][5];
            row[0] = new String[]{String.valueOf(user.id()), user.name(), user.cpf(), user.phone(), user.email()};
            tableBuilder.setValues(row);
            System.out.println(tableBuilder.build());
        } else {
            System.out.println("Não existe usuário com o CPF informado");
        }
    }

    private static void removeUser() {
        System.out.print("Digite o CPF: ");
        String cpf = input.nextLine().trim();
        boolean isValidCpf = CPFValidator.isCPF(cpf);
        while (!isValidCpf) {
            System.out.print("Digite um CPF válido: ");
            cpf = input.nextLine();
            isValidCpf = CPFValidator.isCPF(cpf);
        }
        System.out.println();

        try {
            if (dataSource.remove(cpf)) {
                System.out.println("Usuário removido com sucesso!");
            } else {
                System.out.println("Não foi possível remover o usuário.");
                System.out.println("Verifique se o usuário realmente existe no sistema.");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu ume erro ao sincronizar o banco de dados.");
        }
    }
}
