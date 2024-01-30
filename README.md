# Birthday Wishing App

## Purpose of the Application:
In today's fast-paced world, where personal connections are often overshadowed by the bustle of daily life, it's easy to overlook special occasions like birthdays. This is where the "Birthday Wishes App" comes into play – a mobile application designed to bridge this gap in personal connections. The core functionality of this app is to automatically send birthday wishes to people whose contact details are saved within the app. This innovative tool not only reminds users of upcoming birthdays but also ensures that heartfelt wishes are conveyed on time, fostering stronger bonds and nurturing relationships.

## Target Audience:
The Birthday Wishes App is ideal for anyone who values personal relationships, from busy professionals who struggle to keep track of important dates to individuals who love to spread joy by remembering and celebrating special moments in the lives of their friends and family.

## Unique Features:
- **Automated Birthday Wishes:** Set personalized messages for each contact, which the app will automatically send on their birthdays.
- **User Authentication:** Users can register and login into the app and save different Birthdays.
- **Customizable Messages:** A variety of templates and custom message options to add a personal touch to birthday greetings.
- **Notification Reminders:** Option to receive notifications for upcoming birthdays, allowing for personal phone calls or additional messaging.

## Concepts Required for Development:
1.	**Android Development Basics:** Understanding Activities, Intents, and Layouts.
2.	**Database Integration:** Utilizing Firebase or any other database to store user data, contact information, and personalized messages.
3.	**UI/UX Design:** Creating a user-friendly interface that is both appealing and intuitive.
4.	**Notifications and Background Services:** Implementing notifications and services that run in the background for sending wishes.
5.	**Testing and Debugging:** To ensure smooth functionality across different Android versions and devices.


## Basic idea For Data Model:
- **Collection:** Users
  
  Each document in this collection represents a user of the app.
  - **UserID:** Unique identifier for the user.
  - **Email:** User's email address.
  - **PasswordHash:** Hashed password for secure authentication.
  - **BirthdayContacts:** An array of references to Birthday Contact documents.

  **Sample Data:**
    ```JSON
    {
      "UserID": "user123",
      "Email": "john.doe@example.com",
      "PasswordHash": "hashedpassword123",
       "BirthdayContacts": ["contact123", "contact456"]
    }
    ```

- **Collection:** BirthdayContacts
  
  Each document in this collection represents a birthday contact added by a user.
  - **ContactID:** Unique identifier for the contact.
  - **UserID:** Reference to the user who added this contact.
  - **FirstName:** First name of the contact.
  - **LastName:** Last name of the contact.
  - **RelationType:** The type of relationship with the user (e.g., friend, family, colleague).
  - **BirthdayDate:** The contact's birthday date.
  - **BirthdayWish:** An optional pre-set birthday wishes for the contact. Else random wish will be sent.
  - **Contact Number:** Contacts's phone number for sending wishes.
  
  **Sample Data**:
  ```JSON
  {
  "ContactID": "contact123",
  "UserID": "user123",
  "FirstName": "Jane",
  "LastName": "Doe",
  "RelationType": "Friend",
  "BirthdayDate": "1990-05-12",
  "BirthdayWish": "Happy Birthday, Jane! Hope you have a fantastic day!",
  "ContactNumber": 1234567890,
  }
  ```


