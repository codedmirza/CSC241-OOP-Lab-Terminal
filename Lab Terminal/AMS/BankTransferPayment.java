// BankTransferPayment.java

public class BankTransferPayment implements PaymentMethod {

    private String accountNumber;
    private String bankName;

    public BankTransferPayment(String accountNumber, String bankName) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
    }

    @Override
    public boolean pay(double amount) {
        // 1. Account number: digits only, must be between 8 and 20 characters long
        if (accountNumber == null || !accountNumber.matches("^\\d{8,20}$")) {
            System.out.println("Bank transfer failed: Account number must contain 8 to 20 digits.");
            return false;
        }

        // 2. Bank name: letters, spaces, dots, and hyphens only (rejects numeric-only inputs)
        if (bankName == null || !bankName.matches("^(?!\\d+$)[a-zA-Z\\s.\\-&]+$")) {
            System.out.println("Bank transfer failed: Bank name cannot be entirely numeric or contain invalid symbols.");
            return false;
        }

        System.out.println("Bank transfer of Rs " + amount + " from " + bankName + " account " + accountNumber + " successful.");
        return true;
    }

    @Override
    public String getMethodName() { return "Bank Transfer"; }
}
