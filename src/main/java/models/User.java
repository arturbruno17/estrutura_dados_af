package models;

public record User(long id, String name, String cpf, String phone, String email) {

    private static long currentId = 1L;

    public static long genId() {
        return currentId++;
    }

    public static void setCurrentId(long id) {
        currentId = id;
    }
}
