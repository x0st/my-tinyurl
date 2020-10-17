package core;

final public class Base62Encoder {
    final private String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long b10) {
        if (b10 < 1) throw new IllegalArgumentException("b10 must be positive");

        StringBuilder sb = new StringBuilder();

        while (b10 > 0) {
            sb.append(characters.charAt((int) (b10 % 62)));
            b10 /= 62;
        }

        return sb.toString();
    }
}
