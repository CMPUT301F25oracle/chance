# Ingredient

## IngredientActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Displays the values stored in Ingredients gotten from IngredientList (from IngredientStorageController) | IngredientStorageController |
| Display a button that lets the user edit this ingredient in IngredientEditActivity | IngredientEditActivity |
| Display a button that lets the user edit this ingredient in IngredientEditActivity | Ingredient |

---

## EventListActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Display list of joinable events Search/filter events and open details Refresh list after create/edit | EventController EventDetailActivity EventCreateActivity |

---

## EventDetailActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Show event details and registration status Join or leave the waiting list Handle deep link from QR scan | EventController WaitingList QRScanActivity |

---

## EventCreateActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Create/edit event fields Set registration window, capacity, price Attach or update poster Toggle join-location requirement (optional) | EventController Organizer EventController EventController |

---

## LotteryController

| Responsibilities | Collaborators |
| ----- | ----- |
| Run draws and replacement draws Persist outcomes and expose chosen/final lists Notify selected/unenrolled cohorts as needed | Lottery EventController Invitation |

---

## EventController

| Responsibilities | Collaborators |
| ----- | ----- |
| Validate create/update inputs Enforce registration window and capacity rules Orchestrate waiting-list joins/leaves Provide lists/data for UI screens | EventCreateActivity EventDetailActivity WaitingList EventListActivity |

---

## QRScanActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Scan QR and decode eventId Navigate to EventDetailActivity Support join from scanned event | EventController EventDetailActivity WaitingList |

---

## SignUpActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| Let selected entrant accept or decline invitation Confirm sign-up and show result Handle decline and return to event details | SignUpController Invitation EventDetailActivity |

---

## ProfileActivity

| Responsibilities | Collaborators |
| ----- | ----- |
| View/edit entrant profile (name, email, phone) Manage notification preferences Show history of joined/selected events | ProfileController ProfileController EventController |

---

## ProfileController

| Responsibilities | Collaborators |
| ----- | ----- |
| Save/update entrant profile Manage notification preferences Provide profile data to other screens | ProfileActivity ProfileActivity EventDetailActivity |

---

## SignUpController

| Responsibilities | Collaborators |
| ----- | ----- |
| Validate capacity and registration window Convert accepted invitation into attendee Trigger replacement draw on decline if needed Update waiting list and event attendee lists | EventController Invitation LotteryController WaitingList |

---

## Event

| Responsibilities | Collaborators |
| ----- | ----- |
| Hold event data: name, description, time/place Track registrationStart/registrationEnd Maintain capacity, price, posterId Keep entrants (waiting) and attendees (accepted) Provide counts for entrants/attendees | Organizer EventController WaitingList EventDetailActivity Lottery |

---

## Entrant

| Responsibilities | Collaborators |
| ----- | ----- |
| Keeps name, email, phone number, history of events joined Join and leave event waiting lists Manage notification preferences Edit or delete profile information | WaitingList ProfileController EventController ProfileActivity |

---

## Lottery

| Responsibilities | Collaborators |
| ----- | ----- |
| Selects a random User/entrant from the Waiting list | WaitingList |
| Gives that User to the EventHandler | Event |

---

## Organizer

| Responsibilities | Collaborators |
| ----- | ----- |
| Create/update events with registration windows Publish event posters Set capacity limit and price View waiting list and trigger draws Send notifications to selected entrants | Event EventController LotteryController EventCreateActivity |

---

## WaitingList

| Responsibilities | Collaborators |
| ----- | ----- |
| Store ArrayList<Entrant> of all users who joined this event Each entry includes: Entrant object reference, joinTimestamp, optional joinLocation Add entrant to list when they click "Join" button Remove entrant from list when they leave or get selected Prevent duplicate entries (check if entrant already in list) Provide count() method for UI display Provide getEntrants() method for lottery draw | Entrant Event SignUpActivity EventDetailActivity EventController Lottery |

---

## SignUp

| Responsibilities | Collaborators |
| ----- | ----- |
| Allows the user to sign up for the lottery | User/Entrant |
| Sends that user's information to the WaitingList | WaitingList |

---

## Invitation

| Responsibilities | Collaborators |
| ----- | ----- |
| Send invitations out to the registered entrants | Entrant LotteryController SignUpController SignUpActivity |
| status pending/accepted/declined/cancelled |  |
| allow entrant to accept (sign up) or decline and notify the system to trigger replacement draw if needed. |  |
| expire invitations if not acted upon |  |

---

## QR Code

| Responsibilities | Collaborators |
| ----- | ----- |
| Add event info to QR code | Event |
| allow join to the waiting list |  |

---

## Administrator

| Responsibilities | Collaborators |
| ----- | ----- |
| Browse events, profiles, and uploaded images | Event Entrant |
| Remove events/profiles/images when necessary |  |
| Review notification/log records |  |
