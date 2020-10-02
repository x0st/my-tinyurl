package domain;

final public class LongUrl {
    private final String url;

    public LongUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}