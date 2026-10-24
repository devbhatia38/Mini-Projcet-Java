import model.Account;
import model.CurrentAccount;
import model.Customer;
import model.SavingsAccount;
import service.Repository;

public class Practical13Demo {
    public static void main(String[] args) {
        Repository<Account> accounts = new Repository<>();
        Account first = new SavingsAccount("Riya", 5000, 500);
        Account second = new CurrentAccount("Aarav", 2000, 5000);
        Account third = new SavingsAccount("Mira", 9000, 1000);
        accounts.saveAll(first, second, third);
        System.out.println("Lookup: " + accounts.findById(second.getAccountNumber()));
        accounts.findAll().forEach(System.out::println);

        Repository<Customer> customers = new Repository<>();
        Customer customer = new Customer("Riya", "riya@example.com", "9876543210");
        customers.save(customer);
        System.out.println("Customer lookup: " + customers.findById(customer.getCustomerId()).getName());
        System.out.println("Average: " + Repository.average(new Integer[]{10, 20, 30}));
    }
}
