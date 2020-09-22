package com.example.trivia_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia_app.controller.AppController;
import com.example.trivia_app.data.AnswerListAsyncResponse;
import com.example.trivia_app.data.QuestionBank;
import com.example.trivia_app.model.Questions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private Button generateScore;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private CardView cardView;
    private int currentQuestionIndex ;
    private List<Questions> questionsList;

    private static final String MESSAGE_ID="MESSAGE";
    int previousScore;
    private int current_score=0;
    private TextView score;
    private TextView highScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextButton= findViewById(R.id.next_button);
        prevButton= findViewById(R.id.prev_button);
        generateScore = findViewById(R.id.generateScore);
        trueButton = findViewById(R.id.true_button);
        falseButton= findViewById(R.id.false_button);
        cardView = findViewById(R.id.cardView);
        questionCounterTextView= findViewById(R.id.question_counter);
        questionTextView= findViewById(R.id.question_textView);
        score = findViewById(R.id.score);
        highScore =findViewById(R.id.highScore);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        generateScore.setOnClickListener(this);



        // just and example --AppController.getInstance();
        questionsList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Questions> questionsArrayList) {

                questionTextView.setText(questionsArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(currentQuestionIndex+" out of 100");
                Log.d("Inside", "processFinished: "+questionsArrayList);
            }
        });

        Log.d("main", "onCreate: "+ questionsList);

        //=====================Displaying the high score the score==============================
        SharedPreferences getdata =getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        previousScore = getdata.getInt("scores",0);
        highScore.setText("Previous high score: "+previousScore);
        currentQuestionIndex = getdata.getInt("question", 0);

        }

    @Override
    protected void onStop() {
        super.onStop();
        if(current_score > previousScore)
        saveScore();


    }

    private void questionUpdater(){

        String question = questionsList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex+ " out of "+questionsList.size());
        score.setText("Score: "+current_score);




    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.prev_button:
                currentQuestionIndex = (currentQuestionIndex-1) % questionsList.size();
                if (currentQuestionIndex<0)
                    currentQuestionIndex=0;
                questionUpdater();
                break;
            case R.id.next_button:
                currentQuestionIndex = (currentQuestionIndex+1)%questionsList.size();
                questionUpdater();
                break;
            case R.id.true_button:
                trueButton.setEnabled(false);
                checkAnswer(true);
                questionUpdater();
                trueButton.setEnabled(true);
                break;
            case R.id.false_button:
                falseButton.setEnabled(false);
                checkAnswer(false);
                questionUpdater();
                falseButton.setEnabled(true);
                break;
            case R.id.generateScore:
                shareScore();
               Snackbar make = Snackbar.make(v, "My high score is : "+previousScore,Snackbar.LENGTH_LONG );
               make.show();
                break;

        }
    }

    private void checkAnswer(boolean choice) {
        boolean answer = questionsList.get(currentQuestionIndex).isAnsTrue();
        int toastMessage = 0;
        if(choice == answer){
            toastMessage = R.string.correct_answer;
            fadeAnimation();
            current_score = current_score +1 ;
            currentQuestionIndex = (currentQuestionIndex+1)%questionsList.size();
        }

        else {
            toastMessage = R.string.wrong_answer;
            shakeAnimation();
            if(current_score > 0)
                current_score = current_score-1;
            currentQuestionIndex = (currentQuestionIndex+1)%questionsList.size();

        }
        Toast.makeText(MainActivity.this,toastMessage,Toast.LENGTH_SHORT).show();

    }


    private void shakeAnimation(){

        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        cardView.setAnimation(shake);


        shake.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimation(){
        AlphaAnimation fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation.setDuration(350);
        fadeAnimation.setRepeatCount(1);
        fadeAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(fadeAnimation);

        fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void saveScore(){

        //====================Saving the score=================================
        SharedPreferences score = getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        SharedPreferences.Editor editor = score.edit();
        editor.putInt("scores",current_score);
        editor.putInt("question",currentQuestionIndex);
        editor.apply();

    }

    private void shareScore(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Play this awesome game I am playing. You can play this too download trivia on playstore." +
                " My highscore is "+previousScore+" and my current score is "+current_score);
        startActivity(intent);

    }


}
