package classifier.crawlers;

public enum Channels {

    Auto("600"),
    Sportas("903"),
    Verslas("907"),
    Mokslas("908");

    private final String channel;

    Channels(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

}
