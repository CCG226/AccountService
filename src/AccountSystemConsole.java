import java.util.Scanner;

public class AccountSystemConsole {
    public AccountSystemConsole(AccountSetUp set) throws Exception {

        scanner = new Scanner(System.in);

        if(set == AccountSetUp.Default)
        {
            accountSys = new AccountSystem();
        }
        else if(set == AccountSetUp.ChangeMax)
        {
            int maxPasswordSize = (ChangeDefaultMaxLengthRequest()) ? ParsePasswordLength() : AccountSystem.DEFAULT_PASSWORD_LENGTH;

            accountSys = new AccountSystem(maxPasswordSize);

        }
        else if(set == AccountSetUp.ChangeMaxAndMin)
        {
            int minPasswordSize = (ChangeDefaultMinLengthRequest()) ? ParsePasswordLength() : AccountSystem.PASSWORD_LENGTH_MIN;
            int maxPasswordSize = (ChangeDefaultMaxLengthRequest()) ? ParsePasswordLength() : AccountSystem.DEFAULT_PASSWORD_LENGTH;

            accountSys = new AccountSystem(minPasswordSize, maxPasswordSize);
        }
        else {
            throw new RuntimeException("Unknown Set Up Enum "+ set);
        }
    }
    private Scanner scanner;
    private AccountSystem accountSys;

    public int GetMaxPasswordLengthAPI()
    {
        return accountSys.getPasswordLengthMax();
    }
    public int GetMinPasswordLengthAPI()
    {
        return accountSys.getPasswordLengthMin();
    }
    public int GetUsernameLengthAPI()
    {
        return AccountSystem.USERNAME_LENGTH;
    }
    private boolean ChangeDefaultMinLengthRequest()
    {
        System.out.println("Would You Like To Change The Required Minimum Length For Your Password. (Y/N)");
        System.out.println("Default Minimum Length Is " + AccountSystem.PASSWORD_LENGTH_MIN + " Characters.");

        return YesOrNoRequest();
    }
    private boolean ChangeDefaultMaxLengthRequest()  {
        System.out.println("Would You Like To Change The Required Maximum Length For Your Password. (Y/N)");
        System.out.println("Default Maximum Length Is " + AccountSystem.DEFAULT_PASSWORD_LENGTH + " Characters.");

        return YesOrNoRequest();



    }
    public void LoginRequest() throws Exception {
        System.out.println("Enter Username:");

        UsernameRequest();
        System.out.println("Enter Password:");
        PasswordRequest();

        if (accountSys.Authenticate()) {
            System.out.println("Successfully Logged In As " + accountSys.GetUsername());
        } else {
            System.out.println("Failed To Log In As " + accountSys.GetUsername() + ". Invalid Username Or Password");
        }
    }

    private void UsernameRequest() {
        boolean result = false;
        while (!result) {
            try {
                String uName = scanner.nextLine();

                SetUsernameAPI(uName);

                result = true;

            } catch (AccountSystemException ex) {
                System.out.println("Invalid Username: " + ex.getMessage());

            }
        }
    }

    private void PasswordRequest() {
        boolean pSuccess = false;
        while (!pSuccess) {
            try {
                String pword = scanner.nextLine();

                SetPasswordAPI(pword);

                pSuccess = true;

            } catch (AccountSystemException ex) {
                System.out.println("Invalid Password: " + ex.getMessage());

            }
        }
    }
    public void CreateAccountAPI() throws Exception {
        boolean isAccountCreated = false;
        try {
            isAccountCreated = accountSys.StoreAccount();
        } catch (AccountSystemException ex) {
            System.out.println("Failed To Create Account: " + ex.getMessage());
        }
        if (isAccountCreated) {
            System.out.println("Account Created For " + accountSys.GetUsername());
        }
    }
    public void SetUsernameAPI(String username) throws AccountSystemException {
        accountSys.SetUsername(username);
    }
    public void SetPasswordAPI(String password) throws AccountSystemException {
        accountSys.SetPassword(password);
    }
    public void SignUpRequest() throws Exception {
        System.out.println("Create A Username:");
        System.out.println("Requirements: Cant Be Empty. Cant Be More Than " + (AccountSystem.USERNAME_LENGTH) + " Characters. Alphabetical Characters Only");

        UsernameRequest();

        System.out.println("Create A Password:");
        System.out.println("Requirements: Must Be More Than " + (AccountSystem.PASSWORD_LENGTH_MIN - 1) + " Characters And Less Than " + (accountSys.getPasswordLengthMax() + 1) + " Characters." +
                " Lowercase a-z Characters Only");

        PasswordRequest();

        CreateAccountAPI();
    }

    private int ParsePasswordLength() {
        System.out.println("Enter New Password Length Requirement. Must Be Between " + AccountSystem.PASSWORD_LENGTH_MIN + " - " + AccountSystem.PASSWORD_LENGTH_MAX + " Characters.");
        String result;

        do {

            result = scanner.nextLine();

        } while (IsPLengthInputValid(result) == false);

        return Integer.parseInt(result);
    }

    private boolean IsPLengthInputValid(String input) {
        int length;

        try {
            length = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Error: Length Value Must Be A Positive Whole Number!");
            return false;
        }

        if (AccountSystem.IsPLengthInBounds(length) == false) {
            System.out.println("Value Must Be Between " + AccountSystem.PASSWORD_LENGTH_MIN + " - " + AccountSystem.PASSWORD_LENGTH_MAX);
            return false;
        }

        return true;
    }

    public boolean YesOrNoRequest() {
        char yesOrNo = ' ';
        final int NO_INPUT = 0;
        do {
            System.out.println("Please Press 'Y' For Yes And 'N' For No.");

            String res = scanner.nextLine();
            if (res.length() > NO_INPUT) {
                yesOrNo = res.charAt(0);
            }

        } while (Character.toUpperCase(yesOrNo) != 'Y' && Character.toUpperCase(yesOrNo) != 'N');
        if (Character.toUpperCase(yesOrNo) == 'Y') {
            return true;
        }

        return false;
    }
}

