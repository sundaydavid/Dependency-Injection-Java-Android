package com.example.dijet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsListActivity extends AppCompatActivity
implements Callback<QuestionsListResponseSchema> {

    private RecyclerView mRecyclerview;
    private QuestionsAdapter mQuestionsAdapter;
    private StackoverflowApi mStackoverflowApi;
    private Call<QuestionsListResponseSchema> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing recycler view
        mRecyclerview = findViewById(R.id.recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mQuestionsAdapter = new QuestionsAdapter(new OnQuestionClickListener() {
            @Override
            public void onQuestionClicked(Question question) {
                QuestionDetailsActivity.start(QuestionsListActivity.this, question.getmId());
            }
        });

        mRecyclerview.setAdapter(mQuestionsAdapter);

        //Retrofit config
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mStackoverflowApi = retrofit.create(StackoverflowApi.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCall = mStackoverflowApi.lastActivityQuestions(20);
        mCall.enqueue(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCall != null){
            mCall.cancel();
        }
    }

    @Override
    public void onResponse(Call<QuestionsListResponseSchema> call, Response<QuestionsListResponseSchema> response) {
        QuestionsListResponseSchema responseSchema;
        if (response.isSuccessful() && (responseSchema = response.body()) != null){
            mQuestionsAdapter.bindData(responseSchema.getQuestions());
        }else {
            onFailure(call,null);
        }
    }

    @Override
    public void onFailure(Call<QuestionsListResponseSchema> call, Throwable t) {

        QuestionsListResponseSchema responseSchema;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(ServerErrorDialogFragment.newInstance(), null)
                .commitAllowingStateLoss();

    }

    public interface OnQuestionClickListener{
        void onQuestionClicked(Question question);
    }

    public static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder>{

        private final OnQuestionClickListener mOnQuestionClickListener;
        private List<Question> mQuestionList = new ArrayList<>(0);

        //View holder
        public class QuestionsViewHolder extends RecyclerView.ViewHolder{

            public TextView mTitle;

            public QuestionsViewHolder(@NonNull View itemView) {
                super(itemView);
                mTitle = itemView.findViewById(R.id.txt_title);
            }
        }

        public QuestionsAdapter(OnQuestionClickListener onQuestionClickListener){
            mOnQuestionClickListener = onQuestionClickListener;
        }

        //DataBinding
        public void bindData(List<Question> questions){
            mQuestionList = new ArrayList<>(questions);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_question_item,parent,false);

            return new QuestionsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.mTitle.setText(mQuestionList.get(position).getmTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnQuestionClickListener.onQuestionClicked(mQuestionList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mQuestionList.size();
        }

    }
}