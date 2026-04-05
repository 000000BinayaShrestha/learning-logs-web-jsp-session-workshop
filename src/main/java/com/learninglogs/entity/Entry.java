package com.learninglogs.entity;

import java.sql.Timestamp;

/**
 * Entry entity — represents a learning note under a Topic.
 * Maps to the `entries` table in the database.
 *
 * Updated for Week 4: added title, link, and image fields.
 */
public class Entry {

    private int id;
    private String title;
    private String text;
    private int topicId;
    private String link;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Entry(String title, String text, int topicId) {
        this.title = title;
        this.text = text;
        this.topicId = topicId;
    }

    public Entry(int id, String title, String text, int topicId,
                 String link, String image,
                 Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.topicId = topicId;
        this.link = link;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getText() { return text; }
    public int getTopicId() { return topicId; }
    public String getLink() { return link; }
    public String getImage() { return image; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setText(String text) { this.text = text; }
    public void setLink(String link) { this.link = link; }
    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " (Topic ID: " + topicId + ", Created: " + createdAt + ")";
    }
}
