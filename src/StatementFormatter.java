public final class StatementFormatter {
    private StatementFormatter() {
    }

    public static String buildStatement(Account account) {
        return new StringBuilder()
                .append("Account statement\n")
                .append("Account number: ").append(account.getAccountNumber()).append('\n')
                .append("Owner: ").append(account.getOwnerName()).append('\n')
                .append("Balance: ").append(account.getBalance()).append('\n')
                .append("Status: ").append(account.isActive() ? "active" : "inactive")
                .toString();
    }
}
