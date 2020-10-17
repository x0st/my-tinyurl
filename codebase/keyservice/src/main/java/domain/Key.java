package domain;

final public class Key {
    private final String key;
    private final boolean used;

    Key(String key) {
        this.key = key;
        this.used = false;
    }

    public static Key create(String key) {
        return new Key(key);
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[10];

        for (int i = 0; i < key.length(); i++) {
            bytes[i] = (byte) key.charAt(i);
        }

        for (int i = key.length(); i < 9; i++) {
            bytes[i] = 0;
        }

        bytes[9] = used ? (byte) 1 : (byte) 0;

        return bytes;
    }

    @Override
    public String toString() {
        return key;
    }
}
