package com.example.dijet;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class SingleQuestionsResponseSchema {

    @SerializedName("items")
    private final List<QuestionWithBody> mQuestions;

    public SingleQuestionsResponseSchema(QuestionWithBody questions) {
       mQuestions = Collections.singletonList(questions);
    }

    public QuestionWithBody getmQuestions() {
        return mQuestions.get(0);
    }
}
