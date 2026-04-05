package com.learninglogs.entity;

import java.sql.Timestamp;

/**
 * Topic entity — represents a learning topic.
 * Maps to the `topics` table in the database.
 *
 * Complete from Week 2. Added setId() for Week 4 edit functionality.
 * Week 5 adds userId to associate topics with a specific user.
 */
public class Topic {

    private int id;
    private String name;
    private int userId;          // NEW for Week 5 — links topic to a user
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Topic(String name) {
        this.name = name;
    }

    // UPDATED for Week 5 — added userId parameter
    // Week 4 was: Topic(int id, String name, Timestamp createdAt, Timestamp updatedAt)
    public Topic(int id, String name, int userId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getUserId() { return userId; }           // NEW for Week 5
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUserId(int userId) { this.userId = userId; }  // NEW for Week 5

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (Created: " + createdAt + ")";
    }
}
