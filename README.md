# Bank Account Management System (Java)

A **Java-based console application** simulating a real-world banking system. This project demonstrates **Object-Oriented Programming (OOP) principles**, secure account handling, and practical application of Java concepts.

---

## **Project Overview**

This project allows users to create **Savings** and **Current accounts**, manage their accounts securely, and perform transactions with proper validations. It also handles minor accounts with guardian details and logs all activities for transparency.

---

## **Key Features**

- 🏦 **Account Management**
  - Create Savings & Current accounts
  - Minor accounts handled with guardian information
  - Automatic unique account number generation

- 💵 **Transactions**
  - Deposit and Withdraw money
  - Optional transaction descriptions/purposes
  - Minimum balance enforcement (Savings)
  - Overdraft limit handling (Current)
  - View last 5 transactions

- 🔐 **Security**
  - Password with complexity requirements (uppercase, lowercase, digit, special character)
  - 4-digit PIN validation (cannot match DOB patterns)
  - Login authentication with 3 attempts before account lock

- 📈 **Additional Features**
  - Apply simple interest for Savings accounts
  - Track total accounts created
  - Display first name for privacy

---

## **Technical Highlights**

- ✅ **OOP Principles**
  - Abstract classes, Interfaces
  - Inheritance & Polymorphism
  - Method Overloading

- 💡 **Encapsulation**
  - Sensitive data like balance, password, and PIN are protected
  - Getter and setter validations

- 🕒 **Transaction Logging**
  - Uses Java **Date/Time API** for accurate transaction timestamps
  - Maintains a transaction history list

- ⚡ **Error Handling**
  - Handles invalid input, insufficient balance, overdraft limits
  - Ensures robust user input validation

---

## **File Structure**

BankAccountManagementSystem/
├─ BankApp.java
├─ BankAccount.java
├─ SavingsAccount.java
├─ CurrentAccount.java
├─ UserInterface.java



---

## **How to Run**

1. **Clone the repository:**
git clone https://github.com/yourusername/BankAccountManagementSystem.git

2. **Navigate to the projet folder.**
     cd BankAccountManagementSystem

3. **Compile the Java files**
     javac*.java

4.**Run the Application**
     java BankApp

5.**Follow the on-screen instructions to create accounts and perform transactions.**


## **Mentorship** 

Special thanks to Suman Kumar Das for guidance in OOP design, real-world application structure, and secure coding practices.

 ## **Future Enhancements**

Add GUI using JavaFX or Swing for better user experience

Implement file/database persistence to store accounts and transactions

Enable multi-user login sessions and report generation

## **Technologies Used**

Java (JDK 8+)

Console-based application

Java Date/Time API

Object-Oriented Programming concepts (Abstraction, Inheritance, Polymorphism, Encapsulation)

## **License**

This project is for educational purposes and inspired by OOP learning projects.



