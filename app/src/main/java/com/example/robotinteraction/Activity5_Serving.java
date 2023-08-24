package com.example.robotinteraction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class Activity5_Serving extends AppCompatActivity {
    private Animation buttonAnimation;
    private TextView textViewOrderStatusTitle, textViewOrderStatusMessage, textViewLoggedIn, buttonQuiz, buttonWaitingRoom;
    private String selectedDrink;
    private static final long TIME_THRESHOLD = 20000; // 20 secondi
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private Activity_SocketManager socket;  // Manager del socket per la comunicazione con il server
    private String sessionID = "-1", user = "Guest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5serving);

        connection();
        initUIComponents();
        setupListeners();

        receiveParam();
        setUpComponent();
    }
    private void connection() {
        socket = Activity_SocketManager.getInstance(); // Ottieni l'istanza del gestore del socket
        runnable = () -> navigateTo(Activity0_OutOfSight.class, sessionID, user);
    }
    private void initUIComponents() {
        textViewLoggedIn = findViewById(R.id.textViewLoggedIn);
        buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        buttonQuiz = findViewById(R.id.buttonQuiz);
        buttonWaitingRoom = findViewById(R.id.buttonWaitingRoom);
        textViewOrderStatusTitle = findViewById(R.id.textViewOrderStatusTitle);
        textViewOrderStatusMessage = findViewById(R.id.textViewOrderStatusMessage);
    }
    private void setupListeners() {
        setTouchListenerForAnimation(buttonQuiz);
        setTouchListenerForAnimation(buttonWaitingRoom);
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
        Intent intent = new Intent(Activity5_Serving.this, targetActivity);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        startActivity(intent);
    }
    private void navigateToParam(Class<?> targetActivity, String param1, String param2, String param3) {
        Intent intent = new Intent(Activity5_Serving.this, targetActivity);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        intent.putExtra("param3", param3);
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
    public void onClickWait(View v) {
        resetInactivityTimer(); // Aggiungi questa linea per reimpostare il timer
        navigateToParam(Activity6_Attesa.class, sessionID, user, selectedDrink);
    }
    public void onClickQuiz(View v) {
        resetInactivityTimer(); // Aggiungi questa linea per reimpostare il timer
        navigateToParam(Activity6_Chat.class, sessionID, user, selectedDrink);
    }
    private void receiveParam() {
        Intent intent = getIntent();
        if (intent != null) {
            sessionID = intent.getStringExtra("param1");
            user = intent.getStringExtra("param2");
            selectedDrink = intent.getStringExtra("param3");

            int atIndex = user.indexOf("@");

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

            textViewOrderStatusTitle.setText("In preparazione: \n" + selectedDrink);

            if (selectedDrink != "") {
                textViewOrderStatusMessage.setText("Il tuo " + selectedDrink + " è attualmente in preparazione. Puoi attendere in sala d'attesa o intrattenerti rispondendo ai quiz.");
            }
        });
    }

}