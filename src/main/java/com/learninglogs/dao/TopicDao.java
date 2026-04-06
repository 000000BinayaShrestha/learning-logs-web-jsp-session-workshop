package com.learninglogs.dao;

import com.learninglogs.entity.Topic;
import java.util.ArrayList;

/**
 * Topic DAO Interface — defines database operations for topics.
 * Complete from Week 2: insertTopic, fetchAllTopics, findTopicByName.
 * Week 4 adds: findTopicById, updateTopic, deleteTopic, searchTopics.
 * Week 5: insertTopic now includes userId from the Topic object.
 * Week 7 tutorial: adds user-scoped methods (fetchAllTopicsByUserId, searchTopicsByUserId).
 * Week 7 workshop: adds checkUserForTopic for ownership validation (provided).
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

    // Ownership check — used by TopicServlet and EntryServlet (TODO 3)
    // to verify a topic belongs to the logged-in user before allowing access.
    boolean checkUserForTopic(int userId, int topicId);
}
