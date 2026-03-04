## 📱 Android Application – Anti Food Waste

This project consists of developing an **Android mobile application** that allows clients to reserve unsold food baskets from local commerces while enabling merchants to manage their offers.  
The main objective of this application is to **reduce food waste** by connecting clients with commerces that have surplus food available.

---

## 👥 Application Actors

The application includes the following actors:

### Client
- Browse the list of commerces
- View available food baskets
- Reserve a food basket
- View reservation history
- Cancel reservations

### Merchant
- Add new food baskets
- Modify existing baskets
- Delete baskets
- View received reservations

### Admin (Optional)
- Perform minimal management tasks if needed.

---

## 🔐 Authentication

The application provides **local authentication** with the following features:

- User registration
- User login
- Role management (**Client / Merchant**)
- User session management using **SharedPreferences**

---

## 🧑‍💻 Client Features

Clients can:

- View the list of available commerces
- View available baskets for each commerce
- Reserve a basket
- Access their reservation history
- Cancel an existing reservation

---

## 🏪 Merchant Features

Merchants can:

- Add a new basket (**title, price, quantity**)
- Modify an existing basket
- View received reservations
- Delete a basket

---

## 🗄️ Local Database

The application uses a **local database** with the following tables:

- `User`
- `Commerce`
- `Panier`
- `Reservation`

### Database Relationships

- **User (1) → (N) Reservation**
- **Commerce (1) → (N) Panier**
- **Panier (1) → (N) Reservation**

---
