

public class Main {



    public static void main(String[] args) throws Exception {

       AccountSystemConsole accountSysApp = new AccountSystemConsole(AccountSetUp.ChangeMax);

        System.out.println("Do You Already Have An Account? (Y/N)");
        boolean canLogin = accountSysApp.YesOrNoRequest();


        if (canLogin) {
            accountSysApp.LoginRequest();
        } else {
            accountSysApp.SignUpRequest();
        }
    }

}