public class AccountSystemException  extends Exception{

    public AccountSystemException() {
        super("Account System Error Occurred");
    }

    public AccountSystemException(String message) {
        super(message);
    }
}
