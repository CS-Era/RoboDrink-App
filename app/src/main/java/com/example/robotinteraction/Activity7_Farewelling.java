package com.example.robotinteraction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity7_Farewelling extends AppCompatActivity {

    private TextView textViewLoggedIn, textViewDrinkName, textViewDrinkDescription;
    private Animation buttonAnimation;
    private TextView buttonRetrieve;
    private static final long TIME_THRESHOLD = 20000; // 20 secondi
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private Socket_Manager socket;  // Manager del socket per la comunicazione con il server
    private String sessionID = "-1", user = "Guest", innerResponseDescription;
    ;
    private String selectedDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_7farewelling);
        getWindow().setWindowAnimations(0);

        connection();
        initUIComponents();
        setupListeners();

        // Utente che potrà visualizzare la rating bar per le recensioni
        // Imposta il flag a 'true' quando l'utente entra in questa Activity
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("user_visited_farewelling_activity", true);
        editor.apply();

        receiveParam();
        setUpComponent();
    }
    private void connection() {
        socket = Socket_Manager.getInstance(); // Ottieni l'istanza del gestore del socket
        runnable = () -> navigateTo(Activity0_OutOfSight.class, sessionID, user);
    }
    private void initUIComponents() {
        textViewDrinkName = findViewById(R.id.textViewDrinkName);
        buttonRetrieve = findViewById(R.id.buttonRetrieve);
        textViewDrinkDescription = findViewById(R.id.textViewDrinkDescription);
        buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        textViewLoggedIn = findViewById(R.id.textViewLoggedIn);

    }
    private void setupListeners() {
        setTouchListenerForAnimation(buttonRetrieve);
    }
    private void setTouchListenerForAnimation(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    resetInactivityTimer();
                    applyButtonAnimation(v);
                }
                return false;
            }
        });
    }
    private void applyButtonAnimation(View v) {
        v.startAnimation(buttonAnimation);
        new Handler().postDelayed(v::clearAnimation, 200);
    }
    private void navigateTo(Class<?> targetActivity, String param1, String param2) {
        Intent intent = new Intent(Activity7_Farewelling.this, targetActivity);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        startActivity(intent);
    }
    private void navigateToParam(Class<?> targetActivity, String param1, String param2) {
        Intent intent = new Intent(Activity7_Farewelling.this, targetActivity);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);

        startActivity(intent);
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resetInactivityTimer();
        return super.onTouchEvent(event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimer();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    private void startInactivityTimer() {

        handler.postDelayed(runnable, TIME_THRESHOLD);
    }
    private void resetInactivityTimer() {
        handler.removeCallbacks(runnable);
        startInactivityTimer();
    }
    private void receiveParam() {
        Intent intent = getIntent();
        if (intent != null) {
            sessionID = intent.getStringExtra("param1");
            user = intent.getStringExtra("param2");
            selectedDrink = intent.getStringExtra("param3");
            innerResponseDescription = intent.getStringExtra("param4");

            int atIndex = user.indexOf("@");

            // Verificare se è presente il simbolo "@"
            if (atIndex != -1) {
                String username = user.substring(0, atIndex);
                runOnUiThread(() -> textViewLoggedIn.setText(username));
            } else {
                runOnUiThread(() -> textViewLoggedIn.setText(user));
            }
        }
    }
    private void setUpComponent() {
        runOnUiThread(() -> {
            textViewDrinkName.setText(selectedDrink);
            textViewDrinkDescription.setText(innerResponseDescription);
        });
    }
    public void onClickRitira(View v) {
        v.setClickable(false);
        resetInactivityTimer(); // Aggiungi questa linea per reimpostare il timer
        navigateToParam(Activity8_Gone.class, sessionID, user);
    }
}
