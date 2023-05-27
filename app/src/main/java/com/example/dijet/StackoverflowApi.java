package com.example.dijet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StackoverflowApi {

    @GET("/questions?order=desc&sort=activity&site=stackoverflow")
    Call<QuestionsListResponseSchema> lastActivityQuestions(
            @Query("pagesize") Integer pageSize
    );

    @GET("/questions/{questionId}?site=stackoverflow&filter=withbody")
    Call<SingleQuestionsResponseSchema> questionDetails(
            @Path("questionId") String questionId
    );

}
