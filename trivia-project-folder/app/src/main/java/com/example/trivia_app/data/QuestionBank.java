package com.example.trivia_app.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia_app.controller.AppController;
import com.example.trivia_app.model.Questions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// contains a method that can
public class QuestionBank {

    ArrayList<Questions> questionsArrayList=new ArrayList<>();
    // This stores the questions...

    private String url="https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Questions> getQuestions(final AnswerListAsyncResponse callBack){

        JsonArrayRequest jsonArrayRequest =new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i=0;i<response.length();i++){
                            try {
                                Questions question = new Questions();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnsTrue(response.getJSONArray(i).getBoolean(1));

                                //add questions to list
                                questionsArrayList.add(question);
                                //Log.d("hello", "onResponse: "+question);
/*
                                Log.d("", "onResponse: "+question);
                                Log.d("", "jsonQus: "+response.getJSONArray(i).get(0).toString());
                                Log.d("", "jsonAns: "+response.getJSONArray(i).getBoolean(1));
*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (null!= callBack) callBack.processFinished(questionsArrayList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionsArrayList;
    }

}
