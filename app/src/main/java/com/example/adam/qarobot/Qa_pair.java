package com.example.adam.qarobot;

public class Qa_pair {
    private String question;
    private String answer;

    public Qa_pair(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }
}
