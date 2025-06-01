import java.util.*;

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
}

class Car {
    private String carId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean isAvailable;

    public Car(String carId, String brand, String model, double basePricePerDay) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
    }

    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double calculatePrice(int rentalDays) {
        return basePricePerDay * rentalDays;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void rent() {
        isAvailable = false;
    }

    public void returnCar() {
        isAvailable = true;
    }
}

class Customer {
    private String customerId;
    private String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

class Rental {
    private Car car;
    private Customer customer;
    private int days;

    public Rental(Car car, Customer customer, int days) {
        this.car = car;
        this.customer = customer;
        this.days = days;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;
    private Map<String, User> users;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
        users = new HashMap<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.authenticate(password);
    }

    public void rentCar(Car car, Customer customer, int days) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));
        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        rentals.removeIf(rental -> rental.getCar().equals(car));
    }

    //method for showing rental history of user logged-in
    public void showRentalHistory(String username) {
        System.out.println("Rental History for user: " + username);
        rentals.stream()
            .filter(rental -> rental.getCustomer().getName().equals(username))
            .forEach(rental -> {
                System.out.println("Car: " + rental.getCar().getBrand() + " " + rental.getCar().getModel() + ", Days: " + rental.getDays());
            });
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Welcome to Car Rental System =====");

        while (true) {
            System.out.println("1. Login\n2. Sign Up\n3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (authenticateUser(username, password)) {
                    System.out.println("Login successful!");
                    performRentalOperations(scanner,username);
                } else {
                    System.out.println("Invalid credentials. Try again.");
                }

            } else if (choice == 2) {
                System.out.print("Choose a Username: ");
                String username = scanner.nextLine();
                System.out.print("Set a Password: ");
                String password = scanner.nextLine();

                addUser(new User(username, password));
                System.out.println("Sign-up successful. Please log in.");

            } else if (choice == 3) {
                System.out.println("Thank you for using Car Rental System!");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void performRentalOperations(Scanner scanner, String username) {
        while (true) {
            System.out.println("\n1. Rent a Car\n2. Return a Car\n3. View Rental History\n4. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.println("\nAvailable Cars:");
                for (Car car : cars) {
                    if (car.isAvailable()) {
                        System.out.println(car.getCarId() + " - " + car.getBrand() + " " + car.getModel());
                    }
                }

                System.out.print("Enter the car ID you want to rent: ");
                String carId = scanner.nextLine();
                System.out.print("Enter the number of days for rental: ");
                int rentalDays = scanner.nextInt();
                scanner.nextLine();

                Car selectedCar = cars.stream().filter(car -> car.getCarId().equals(carId) && car.isAvailable()).findFirst().orElse(null);
                if (selectedCar != null) {
                    System.out.println("\nPayment Options:\n1. UPI\n2. Bank Transfer\n3. Cash on Delivery");
                    System.out.print("Choose a payment method: ");
                    int paymentChoice = scanner.nextInt();
                    scanner.nextLine();

                    handlePayment(paymentChoice);

                    System.out.print("Confirm rental (Y/N): ");
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("Y")) {
                        rentCar(selectedCar, new Customer("CUS" + (customers.size() + 1), username), rentalDays);
                        System.out.println("Car rented successfully!");
                    } else {
                        System.out.println("Rental canceled.");
                    }
                } else {
                    System.out.println("Invalid car ID or car not available.");
                }

            } else if (choice == 2) {
                System.out.print("Enter the car ID to return: ");
                String carId = scanner.nextLine();
                Car carToReturn = cars.stream().filter(car -> car.getCarId().equals(carId) && !car.isAvailable()).findFirst().orElse(null);
                if (carToReturn != null) {
                    returnCar(carToReturn);
                    System.out.println("Car returned successfully.");
                } else {
                    System.out.println("Invalid car ID or car not rented.");
                }

            } else if (choice == 3) {
                showRentalHistory(username);

            } else if (choice == 4) {
                System.out.println("Logged out successfully.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private void handlePayment(int paymentChoice) {
        switch (paymentChoice) {
            case 1:
                System.out.println("Pay via UPI to: carrental@upi");
                break;
            case 2:
                System.out.println("Transfer to Bank Account: 123456789");
                break;
            case 3:
                System.out.println("Cash on delivery selected. No refunds for cancellations.");
                break;
            default:
                System.out.println("Invalid payment method.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();

        rentalSystem.addCar(new Car("C001", "Toyota", "Camry", 60.0));
        rentalSystem.addCar(new Car("C002", "Honda", "Accord", 70.0));
        rentalSystem.addCar(new Car("C003", "Mahindra", "Thar", 150.0));
        rentalSystem.addCar(new Car("C004", "MarutiSuzuki", "WagonR", 240.0));
        rentalSystem.addCar(new Car("C005", "Tata", "Safari", 320.0));

        rentalSystem.menu();
    }
}
