package com.example.trackabook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextInputLayout bookCodeLayout,numOfDaysLayout;
    TextInputEditText bookCode,numOfDays;
    Button check,borrow;
    TextView author,title,type,status,price,total_cost;
    StorageReference storageRef;
    ImageView bookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getReference();

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    provideSummary();
                }
            });

            borrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrowBook();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this,"An error occured" + e,Toast.LENGTH_SHORT).show();
            Log.e("TrackABook","Error: " + e);
        }
    }
    public void getReference(){
        //Firestore
        db = FirebaseFirestore.getInstance();

        //Input fields
        bookCode = findViewById(R.id.txtBookCode);
        bookCodeLayout = findViewById(R.id.bookCode);
        numOfDays = findViewById(R.id.numDays);
        numOfDaysLayout = findViewById(R.id.numOfDays);

        //buttons
        check = findViewById(R.id.checkButton);
        borrow = findViewById(R.id.borrowButton);

        //Image
        bookImage = findViewById(R.id.bookImage);

        //TextViews
        author = findViewById(R.id.author);
        title = findViewById(R.id.title);
        type = findViewById(R.id.type);
        status = findViewById(R.id.status);
        price = findViewById(R.id.price);
        total_cost = findViewById(R.id.cost);
    }
    public void borrowBook(){
        String code = bookCode.getText().toString();
        String days = numOfDays.getText().toString();

        if(code.isEmpty() && days.isEmpty()){
            bookCode.setError("Required");
            numOfDays.setError("Required");
        }else if(code.isEmpty()){bookCode.setError("Required"); numOfDays.setError(null);}
        else if(days.isEmpty()){numOfDays.setError("Required"); bookCode.setError(null);}
        else  {
            bookCode.setError(null);
            db.collection("Books")
                    .document(code)
                    .update("isBorrowed", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Book is now borrowed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to make borrow request: " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void provideSummary(){
        String code = bookCode.getText().toString();
        String days = numOfDays.getText().toString();

        if(code.isEmpty() && days.isEmpty()){
            bookCode.setError("Required");
            numOfDays.setError("Required");
        }else if(code.isEmpty()){bookCode.setError("Required"); numOfDays.setError(null);}
        else if(days.isEmpty()){numOfDays.setError("Required"); bookCode.setError(null);}
        else {
            bookCode.setError(null);
            numOfDays.setError(null);
            db.collection("Books")
                    .document(code)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String auth = documentSnapshot.getString("author");
                            String tit = documentSnapshot.getString("title");
                            String typ = documentSnapshot.getString("type");
                            Boolean isBorrowed = documentSnapshot.getBoolean("isBorrowed");
                            String image = documentSnapshot.getString("image");

                            if (auth == null && tit == null && typ == null && isBorrowed == null) {
                                Toast.makeText(MainActivity.this, "Book Code Not Found", Toast.LENGTH_SHORT).show();
                            } else {

                                borrow.setBackground(getDrawable(R.drawable.round_button));
                                borrow.setEnabled(true);

                                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // The download URL is available here
                                        String imageUrl = uri.toString();

                                        // Load and display the image using Picasso or any other image loading library
                                        Picasso.get().load(imageUrl).into(bookImage);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that occur while retrieving the download URL
                                        Log.d("FirebaseStorage", "Error getting download URL: " + e.getMessage());
                                    }
                                });

                                title.setText(tit);
                                author.setText(auth);
                                type.setText(typ);

                                if (!isBorrowed) {
                                    status.setText("Available");
                                    if (typ.equalsIgnoreCase("regular")) {
                                        price.setText("₱20 per day");
                                        RegularBook regularBook = new RegularBook(Integer.parseInt(numOfDays.getText().toString()));
                                        regularBook.computeCost();
                                        total_cost.setText("₱" + String.valueOf(regularBook.displayTotalCost()));
                                    } else if (typ.equalsIgnoreCase("premium")) {
                                        price.setText("₱50 per day");
                                        PremiumBook premiumBook = new PremiumBook(Integer.parseInt(numOfDays.getText().toString()));
                                        premiumBook.computeCost();
                                        total_cost.setText("₱" + String.valueOf(premiumBook.displayTotalCost()));
                                    } else {
                                        price.setText("N/A");
                                    }
                                } else {
                                    status.setText("Not available");
                                    price.setText("0.00");
                                    total_cost.setText("0.00");
                                    Toast.makeText(MainActivity.this, "Book is not available for borrowing", Toast.LENGTH_SHORT).show();
                                    borrow.setBackground(getDrawable(R.drawable.disabled_button));
                                    borrow.setEnabled(false);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}