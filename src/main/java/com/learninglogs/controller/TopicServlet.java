package com.learninglogs.controller;

import com.learninglogs.dao.TopicDao;
import com.learninglogs.dao.TopicDaoImpl;
import com.learninglogs.entity.Topic;
import com.learninglogs.entity.User;
import com.learninglogs.utils.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TopicServlet — handles all topic-related HTTP requests.
 *
 * URL: /topic
 *
 * GET actions:
 *   (default)      -> list user's topics -> topic-list.jsp
 *   ?action=new    -> show add form      -> topic-add-edit.jsp
 *   ?action=edit   -> show edit form     -> topic-add-edit.jsp (pre-filled)
 *   ?action=search -> search user's topics -> topic-list.jsp (filtered)
 *
 * POST actions:
 *   action=add     -> insert new topic  -> redirect to /topic
 *   action=edit    -> update topic      -> redirect to /topic
 *   action=delete  -> delete topic      -> redirect to /topic
 *
 * Complete from Week 7 tutorial — uses session userId for all operations.
 */
@WebServlet("/topic")
public class TopicServlet extends HttpServlet {

    private final TopicDao topicDao = new TopicDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        User user = (User) SessionUtil.getAttribute(request, "user");

        if (action == null) {
            ArrayList<Topic> topics = topicDao.fetchAllTopicsByUserId(user.getId());
            request.setAttribute("topics", topics);
            request.getRequestDispatcher("/WEB-INF/views/topic-list.jsp")
                   .forward(request, response);
        }
        else if ("new".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
                   .forward(request, response);
        }
        else if ("edit".equals(action)) {
            int topicId = Integer.parseInt(request.getParameter("topicid"));
            // Ownership check — prevent editing another user's topic via URL manipulation
            if (!topicDao.checkUserForTopic(user.getId(), topicId)) {
                response.sendRedirect(request.getContextPath() + "/topic");
                return;
            }
            Topic topic = topicDao.findTopicById(topicId);
            request.setAttribute("topic", topic);
            request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
                   .forward(request, response);
        }
        else if ("search".equals(action)) {
            String keyword = request.getParameter("search");
            ArrayList<Topic> topics;
            if (keyword == null || keyword.trim().isEmpty()) {
                topics = topicDao.fetchAllTopicsByUserId(user.getId());
            } else {
                topics = topicDao.searchTopicsByUserId(user.getId(), keyword.trim());
            }
            request.setAttribute("topics", topics);
            request.setAttribute("searchKeyword", keyword);
            request.getRequestDispatcher("/WEB-INF/views/topic-list.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String topicName = request.getParameter("topic");

            if (topicName == null || topicName.trim().isEmpty()) {
                request.setAttribute("error", "Topic name cannot be empty.");
                request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
                       .forward(request, response);
                return;
            }

            Topic newTopic = new Topic(topicName.trim());
            User user = (User) SessionUtil.getAttribute(request, "user");
            newTopic.setUserId(user.getId());
            boolean success = topicDao.insertTopic(newTopic);

            if (!success) {
                request.setAttribute("error", "Topic already exists.");
                request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
                       .forward(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/topic");
        }
        else if ("edit".equals(action)) {
            int topicId = Integer.parseInt(request.getParameter("topicid"));
            User user = (User) SessionUtil.getAttribute(request, "user");
            // Ownership check — prevent editing another user's topic via URL manipulation
            if (!topicDao.checkUserForTopic(user.getId(), topicId)) {
                response.sendRedirect(request.getContextPath() + "/topic");
                return;
            }
            String topicName = request.getParameter("topic");

            if (topicName == null || topicName.trim().isEmpty()) {
                request.setAttribute("error", "Topic name cannot be empty.");
                Topic topic = topicDao.findTopicById(topicId);
                request.setAttribute("topic", topic);
                request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
                       .forward(request, response);
                return;
            }

            Topic topic = new Topic(topicName.trim());
            topic.setId(topicId);
            topicDao.updateTopic(topic);
            response.sendRedirect(request.getContextPath() + "/topic");
        }
        else if ("delete".equals(action)) {
            int topicId = Integer.parseInt(request.getParameter("topicid"));
            User user = (User) SessionUtil.getAttribute(request, "user");
            // Ownership check — prevent deleting another user's topic via URL manipulation
            if (!topicDao.checkUserForTopic(user.getId(), topicId)) {
                response.sendRedirect(request.getContextPath() + "/topic");
                return;
            }
            topicDao.deleteTopic(topicId);
            response.sendRedirect(request.getContextPath() + "/topic");
        }
    }
}
