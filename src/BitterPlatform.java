import java.io.*;
import java.util.Scanner;

/**
 * his class will allow the user to interact with the databases by logging in
 * and out of the social network, listing the Users that specific User follows,
 * allowing the User to follow and unfollow other Users, and listing all the
 * Users in that social network.
 *
 * @author Yash Jain
 *         SBU ID: 109885836
 *         email: yash.jain@stonybrook.edu
 *         HW 6 CSE 214
 *         Section 10 Daniel Scanteianu
 *         Grading TA: Anand Aiyer
 */
public class BitterPlatform {
    //Data field
    private static Bitter bitter = new Bitter();
    private static Account account;
    private static String email;

    public static void main(String[] args){
        //Deserialize an object
        try {
            FileInputStream fileIn = new FileInputStream("src\\Bitter.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            bitter = (Bitter) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Welcome to Bitter! Previous data loaded!");
        } catch (IOException e) {
            System.out.println("Welcome to Bitter! Previous data not found!");
        } catch (ClassNotFoundException e) {
            System.out.println("Welcome to Bitter! Previous data not found!");
        }

        //Variables
        String choice = "", name = "", pass = "";
        Scanner input = new Scanner(System.in);

        //Login menu
        while(true){
            System.out.println("L) Login");
            System.out.println("S) Sign Up");
            System.out.println("Q) Quit");
            System.out.print("Please select an option: ");
            choice = input.nextLine(); //User chooses option
            switch (choice.toUpperCase()) {
                case "L": //Login
                    System.out.print("Please enter your email: ");
                    email = input.nextLine();
                    System.out.print("Please enter a password: ");
                    pass = input.nextLine();

                    //Check if email and password are correct
                    if((account = bitter.getAccounts().getAccountInformation(email)) != null)
                        if(pass.equals(account.getPassword().getPassword()))
                            userMenu(); //Grant access
                        else
                            System.out.println("Invalid password.");
                    else
                        System.out.println("Invalid email.");
                    break;
                case "S":   //Sign up
                    System.out.print("Please enter your email: ");
                    email = input.nextLine();
                    System.out.print("Please enter your name: ");
                    name = input.nextLine();
                    System.out.print("Please enter a password: ");

                    //Create Password
                    Password password;
                    while(true) {
                        try {
                            password = new Password(input.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Your password is not secure enough." +
                                    " Please make sure you have at least one upper case and one " +
                                    "lower case letter, one special character, and one number.");
                        }
                    }

                    //Create User and Account objects
                    User user = new User(name, email);
                    account = new Account(name, password);

                    //Add them to the database
                    try {
                        bitter.addUser(email, user, account);
                        System.out.println("Thanks for signing up! Welcome to Bitter!");
                        userMenu();
                    } catch (Exception e) {
                        System.out.println("Invalid email!");
                    }

                    break;
                    case "Q":   //Quit
                        //Save data before exiting
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream("src\\Bitter.ser");
                            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                            out.writeObject(bitter);
                            out.close();
                            fileOutputStream.close();
                            System.out.println("Saved!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //Exit
                        System.out.println("Thanks for using Bitter!");
                        System.exit(1);
                    break;
                default:
                    System.out.println("Invalid choice, please try again");
            }
        }

    }

    public static void userMenu(){
        //Data fields
        Scanner input = new Scanner(System.in);
        String choice;
        User user;
        Account tempAccount;

        while(true) {
            //User Menu
            System.out.println("F) Follow someone");
            System.out.println("U) Unfollow someone");
            System.out.println("V) View Followers");
            System.out.println("S) See who you follow");
            System.out.println("A) List all users");
            System.out.println("L) Logout");
            System.out.println("C) Close your account");
            choice = input.nextLine(); //User chooses option
            switch (choice.toUpperCase()){
                case "F":
                    System.out.print("Please enter the email of the person you would like to follow: ");
                    if((user = bitter.getUsers().getUser(input.nextLine())) != null) {
                        account.follow(user); // Follow the user

                        //The followed user receives a follower
                        tempAccount = bitter.getAccounts().getAccountInformation(user.getEmail());
                        tempAccount.receiveFollower(bitter.getUsers().getUser(email));
                    }else
                        System.out.println("Invalid email, user not found!");
                    break;
                case "U":
                    System.out.print("Please enter the email of the person you would like to unfollow: ");
                    if((user = bitter.getUsers().getUser(input.nextLine())) != null) {
                        account.unfollow(user); //Unfollow the user

                        //The unfollowed user lost a follower
                        tempAccount = bitter.getAccounts().getAccountInformation(user.getEmail());
                        tempAccount.loseFollower(bitter.getUsers().getUser(email));
                    }else
                        System.out.println("Invalid email, user not found!");
                    break;
                case "V":
                    System.out.println(account.printFollowers());
                    break;
                case "S":
                    System.out.println(account.printFollowing());
                    break;
                case "A":
                    System.out.println(bitter.toString());
                    break;
                case "L":
                    return;
                case "C":
                    for(String s : bitter.getAccounts().keySet()){
                        //Accounts who follow this account will lose them in the Followers/Following list
                        bitter.getAccounts().getAccountInformation(s).loseFollower(bitter.getUsers().getUser(email));
                        bitter.getAccounts().getAccountInformation(s).unfollow(bitter.getUsers().getUser(email));
                    }
                    System.out.println(account.getName() + "'s account has been closed.");
                    bitter.removeUser(email);
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
