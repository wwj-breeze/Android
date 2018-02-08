package com.wangwenjun.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private TextView mQuestionTextView;
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private final static String TAG = "QuizActivity";
    private static final String KEY_QUESTION_INDEX ="question_index";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_china, true),
            new Question(R.string.question_japan, false),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Button trueButton = (Button) findViewById(R.id.true_button);
        Button falseButton = (Button) findViewById(R.id.false_button);
        Button cheatButton = (Button) findViewById(R.id.cheat_button);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_button);
        ImageButton prevButton = (ImageButton) findViewById(R.id.previous_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if (trueButton != null) {
            trueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsCheater) {
                        Toast.makeText(QuizActivity.this, R.string.judgment_toast,
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(QuizActivity.this,
                                mQuestionBank[mCurrentIndex].isAnswerTrue() ? "TRUE" : "FALSE",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (falseButton != null) {
            falseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsCheater) {
                        Toast.makeText(QuizActivity.this, R.string.judgment_toast,
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(QuizActivity.this,
                                mQuestionBank[mCurrentIndex].isAnswerTrue() ? "FALSE" : "TRUE",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (cheatButton != null) {
            cheatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
            });
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
                }
            });
        }

        if (prevButton != null) {
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(--mCurrentIndex < 0) {
                        mCurrentIndex = 0;
                        Toast.makeText(QuizActivity.this, R.string.first,
                                Toast.LENGTH_SHORT).show();
                    }else{
                        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
                    }
                }
            });
        }

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
            }
        });

        //处理因屏幕旋转导致销毁而回到首页的问题
        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_QUESTION_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
        }
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
    }

    /**
     * 接收返回值
     * @param requestCode 子activity的代号
     * @param resultCode 返回的数据
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null)
                return;
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    /**
     * 设置值避免屏幕旋转导致数据销毁
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_QUESTION_INDEX, mCurrentIndex);
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
    }
}
