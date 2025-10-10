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
├── app/                      # Android Studio source code
│   ├── java/                 # Activities, models, adapters
│   ├── res/                  # Layouts, icons, and drawables
│   └── AndroidManifest.xml
├── docs/                     # CRC cards and analysis documents
├── wiki/                     # Mockups and storyboard (.png)
├── README.md                 # Project overview (this file)
└── .gitignore


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
Key anticipated classes derived from user stories:
- `Event` – stores event details and waiting lists  
- `Entrant` – represents user profiles and histories  
- `Organizer` – manages event creation and draws  
- `LotterySystem` – performs random selections  
- `NotificationManager` – sends app notifications  
- `FirebaseManager` – handles database operations  
- `QRCodeHandler` – generates and scans event codes  
- `Admin` – manages event and user removals  

Detailed **CRC cards** and analysis are available in `/docs` and linked in the Wiki.

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
