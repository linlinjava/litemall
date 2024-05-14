package org.linlinjava.litemall.admin.dto;

public class Prompt {
    private String prompt;
    private String imageUrl;

    public Prompt() {
    }

    public Prompt(String question) {
        this.prompt = question;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String question) {
        this.prompt = question;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}