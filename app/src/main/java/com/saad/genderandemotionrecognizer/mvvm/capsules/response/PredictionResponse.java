package com.saad.genderandemotionrecognizer.mvvm.capsules.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PredictionResponse {

    @SerializedName("emotion")
    @Expose
    private String emotion;
    @SerializedName("female_prob")
    @Expose
    private String femaleProb;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("male_prob")
    @Expose
    private String male_prob;

    public PredictionResponse(String emotion, String femaleProb, String gender, String male_prob) {
        this.emotion = emotion;
        this.femaleProb = femaleProb;
        this.gender = gender;
        this.male_prob = male_prob;
    }

    public String getFemaleProb() {
        return femaleProb;
    }

    public void setFemaleProb(String femaleProb) {
        this.femaleProb = femaleProb;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMale_prob() {
        return male_prob;
    }

    public void setMale_prob(String male_prob) {
        this.male_prob = male_prob;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
