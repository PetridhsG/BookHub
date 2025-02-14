# BookHub

## Overview
This project is a distributed system for managing room rentals, similar to platforms like Airbnb and Booking.com. It consists of a Java-based backend using TCP communication and an Android frontend for user interaction. The system allows users to list, search, and book accommodations while ensuring scalability and fault tolerance through distributed computing techniques.

## Features

### Manager Functionality (Console Application)
- Add accommodations with details and images.
- Define available booking dates.
- View bookings for owned accommodations.
- Communicate with the Master node using a TCP-based console application.

### Tenant Functionality (Android Application)
- Search for accommodations using filters:
  - Location
  - Available dates
  - Number of guests
  - Price
  - Star rating
- Make a reservation for a selected accommodation.
- Rate accommodations (1-5 stars).
- All interactions occur asynchronously via TCP sockets.

## System Architecture
### Master Node (Java TCP Server)
- Handles requests from tenants and managers.
- Routes data to appropriate worker nodes.
- Implements MapReduce for distributed data processing.
- Ensures thread-safe request handling using synchronized, wait, and notify mechanisms.

### Worker Nodes (Java TCP Servers)
- Store and manage accommodation data in-memory.
- Handle room availability and reservations.
- Support dynamic allocation at system initialization.
- Communicate with the Master node using TCP sockets.

### Frontend (Android Application)
- Provides a user-friendly interface for tenants.
- Uses TCP sockets for communication with the backend.
- Implements asynchronous requests to maintain responsiveness.

## Data Model (JSON Example)
```json
{
  "roomName": "roomName",
  "noOfPersons": 5,
  "area": "Area1",
  "stars": 3,
  "noOfReviews": 15,
  "roomImage": "/usr/bin/images/roomName.png"
}
```

## Implementation Details
- **Backend:** Java with TCP-based communication.
- **Frontend:** Android (Java/Kotlin) with TCP sockets.
- **Concurrency:** Multi-threading using synchronized, wait, and notify.
- **Data Storage:** In-memory structures (no database, except for storing images).
- **Distributed Processing:** MapReduce for efficient query handling.

## Fault Tolerance
- Implements active replication for worker nodes.
- Ensures data consistency across multiple nodes.
- Reroutes requests to replicas in case of worker failure.

## Deployment
1. **Start the Master Node:** Runs as a TCP server.
2. **Start Worker Nodes:** Connect dynamically to the Master.
3. **Run the Console App (Manager):** Adds accommodations.
4. **Run the Android App (Tenant):** Searches and books rooms.
