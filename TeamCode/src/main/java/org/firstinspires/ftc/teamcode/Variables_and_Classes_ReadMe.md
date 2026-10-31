//////////////////////    TYPES OF VARIABLES      ///////////////////////////////////

- Arrays & Objects: Any custom class you create or use (like ArrayList, Scanner, or a custom User object).
  Use case: Modeling real-world structures, collections, and complex systemic states.
- boolean: Stores strictly two values: true or false
- char: Stores a single character or Unicode value wrapped in single quotes, like 'A' or '$'
- double: Stores fractional/decimal numbers with 15–16 digits of precision
- int: Stores whole numbers from roughly -2.14 billion to 2.14 billion
- float: Stores fractional/decimal numbers with 6–7 digits of precision
- string: : A built-in reference type that acts like a primitive due to special language support. Enclosed in double quotes

**- final - makes the variable unchangeable


//////////////////////     CLASSES    ///////////////////////////////////
- public - Accessible from anywhere in your entire program. Any other class can read or modify it.

- private - Accessible only within the exact same class where it is declared. Hidden from all other classes.


//////////////////////     METHODS      ///////////////////////////////////

    void: A void method is an action command
    A non-void method is a request for data to be returned ** MUST HAVE A RETURN COMMAND
                        Example:
                        
                        // Option A: Using void (Action Only)
                        public void deposit(double amount) {
                        this.balance += amount;
                        // It updates the balance in secret. No data is handed back.
                        }

                        // Option B: Changing void to a data type (Action + Receipt)
                        public double depositAndGetNewBalance(double amount) {
                            this.balance += amount;
                            return this.balance; // Crucial! You MUST use 'return' to send a double back.
                        }

//////////////////////     Local vs Static Variables     ///////////////////////////////////

Local Variables	-
    Declared: Inside a method, constructor, or code block {}.
    Memory Lifetime: Created when the block executes; destroyed immediately when the block exits.
    Purpose: Temporary data processing. Used for loop counters, intermediate math operations, or transient holding states. Must be initialized before use.
Instance Variables (Non-Static Fields)
    Declared: Inside a class, but outside any method or code block.	Bound to a specific object.
    Memory Lifetime: Created with new and destroyed when the object is garbage-collected.
    Purpose: Defining object attributes. Holds unique data specific to an individual instance (e.g., accountBalance for a BankAccount object).
Static Variables (Class Variables)
    Declared: Inside a class, outside methods, using the static keyword.
    Memory Lifetime: Bound to the class itself. Created when the class loads into memory and lasts until the program terminates.
    Purpose: Shared state or global constants. Used when all instances of a class need to share the exact same configuration data or trackers (e.g., totalUsersCount).
