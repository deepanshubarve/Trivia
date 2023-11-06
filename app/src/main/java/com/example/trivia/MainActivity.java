package com.example.trivia;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    List<Question> questionList;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;


    @SuppressLint({"DefaultLocale", "RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        score = new com.example.trivia.model.Score();
        prefs = new Prefs(MainActivity.this);

        //retrieve the last state
        currentQuestionIndex = prefs.getState();

        binding.highestScoreText.setText("Highest:" + String.valueOf(prefs.getHighestScore()));
        binding.scoreText.setText(MessageFormat.format("Current Score:{0}",
                String.valueOf(score.getScore())));

      questionList = new Repository().getQuestion(questionsArrayList -> {
                  binding.questionTextview.setText(questionsArrayList.get(currentQuestionIndex)
                          .getAnswer());

          updateCounter(questionsArrayList);
      });



        binding.buttonPrevious.setOnClickListener(view -> {
            currentQuestionIndex = (currentQuestionIndex -1) % questionList.size();
            updateQuestion();
        });

        binding.buttonNext.setOnClickListener(view -> {
            getNextQuestsion();


        });

        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();
        });

        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();

        });
    }

    private void getNextQuestsion() {
        currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
        updateQuestion();
    }

    @SuppressLint("ResourceType")
    private void checkAnswer(boolean userChooseCorrect) {
        boolean answer =questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userChooseCorrect == answer) {
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        }else{
            snackMessageId = R.string.incorrecct;
            shakeAnimation();
            deductPoints();
        }
        Snackbar.make(binding.cardView,snackMessageId,Snackbar.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale")
    private void updateCounter(ArrayList<Question> questionsArrayList) {
        binding.textViewOutOf.setText(String.format("Question:%d/%d",
                currentQuestionIndex, questionsArrayList.size()));
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextview.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }
    public void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestsion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestsion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }});



        }private void deductPoints(){


        if(scoreCounter > 0){
            scoreCounter -= 100;
            score.setScore((scoreCounter));
            binding.scoreText.setText(MessageFormat.format("Current Score:{0}",
                    String.valueOf(score.getScore())));

        }else{
            scoreCounter= 0;
            score.setScore(scoreCounter);

        }
    }
         private  void  addPoints(){
             scoreCounter = scoreCounter + 100;
             score.setScore((scoreCounter));
             binding.scoreText.setText(String.valueOf(score.getScore()));
             binding.scoreText.setText(MessageFormat.format("Current Score:{0}",
                     String.valueOf(score.getScore())));
}

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        Log.d("Pause", "onPause: saving score" + prefs.getHighestScore());
        super.onPause();
    }
}



