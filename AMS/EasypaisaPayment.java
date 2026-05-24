// EasypaisaPayment.java

public class EasypaisaPayment implements PaymentMethod {

    private String phoneNumber;

    public EasypaisaPayment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean pay(double amount) {
        if (phoneNumber == null || phoneNumber.length() < 10) {
            System.out.println("Easypaisa payment failed: invalid phone number.");
            return false;
        }
        System.out.println("Easypaisa payment of Rs " + amount
                           + " from " + phoneNumber + " successful.");
        return true;
    }

    @Override
    public String getMethodName() { return "Easypaisa"; }
}
