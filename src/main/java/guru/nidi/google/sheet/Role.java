package guru.nidi.google.sheet;

/**
 *
 */
public enum Role {
    OWNER("owner"), READER("reader"), WRITER("writer");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
