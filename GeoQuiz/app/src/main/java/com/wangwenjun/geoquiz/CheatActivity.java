package com.wangwenjun.geoquiz;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.constraint.solver.widgets.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE=
            "com.wangwenjun.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN=
            "com.wangwenjun.geoquiz.answer_shown";
    private static final String TAG = "CheatActivity";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private boolean mIsCheater = false;
    private boolean mAnswerIsTrue;

    private Button mShowAnswer;
    private TextView mAnswerTextView;

    /**
     * 被数据传递者调用
     * @param context
     * @param answerIsTrue 需要传过来的数据
     * @return
     */
    public static Intent newIntent(Context context, boolean answerIsTrue){
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    /**
     *
     * @param result 需要解析数据的intent
     * @return
     */
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        TextView mVersionTextView = (TextView) findViewById(R.id.version_TextView);
        mAnswerTextView = (TextView) findViewById(R.id.answer_TextView);
        mShowAnswer = (Button) findViewById(R.id.show_Answer_Button);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mVersionTextView.setText("API level " + Build.VERSION.SDK_INT);

        if (mShowAnswer != null) {
            mShowAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mAnswerIsTrue)
                        mAnswerTextView.setText(R.string.true_button);
                    else {
                        mAnswerTextView.setText(R.string.false_button);
                    }
                    mIsCheater = true;
                    setAnswerShowResult(true);

                    //按键动画
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int cx = mShowAnswer.getWidth() / 2;
                        int cy = mShowAnswer.getHeight() / 2;
                        float radius = mShowAnswer.getWidth();
                        android.animation.Animator anim = ViewAnimationUtils
                                .createCircularReveal(mShowAnswer, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                super.onAnimationEnd(animation);
                                mShowAnswer.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    }else {
                        mShowAnswer.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        if(savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER);
        }
        setAnswerShowResult(mIsCheater);
    }

    /**
     *
     * @param isAnswerShown 需要设置的返回值
     */
    private void setAnswerShowResult(boolean isAnswerShown){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState" );
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
    }
}
