# CRC Cards

## Table of Contents

- [MainActivity](#mainactivity)
- [Home (Fragment)](#home-fragment)
- [ViewEvent (Fragment)](#viewevent-fragment)
- [CreateEvent (Fragment)](#createevent-fragment)
- [Profile (Fragment)](#profile-fragment)
- [QrcodeScanner (Fragment)](#qrcodescanner-fragment)
- [WaitingListMapActivity](#waitinglistmapactivity)
- [Admin Views](#admin-views)
- [ChanceViewModel](#chanceviewmodel)
- [DataStoreManager](#datastoremanager)
- [EventController](#eventcontroller)
- [ProfileController](#profilecontroller)
- [LotteryController](#lotterycontroller)
- [SignUpController](#signupcontroller)
- [QRCodeHandler](#qrcodehandler)
- [Event](#event)
- [User](#user)
- [Administrator](#administrator)
- [Lottery](#lottery)
- [Notification](#notification)
- [WaitingList](#waitinglist)

---

## MainActivity

| Responsibilities | Collaborators |
| :--- | :--- |
| **Container & Navigation:** orchestrates the swapping of fragments (Home, Profile, etc.) using the backstack. | `ChanceFragment` |
| **Global State:** Observes the `ChanceViewModel` for navigation triggers and popup requests. | `ChanceViewModel` |
| **Initialization:** Sets up the `DataStoreManager` and initializes database polling. | `DataStoreManager` |
| **Animation:** Handles fragment transition animations (circular reveal, fade). | |

---

## Home (Fragment)

| Responsibilities | Collaborators |
| :--- | :--- |
| **Event Browsing:** Displays a list of available events to the user. | `ChanceViewModel` |
| **Filtering:** Allows the user to filter events (search bar logic). | `MultiPurposeEventSearchScreen` |
| **Navigation:** Intercepts clicks on event items to navigate to `ViewEvent`. | `ViewEvent` |

---

## ViewEvent (Fragment)

| Responsibilities | Collaborators |
| :--- | :--- |
| **Event Details:** Displays poster, description, date, and time. | `Event` |
| **User Action:** Handles "Join Waiting List" (with geolocation) and "Leave Waiting List". | `DataStoreManager` |
| **Invitation Handling:** Allows the user to Accept or Decline lottery wins. | `EventController` |
| **Organizer Features:** If the user is the organizer, shows buttons to view entrants or the Map. | `WaitingListMapActivity` |
| **QR Code:** Displays the unique QR code for the event details. | `QRCodeHandler` |

---

## CreateEvent (Fragment)

| Responsibilities | Collaborators |
| :--- | :--- |
| **Input Collection:** Gathers event title, price, capacity, and dates from the user. | `EventController` |
| **Image Handling:** Allows the user to pick an image from the gallery for the poster. | `EventImage` |
| **Permissions:** Requests location permissions if the event requires geolocation. | `DataStoreManager` |
| **Submission:** Validates input and submits the new event to the controller. | `ChanceViewModel` |

---

## Profile (Fragment)

| Responsibilities | Collaborators |
| :--- | :--- |
| **View Profile:** Displays the current user's details (Avatar, Name, Email). | `User` |
| **Edit Profile:** Allows the user to update their contact info and notification preferences. | `ProfileController` |
| **Logout:** triggers the logout sequence. | `Authentication` |

---

## QrcodeScanner (Fragment)

| Responsibilities | Collaborators |
| :--- | :--- |
| **Camera Interface:** Manages camera permissions and preview. | `QrCodeAnalyzer` |
| **Analysis:** Decodes QR data from the image stream. | `QRCodeHandler` |
| **Navigation:** Redirects the user to the specific `ViewEvent` or executes a check-in based on the decoded string. | `ChanceViewModel` |

---

## WaitingListMapActivity

| Responsibilities | Collaborators |
| :--- | :--- |
| **Visualization:** Displays a Google Map view. | `GoogleMap` |
| **Markers:** Fetches entrant geolocation data for a specific event and plots markers. | `FirebaseManager` |
| **Data Context:** Uses the `EventID` to query the specific waiting list locations. | `Event` |

---

## Admin Views

*(Includes Admin, AdminViewUsers, AdminViewPhotos, AdminViewUserProfile)*

| Responsibilities | Collaborators |
| :--- | :--- |
| **Dashboard:** Provides access to manage Users, Events, and Images. | `ProfileController` |
| **Browsing:** Lists all entities (users/photos) for review. | `AdminPhotosAdapter` |
| **Moderation:** Allows the deletion of profiles, events, or images that violate rules. | `EventController` |

---

## ChanceViewModel

| Responsibilities | Collaborators |
| :--- | :--- |
| **State Holder:** Maintains the `CurrentUser`, `EventList`, and `NotificationList` using LiveData. | `User` |
| **Navigation Hub:** Emits events to tell `MainActivity` which fragment or popup to load next. | `ChanceFragment` |
| **Communication:** Acts as the bridge between the UI (Fragments) and the Data Layer. | `Event` |

---

## DataStoreManager

| Responsibilities | Collaborators |
| :--- | :--- |
| **Facade:** Serves as the primary entry point for database operations for the UI. | `FirebaseManager` |
| **Waiting List Logic:** Handles `joinWaitingList`, `leaveWaitingList`, `acceptInvitation`, `rejectInvitation`. | `Event` |
| **User Management:** Wraps authentication and user creation logic. | `User` |
| **Observables:** Provides RxJava observables for real-time updates on Events and Notifications. | `Notification` |

---

## EventController

| Responsibilities | Collaborators |
| :--- | :--- |
| **CRUD:** specific logic to `createEvent`, `updateEvent`, and `deleteEvent`. | `FirebaseManager` |
| **Banner Management:** Handles uploading and removing event banner images. | `EventImage` |
| **Fetching:** Retrieval of single events or all events for the list. | `DataStoreManager` |

---

## ProfileController

| Responsibilities | Collaborators |
| :--- | :--- |
| **Entrant Management:** Updates, retrieves, and deletes Entrant/User profiles. | `User` |
| **Organizer/Admin:** Manages specific profiles for Organizers and Administrators. | `Organizer` |
| **Database:** Interface with the specific Firestore collections for profiles. | `Administrator` |

---

## LotteryController

| Responsibilities | Collaborators |
| :--- | :--- |
| **Lifecycle:** Creates and deletes Lottery instances. | `Lottery` |
| **Execution:** Conducts the random draw (`conductDraw`) to select winners. | `FirebaseManager` |
| **Updates:** Moves users from Entrants list to Winners list in the database. | |

---

## SignUpController

| Responsibilities | Collaborators |
| :--- | :--- |
| **Registration:** Handles the creation of `SignUp` objects (initial user records). | `SignUp` |
| **Verification:** Verifies users and manages device IDs. | `FirebaseManager` |
| **CRUD:** Basic Get/Update/Delete for the SignUp collection. | |

---

## QRCodeHandler

| Responsibilities | Collaborators |
| :--- | :--- |
| **Generation:** Converts a string (Event ID) into a Bitmap QR code. | `Bitmap` |
| **Decoding:** Converts a Bitmap or byte array back into a string string. | `QrcodeScanner` |
| **Utility:** Helper methods for Base64 conversion. | |

---

## Event

| Responsibilities | Collaborators |
| :--- | :--- |
| **Data Holder:** Stores Title, Description, Date, Price, Capacity. | `Date` |
| **List Management:** Maintains lists of IDs for `WaitingList`, `InvitationList`, `Accepted`, `Declined`. | `User` |
| **Location:** Stores the Map of `GeoPoints` for entrants. | `GeoPoint` |
| **Logic:** Checks if full, adds/removes entrants locally before save. | |

---

## User

| Responsibilities | Collaborators |
| :--- | :--- |
| **Identity:** Stores Username, Email, Phone, DeviceID. | `ProfileController` |
| **History:** Tracks lists of `joinedEvents`, `selectedEvents` (won), and `eventHistory`. | `Event` |
| **Preferences:** Stores notification settings and profile image URL. | `Notification` |

---

## Administrator

| Responsibilities | Collaborators |
| :--- | :--- |
| **Privileges:** Identification of the admin role. | `User` |
| **Tracking:** Keeps a list of `managedEvents` and `flaggedUsers`. | `Event` |

---

## Lottery

| Responsibilities | Collaborators |
| :--- | :--- |
| **State:** Tracks the `eventId`, `entrants` list, and `winners` list. | `Event` |
| **Status:** Knows if the draw is `completed`. | `LotteryController` |

---

## Notification

| Responsibilities | Collaborators |
| :--- | :--- |
| **Data:** Stores message content, creation date, and type. | `User` |
| **Meta:** Stores metadata (like related Event ID) for deep linking. | `ChanceViewModel` |

---

## WaitingList

| Responsibilities | Collaborators |
| :--- | :--- |
| **Association:** Links an `eventId` to a list of `entrantIds`. | `Event` |
| **Management:** Methods to `addEntrant`, `removeEntrant`, and get the count. | `User` |
