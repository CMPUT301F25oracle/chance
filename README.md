# 🎟️ Event Lottery System Application  
**CMPUT 301 – Software Engineering**  
University of Alberta | Fall 2025  

---

## 📘 Overview
The **Event Lottery System** is an Android application designed to fairly allocate limited spots for community events using a lottery-based system.  
Instead of a “first-come, first-served” race, entrants can join a waiting list during an open registration period. Once registration closes, a random draw determines who gets to participate. This ensures fairness and accessibility for everyone, including users with limited availability.

---

## 🎯 Objectives
- Allow entrants to browse, filter, and join waiting lists for events.  
- Enable organizers to create, manage, and run lotteries for their events.  
- Provide administrators with moderation tools to manage events and users.  
- Integrate Firebase for storing event and participant data.  
- Support QR code scanning for quick event access.  

---

## 👥 Actors
| Role | Description |
|------|--------------|
| **Entrant** | A user who joins or leaves event waiting lists. |
| **Organizer** | A user who creates events, manages registrations, and performs lottery draws. |
| **Administrator** | Maintains infrastructure, removes inappropriate content, and oversees system integrity. |

---

## 🧩 Core Features
- **Lottery-based sign-up** for fair participation.  
- **Event browsing and filtering** by category or availability.  
- **QR code scanning** to quickly access event details.  
- **Organizer tools** for creating events and managing waiting lists.  
- **Notifications** for selected or declined entrants.  
- **Firebase integration** for real-time data and synchronization.  
- **Optional geolocation verification** for location-based participation.  

---

## 🗂️ Project Structure
```bash
├── app/                      # Android Studio source code
│   ├── java/                 # Activities, models, adapters
│   ├── res/                  # Layouts, icons, and drawables
│   └── AndroidManifest.xml
├── doc/                     # CRC cards and analysis documents
├── wiki/                     # Mockups and storyboard (.png)
├── README.md                 # Project overview (this file)
└── .gitignore
```

---

## 📋 Product Backlog
All **User Stories** are tracked as GitHub Issues using the format:
US XX.XX.XX – <short summary>


Each issue contains:
- User Story description  
- Summary “in our own words”  
- Acceptance criteria  
- Story point estimate  
- Risk level (Low / Medium / High)  
- Release tag (Halfway / Final)

---

## 🎨 UI Mockups & Storyboard
Visual designs for all major dialogs are included in the **Wiki** as `.png` images.  
Each screen is labeled with its corresponding **User Story ID** and linked through storyboard sequences showing navigation and transitions.

---

## 🧱 Object-Oriented Analysis

Key anticipated classes and controllers derived from user stories and CRC card design:

- **`Event`** – Holds event information (name, description, date/time, capacity, price, and poster). Maintains registration windows and manages waiting lists and attendees. Provides counts for entrants and attendees.  
  *Collaborators:* `Organizer`, `EventController`, `WaitingList`, `EventDetailActivity`, `Lottery`

- **`Entrant`** – Represents the participant profile with personal details and event history. Can join or leave waiting lists, manage notifications, and edit or delete their profile.  
  *Collaborators:* `WaitingList`, `ProfileController`, `EventController`, `ProfileActivity`

- **`Organizer`** – Manages event creation and updates, defines registration windows, sets capacities and prices, and publishes posters. Can view waiting lists, trigger lottery draws, and send notifications.  
  *Collaborators:* `Event`, `EventController`, `LotteryController`, `EventCreateActivity`

- **`Administrator`** – Oversees the system’s infrastructure. Can browse and remove events, profiles, and uploaded images, and review notification or log records.  
  *Collaborators:* `Event`, `Entrant`

- **`EventController`** – Handles event creation, updates, and validation. Enforces capacity and registration window constraints. Orchestrates waiting list joins and leaves and supplies data to UI components.  
  *Collaborators:* `EventCreateActivity`, `EventDetailActivity`, `WaitingList`, `EventListActivity`

- **`LotteryController`** – Runs event draws and replacement draws, updates chosen lists, and triggers notifications for selected or declined entrants.  
  *Collaborators:* `Lottery`, `EventController`, `Invitation`

- **`Lottery`** – Selects entrants randomly from a waiting list and returns chosen participants to the event handler.  
  *Collaborators:* `WaitingList`, `Event`

- **`WaitingList`** – Stores all entrants for each event, preventing duplicates. Supports adding/removing entrants, returning counts, and providing entrants for lottery draws.  
  *Collaborators:* `Entrant`, `Event`, `SignUpActivity`, `EventDetailActivity`, `EventController`, `Lottery`

- **`Invitation`** – Manages invitations to selected entrants, including pending/accepted/declined statuses. Triggers replacement draws on decline and can expire unresponded invitations.  
  *Collaborators:* `Entrant`, `LotteryController`, `SignUpController`, `SignUpActivity`

- **`SignUpController`** – Validates capacity and registration conditions when an entrant accepts an invitation. Updates waiting lists and triggers replacement draws if needed.  
  *Collaborators:* `EventController`, `Invitation`, `LotteryController`, `WaitingList`

- **`ProfileController`** – Handles profile data storage, updates, and notification preferences. Provides entrant profile information to other screens.  
  *Collaborators:* `ProfileActivity`, `EventDetailActivity`

- **`EventListActivity`** – Displays the list of joinable events. Handles searching, filtering, and navigation to event details.  
  *Collaborators:* `EventController`, `EventDetailActivity`, `EventCreateActivity`

- **`EventDetailActivity`** – Shows full event details and current registration status. Allows entrants to join or leave waiting lists and handles navigation from QR scans.  
  *Collaborators:* `EventController`, `WaitingList`, `QRScanActivity`

- **`EventCreateActivity`** – UI for creating or editing event details, posters, and registration settings.  
  *Collaborators:* `EventController`, `Organizer`

- **`ProfileActivity`** – UI for entrants to view or edit their profile, manage notifications, and review their joined or selected events.  
  *Collaborators:* `ProfileController`, `EventController`

- **`SignUpActivity`** – UI for entrants to accept or decline invitations and confirm participation.  
  *Collaborators:* `SignUpController`, `Invitation`, `EventDetailActivity`

- **`QRScanActivity`** – Handles QR code scanning and decoding for joining events via direct links.  
  *Collaborators:* `EventController`, `EventDetailActivity`, `WaitingList`

- **`QR Code` / `QRCodeHandler`** – Generates and encodes event info into QR codes and supports joining via scan.  
  *Collaborators:* `Event`

📂 Detailed **CRC cards** and analysis are available in `/doc` and linked in the Wiki.

---

## ⚙️ Tools & Technologies
| Tool | Purpose |
|------|----------|
| **Android Studio** | Primary development environment |
| **Firebase Firestore** | Cloud database for events and users |
| **ZXing / MLKit** | QR code generation and scanning |
| **Draw.io / Figma** | UI mockups and storyboards |
| **GitHub Issues & Wiki** | Documentation and backlog management |

---

## 🚀 Development Practices
- Each feature corresponds to a **branch** (e.g., `feature/US_01_01_01_join_waitlist`).  
- Only commit under your own GitHub account — no proxy commits.  
- Use descriptive commit messages:  
git commit -m "Implemented join waiting list (US 01.01.01)"

- Track feature completion using issues and milestones.

---

## 🧪 Release Plan
| Release | Coverage |
|----------|-----------|
| **Halfway Checkpoint** | Core entrant features (joining, viewing, and notifications) |
| **Final Release** | Organizer tools, admin functionality, and full system integration |

---

## 📧 Contributors
- **lamersc**
- **abstractcodes**
- **Manan-Ag**
- **qzimeng-art**
- **Chaidst**
- **PhullFury**


---

© 2025 University of Alberta — CMPUT 301 Project  
For educational use only.
