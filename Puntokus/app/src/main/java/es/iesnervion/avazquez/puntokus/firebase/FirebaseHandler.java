package es.iesnervion.avazquez.puntokus.firebase;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler {

    private static boolean isPersistenceEnabled = false;


    public FirebaseHandler() {
        if (!isPersistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
