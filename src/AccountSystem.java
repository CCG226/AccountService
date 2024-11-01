import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
public class AccountSystem {

    public static final int DEFAULT_PASSWORD_LENGTH = 8;
    public static final int PASSWORD_LENGTH_MIN = 3;
    public static final int PASSWORD_LENGTH_MAX = 20;
    public static final int USERNAME_LENGTH = 10;
    final String PLAIN_PASSWORD_FILE = "plain.txt";
    final String HASH_PASSWORD_FILE = "hash.txt";
    final String SALT_PASSWORD_FILE = "salt.txt";
    private String username;

    private String password;
    private int passwordLengthMax;
    private int passwordLengthMin;
    public AccountSystem() {
        username = "";
        password = "";
        passwordLengthMax = DEFAULT_PASSWORD_LENGTH;
        passwordLengthMin = PASSWORD_LENGTH_MIN;
    }

    public AccountSystem(int passwordLengthMax) throws Exception {
        username = "";
        password = "";
        if (IsPLengthInBounds(passwordLengthMax) == false) {
            throw new Exception("Password Length Must Be Between Must Be Between " + AccountSystem.PASSWORD_LENGTH_MIN + " - " + AccountSystem.PASSWORD_LENGTH_MAX);
        }
        this.passwordLengthMax = passwordLengthMax;
        this.passwordLengthMin = PASSWORD_LENGTH_MIN;
    }
    public AccountSystem(int passwordLengthMax, int passwordLengthMin) throws Exception {
        username = "";
        password = "";
        if (IsPLengthInBounds(passwordLengthMax) == false) {
            throw new Exception("Max Password Length Must Be Between Must Be Less Than " + (AccountSystem.PASSWORD_LENGTH_MAX + 1));
        }
        if (IsPLengthInBounds(passwordLengthMin) == false) {
            throw new Exception("Min Password Length Must Be Between Must Be Greater Than " + (AccountSystem.PASSWORD_LENGTH_MIN - 1));
        }
        if(IsPLengthLogical(passwordLengthMin, passwordLengthMax) == false)
        {
            throw new Exception("Min Password Size Cannot Be Greater Than Max Password Size");
        }
        this.passwordLengthMax = passwordLengthMax;
        this.passwordLengthMin = passwordLengthMin;
    }

    public int getPasswordLengthMax() {
        return passwordLengthMax;
    }

    public int getPasswordLengthMin() {
        return passwordLengthMin;
    }

    public boolean Authenticate() throws Exception {

        if(AuthPlain() && AuthHash() && AuthSalt())
        {
            return true;
        }
        return false;
    }
    private String ConvertMySaltedHashToHash(String saltedHash) throws Exception {
        int hashStartIndex = saltedHash.indexOf(' ');
        if(hashStartIndex == -1)
        {
            throw new Exception("Invalid Account In " + HASH_PASSWORD_FILE);
        }
        return saltedHash.substring(hashStartIndex + 1);
    }
    private boolean AuthSalt() throws Exception {
        Map<String, String> accounts = GetAllAccounts(SALT_PASSWORD_FILE);

        for(Map.Entry<String, String> account : accounts.entrySet())
        {
            if(account.getKey().equals(username))
            {

                String givenPasswordHash = ConvertPasswordToHash();
                String storedPasswordHash = ConvertMySaltedHashToHash(account.getValue());


                if(storedPasswordHash.equals(givenPasswordHash))
                {
                    return true;
                }
            }
        }

        return false;
    }
    private boolean AuthHash() throws Exception {
        Map<String, String> accounts = GetAllAccounts(HASH_PASSWORD_FILE);

        for(Map.Entry<String, String> account : accounts.entrySet())
        {
            if(account.getKey().equals(username))
            {
                if(account.getValue().equals(ConvertPasswordToHash()))
                {
                    return true;
                }
            }
        }

        return false;
    }
    private boolean AuthPlain() throws Exception {
        Map<String, String> accounts = GetAllAccounts(PLAIN_PASSWORD_FILE);

        for(Map.Entry<String, String> account : accounts.entrySet())
        {
            if(account.getKey().equals(username))
            {
                if(account.getValue().equals(password))
                {
                    return true;
                }
            }
        }

        return false;
    }
    private Map<String, String> GetAllAccounts(String fileName) throws Exception {
        Map<String, String> results = new HashMap<>();

        FileReader reader = new FileReader(fileName);

        BufferedReader bufferedReader = new BufferedReader(reader);

        String account = bufferedReader.readLine();

        while (account != null) {
            int seperatorIndex = account.indexOf(',');

            if(seperatorIndex == -1)
            {
                throw new Exception("Invalid Account File: " + fileName);
            }

            String usernameData = account.substring(0, seperatorIndex);

            String passwordData = account.substring(seperatorIndex + 1);

            results.put(usernameData, passwordData);
            account = bufferedReader.readLine();
        }

        reader.close();

        return results;
    }
    public static boolean IsPLengthLogical(int pLengthMin, int pLengthMax) {
        if (pLengthMin > pLengthMax) {
            return false;
        }
        return true;
    }
    public static boolean IsPLengthInBounds(int pLength) {
        if (pLength < PASSWORD_LENGTH_MIN || pLength > PASSWORD_LENGTH_MAX) {
            return false;
        }
        return true;
    }


    private boolean DoesUsernameExist(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String account = bufferedReader.readLine();
        while (account != null) {
            int seperatorIndex = account.indexOf(',');

            if(seperatorIndex == -1)
            {
                throw new Exception("Invalid Account File: " + fileName);
            }

            String usernameData = account.substring(0, seperatorIndex);

            if(this.username.equals(usernameData))
            {
                return true;
            }

            account = bufferedReader.readLine();
        }

        reader.close();

        return false;
    }

    private boolean AddAccountToFile(String fileName, String usernameData, String passwordData) throws Exception {

        try (FileWriter writer = new FileWriter(fileName, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            if(DoesUsernameExist(fileName))
            {
                throw new AccountSystemException("Username Already Exists!");
            }

            bufferedWriter.write(usernameData + "," + passwordData);
            bufferedWriter.newLine();

            return true;

        } catch (IOException ex) {

            return false;
        }
    }

    private String ConvertPasswordToHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] passwordBytes = password.getBytes("UTF-8");

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] digest = messageDigest.digest(passwordBytes);

        String base64Result = Base64.getEncoder().encodeToString(digest);

        return base64Result;
    }

    private String ConvertPasswordToHashAndSaltIt() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        String base64Salt = Base64.getEncoder().encodeToString(salt);

        String base64HashResult = ConvertPasswordToHash();

        return base64Salt + " " + base64HashResult;
    }
    private boolean StoreHashed() throws Exception {

        return AddAccountToFile(HASH_PASSWORD_FILE, username, ConvertPasswordToHash());

    }

    private boolean StoreSalted() throws Exception {

        return AddAccountToFile(SALT_PASSWORD_FILE, username, ConvertPasswordToHashAndSaltIt());
    }
    private boolean StorePlain() throws Exception {

        return AddAccountToFile(PLAIN_PASSWORD_FILE, username, password);

    }
    public boolean StoreAccount() throws Exception {
        if (password.isEmpty() || username.isEmpty()) {
            throw new Exception("Object Empty");
        }

        if (StorePlain() && StoreHashed() && StoreSalted()) {
            return true;
        }
        return false;
    }

    public void UsernameValid(String uname) throws AccountSystemException {
        if (uname.isEmpty()) {
            throw new AccountSystemException("Username Cannot Be Empty");
        }
        if (uname.length() > USERNAME_LENGTH) {
            throw new AccountSystemException("Username Cannot Be More Than " + USERNAME_LENGTH + " Characters");
        }

        for (int i = 0; i < uname.length(); i++) {
            if (Character.isLetter(uname.charAt(i)) == false) {
                throw new AccountSystemException("Username Can Only Have Alphabetical Characters");
            }
        }
    }

    public void PasswordValid(String pword) throws AccountSystemException {
        if (pword.isEmpty()) {
            throw new AccountSystemException("Password Cannot Be Empty");
        }
        if (pword.length() > passwordLengthMax) {
            throw new AccountSystemException("Password Must Be Less Than " + (passwordLengthMax + 1) + " Characters");
        }
        if (pword.length() < passwordLengthMin) {
            throw new AccountSystemException("Password Must Be More Than " + (passwordLengthMin - 1) + " Characters");
        }
        int lowerCnter = 0;
        for (int i = 0; i < pword.length(); i++) {
            if (Character.isLetter(pword.charAt(i))) {
                if (Character.isLowerCase(pword.charAt(i))) {
                    lowerCnter++;
                }

            }

        }
        if (lowerCnter != pword.length()) {
            throw new AccountSystemException("Password Can Only Have Lowercase Alphabetical Characters (a-z)");
        }
    }
    public void SetPassword(String password) throws AccountSystemException {
        PasswordValid(password);
        this.password = password;
    }

    public String GetUsername() {
        return username;
    }

    public void SetUsername(String username) throws AccountSystemException {
        UsernameValid(username);
        this.username = username;
    }


}
