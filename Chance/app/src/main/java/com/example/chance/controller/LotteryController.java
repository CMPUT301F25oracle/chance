//package com.example.chance.controller;
//
//import com.example.chance.model.Lottery;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
///**
// * LotteryController manages all lottery-related operations:
// * creating lotteries, adding entrants, conducting draws,
// * and persisting results in Firestore.
// */
//public class LotteryController {
//
//    private static final String COLLECTION = "lotteries";
//    private final FirebaseManager firebaseManager;
//
//    public LotteryController() {
//        firebaseManager = FirebaseManager.getInstance();
//    }
//
//    // -------------------- Create Lottery --------------------
//    public void createLottery(String eventId,
//                              OnSuccessListener<DocumentReference> onSuccess,
//                              OnFailureListener onFailure) {
//        Lottery lottery = new Lottery(eventId);
//        firebaseManager.addDocument(COLLECTION, lottery, onSuccess, onFailure);
//    }
//
//    // -------------------- Get Lottery --------------------
//    public void getLottery(String lotteryId,
//                           OnSuccessListener<Lottery> onSuccess,
//                           OnFailureListener onFailure) {
//        firebaseManager.getDocument(COLLECTION, lotteryId,
//                document -> {
//                    if (document.exists()) {
//                        Lottery lottery = document.toObject(Lottery.class);
//                        if (lottery != null) {
//                            onSuccess.onSuccess(lottery);
//                        } else {
//                            onFailure.onFailure(new Exception("Lottery parsing failed"));
//                        }
//                    } else {
//                        onFailure.onFailure(new Exception("Lottery not found"));
//                    }
//                },
//                onFailure);
//    }
//
//    // -------------------- Add Entrant --------------------
//    public void addEntrant(String lotteryId,
//                           String entrantId,
//                           OnSuccessListener<Void> onSuccess,
//                           OnFailureListener onFailure) {
//        getLottery(lotteryId, lottery -> {
//            List<String> entrants = lottery.getEntrants();
//            if (entrants == null) entrants = new ArrayList<>();
//
//            if (!entrants.contains(entrantId)) {
//                entrants.add(entrantId);
//                lottery.setEntrants(entrants);
//                firebaseManager.setDocument(COLLECTION, lotteryId, lottery, onSuccess, onFailure);
//            } else {
//                onFailure.onFailure(new Exception("Entrant already joined"));
//            }
//        }, onFailure);
//    }
//
//    // -------------------- Conduct Draw --------------------
//    public void conductDraw(String lotteryId,
//                            int numberOfWinners,
//                            OnSuccessListener<Lottery> onSuccess,
//                            OnFailureListener onFailure) {
//        getLottery(lotteryId, lottery -> {
//            if (lottery.isCompleted()) {
//                onFailure.onFailure(new Exception("Lottery already completed"));
//                return;
//            }
//
//            List<String> entrants = lottery.getEntrants();
//            if (entrants == null || entrants.isEmpty()) {
//                onFailure.onFailure(new Exception("No entrants found"));
//                return;
//            }
//
//            List<String> winners = new ArrayList<>();
//            List<String> waitingList = new ArrayList<>(entrants);
//            Random random = new Random();
//
//            int actualWinners = Math.min(numberOfWinners, entrants.size());
//            for (int i = 0; i < actualWinners; i++) {
//                int index = random.nextInt(waitingList.size());
//                winners.add(waitingList.remove(index));
//            }
//
//            lottery.setWinners(winners);
//            lottery.setWaitingList(waitingList);
//            lottery.setCompleted(true);
//
//            firebaseManager.setDocument(COLLECTION, lotteryId, lottery,
//                    unused -> onSuccess.onSuccess(lottery),
//                    onFailure);
//        }, onFailure);
//    }
//
//    // -------------------- Delete Lottery --------------------
//    public void deleteLottery(String lotteryId,
//                              OnSuccessListener<Void> onSuccess,
//                              OnFailureListener onFailure) {
//        firebaseManager.deleteDocument(COLLECTION, lotteryId, onSuccess, onFailure);
//    }
//}
