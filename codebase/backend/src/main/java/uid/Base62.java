package uid;

final public class Base62 {
    final private String characters;

    public Base62() {
        this.characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    }

    public String encodeBase10(long b10) {
        if (b10 < 1) throw new IllegalArgumentException("b10 must be positive");

        StringBuilder sb = new StringBuilder();

        while (b10 > 0) {
            sb.append(characters.charAt((int) (b10 % 62)));
            b10 /= 62;
        }

        return sb.toString();
    }
}
