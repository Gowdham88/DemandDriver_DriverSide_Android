package com.czsm.driverin.model;

/**
 * Created by macbook on 27/07/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Duration {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private int value;

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The value
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(int value) {
        this.value = value;
    }

}
