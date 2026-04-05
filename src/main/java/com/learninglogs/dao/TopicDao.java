package com.learninglogs.dao;

import com.learninglogs.entity.Topic;
import java.util.ArrayList;

/**
 * Topic DAO Interface — defines database operations for topics.
 * Complete from Week 2: insertTopic, fetchAllTopics, findTopicByName.
 * Week 4 adds: findTopicById, updateTopic, deleteTopic, searchTopics.
 * Week 5: insertTopic now includes userId from the Topic object.
 * Week 7 tutorial: adds user-scoped methods (fetchAllTopicsByUserId, searchTopicsByUserId).
 * Week 7 workshop: adds checkUserForTopic for ownership validation.
 */
public interface TopicDao {
    boolean insertTopic(Topic topic);
    ArrayList<Topic> fetchAllTopics();
    Topic findTopicByName(String name);

    Topic findTopicById(int id);
    boolean updateTopic(Topic topic);
    boolean deleteTopic(int id);
    ArrayList<Topic> searchTopics(String keyword);

    ArrayList<Topic> fetchAllTopicsByUserId(int userId);
    ArrayList<Topic> searchTopicsByUserId(int userId, String keyword);

    // ============================================================
    // TODO 2: Add Ownership Check Method Signature
    // ============================================================
    // Add a method signature that checks if a topic belongs to a
    // specific user.
    //
    // This method will be used by EntryServlet (TODO 4) to validate
    // that the logged-in user actually owns the topic before showing
    // or modifying its entries.
    //
    // WHY IS THIS NEEDED?
    // Without this check, any logged-in user can view/edit/delete
    // ANY user's entries just by changing the topicid in the URL:
    //   /entry?topicid=1  (testuser's topic)
    //   /entry?topicid=6  (demouser's topic)
    //
    // Even though topics are user-scoped on the topic list page,
    // the entry page only needs a topicid — it doesn't verify
    // ownership. This is a URL manipulation vulnerability.
    //
    // The method signature:
    //
    //   boolean checkUserForTopic(int userId, int topicId);
    //
    // Returns true if the topic belongs to the user, false otherwise.
    //
    // The complete code:
    //
    //   boolean checkUserForTopic(int userId, int topicId);
    //
    // ============================================================
    boolean checkUserForTopic(int userId, int topicId);
}
