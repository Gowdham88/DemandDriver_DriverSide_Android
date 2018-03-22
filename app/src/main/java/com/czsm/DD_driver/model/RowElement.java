package com.czsm.DD_driver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by macbook on 27/07/16.
 */
public class RowElement {

        @SerializedName("elements")
        @Expose
        private List<Element> elements = new ArrayList<Element>();

        /**
         *
         * @return
         * The elements
         */
        public List<Element> getElements() {
            return elements;
        }

        /**
         *
         * @param elements
         * The elements
         */
        public void setElements(List<Element> elements) {
            this.elements = elements;
        }

}
