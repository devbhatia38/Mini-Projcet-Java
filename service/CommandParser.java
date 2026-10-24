package service;

public final class CommandParser {
    private CommandParser() {
    }

    public static Command parse(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Command must contain a type, account number and amount");
        }
        try {
            return new Command(TransactionType.valueOf(parts[0].toUpperCase()), parts[1], Long.parseLong(parts[2]));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid transaction command", exception);
        }
    }
}
