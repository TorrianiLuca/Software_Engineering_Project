package view.tui;

public enum Font {
    CLEAR("\033c\033[2J\033[H\033[3J"),
    RESET("\033[0m"),
    UNDERLINED("\033[4m"),
    BOLD("\033[1m"),
    BOLD_ITALIC("\033[1;3m"),
    BLACK("\033[38;5;0m"),
    GREEN("\033[38;5;28m"),
    WHITE("\033[38;5;15m"),
    YELLOW("\033[38;5;220m"),
    BLUE("\033[38;5;20m"),
    GREY("\033[38;5;243m"),
    LIGHT_BLUE("\033[38;5;14m"),
    PINK("\033[38;5;13m"),
    RED("\033[38;5;160m"),
    PURPLE("\033[38;5;54m"),
    GOLD("\033[38;5;214m"),
    CLEAR_LINE("\033[2K"),
    CURSOR_UP("\033[1A"),
    GRAY("\033[38;5;245m");

    private final String code;
    Font(String code){
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static String clearPreviousLine() {
        return "\033[1A\033[2K\r";
    }

    public static String clearSuccessiveLine() {
        return "\033[1B\033[2K\r";
    }
}
