package com.learninglogs.controller;

import com.learninglogs.dao.EntryDao;
import com.learninglogs.dao.EntryDaoImpl;
import com.learninglogs.dao.TopicDao;
import com.learninglogs.dao.TopicDaoImpl;
import com.learninglogs.entity.Entry;
import com.learninglogs.entity.Topic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * EntryServlet — handles all entry-related HTTP requests.
 *
 * URL: /entry
 *
 * GET actions:
 *   (default)      -> list entries for a topic  -> entry-list.jsp
 *   ?action=new    -> show add form             -> entry-add-edit.jsp
 *   ?action=edit   -> show edit form            -> entry-add-edit.jsp (pre-filled)
 *   ?action=search -> search entries in a topic -> entry-list.jsp (filtered)
 *
 * POST actions:
 *   action=add     -> insert new entry   -> redirect to /entry?topicid=X
 *   action=edit    -> update entry       -> redirect to /entry?topicid=X
 *   action=delete  -> delete entry       -> redirect to /entry?topicid=X
 *
 * KEY DIFFERENCE FROM TopicServlet:
 *   Every entry belongs to a topic, so ALL URLs include ?topicid=X.
 *   This servlet needs TWO DAOs — EntryDao for entries AND TopicDao
 *   to fetch the topic name for display.
 *
 * Week 7 workshop: adds ownership check at the top of doGet and doPost
 *   to prevent URL manipulation attacks.
 */

@WebServlet("/entry")
public class EntryServlet extends HttpServlet {

    private final EntryDao entryDao = new EntryDaoImpl();
    private final TopicDao topicDao = new TopicDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // ============================================================
        // TODO 3: Entry Ownership Check (doGet)
        // ============================================================
        // Before any existing logic, validate that the logged-in user
        // actually owns the topic they're trying to access entries for.
        //
        // Steps:
        //   1. Get the topicId from the request parameter
        //   2. Get the User object from the session
        //   3. Call topicDao.checkUserForTopic(user.getId(), topicId)
        //   4. If false — redirect to /topic (user doesn't own this topic)
        //   5. If true — continue with existing logic below
        //
        // WHY IS THIS NEEDED?
        // Without this check, any user can access any topic's entries
        // by changing the topicid in the URL:
        //   testuser logs in -> clicks their topic -> /entry?topicid=1
        //   testuser manually changes URL to -> /entry?topicid=6
        //   Without check: shows demouser's entries (security hole!)
        //   With check: redirected to /topic (access denied)
        //
        // IMPORTANT: This check must be at the TOP of both doGet AND
        // doPost (TODO 3 covers both). The topicId is already parsed
        // later in the existing code, but we need it early for the check.
        //
        // We use redirect (not forward) because the user is trying to
        // access something they shouldn't — we want the URL to change
        // to /topic so they can't just refresh and retry.
        //
        // The complete code (add at the very top of doGet, before
        // the existing String action = ... line):
        //
        //   int topicId = Integer.parseInt(request.getParameter("topicid"));
        //   User user = (User) SessionUtil.getAttribute(request, "user");
        //   if (!topicDao.checkUserForTopic(user.getId(), topicId)) {
        //       response.sendRedirect(request.getContextPath() + "/topic");
        //       return;
        //   }
        //
        // NOTE: The same check goes at the top of doPost as well.
        // You will also need to add these imports:
        //   import com.learninglogs.entity.User;
        //   import com.learninglogs.utils.SessionUtil;
        // ============================================================

        String action = request.getParameter("action");
        int topicId = Integer.parseInt(request.getParameter("topicid"));

        if (action == null) {
            ArrayList<Entry> entries = entryDao.fetchEntriesByTopicId(topicId);
            Topic topic = topicDao.findTopicById(topicId);
            request.setAttribute("entries", entries);
            request.setAttribute("topic", topic);
            request.getRequestDispatcher("/WEB-INF/views/entry-list.jsp")
                   .forward(request, response);
        }

        else if ("new".equals(action)) {
            Topic topic = topicDao.findTopicById(topicId);
            request.setAttribute("topic", topic);
            request.getRequestDispatcher("/WEB-INF/views/entry-add-edit.jsp")
                   .forward(request, response);
        }
        else if ("edit".equals(action)) {
            int entryId = Integer.parseInt(request.getParameter("entryid"));
            Entry entry = entryDao.findEntryById(entryId);
            Topic topic = topicDao.findTopicById(topicId);
            request.setAttribute("entry", entry);
            request.setAttribute("topic", topic);
            request.getRequestDispatcher("/WEB-INF/views/entry-add-edit.jsp")
                   .forward(request, response);
        }

        else if ("search".equals(action)) {
            String keyword = request.getParameter("search");
            ArrayList<Entry> entries;
            if (keyword == null || keyword.trim().isEmpty()) {
                entries = entryDao.fetchEntriesByTopicId(topicId);
            } else {
                entries = entryDao.searchEntries(topicId, keyword.trim());
            }
            Topic topic = topicDao.findTopicById(topicId);
            request.setAttribute("entries", entries);
            request.setAttribute("topic", topic);
            request.setAttribute("searchKeyword", keyword);
            request.getRequestDispatcher("/WEB-INF/views/entry-list.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Ownership check (same as doGet — see TODO 3)

        String action = request.getParameter("action");
        int topicId = Integer.parseInt(request.getParameter("topicid"));

        if ("add".equals(action)) {
            String title = request.getParameter("title");
            String text = request.getParameter("text");
            String link = request.getParameter("link");
            String image = request.getParameter("image");

            if (title == null || title.trim().isEmpty()
                    || text == null || text.trim().isEmpty()) {
                request.setAttribute("error", "Title and description are required.");
                Topic topic = topicDao.findTopicById(topicId);
                request.setAttribute("topic", topic);
                request.getRequestDispatcher("/WEB-INF/views/entry-add-edit.jsp")
                       .forward(request, response);
                return;
            }

            Entry entry = new Entry(title.trim(), text.trim(), topicId);
            entry.setLink(link);
            entry.setImage(image);
            entryDao.insertEntry(entry);
            response.sendRedirect(request.getContextPath() + "/entry?topicid=" + topicId);
        }

        else if ("edit".equals(action)) {
            int entryId = Integer.parseInt(request.getParameter("entryid"));
            String title = request.getParameter("title");
            String text = request.getParameter("text");
            String link = request.getParameter("link");
            String image = request.getParameter("image");

            if (title == null || title.trim().isEmpty()
                    || text == null || text.trim().isEmpty()) {
                request.setAttribute("error", "Title and description are required.");
                Entry entry = entryDao.findEntryById(entryId);
                Topic topic = topicDao.findTopicById(topicId);
                request.setAttribute("entry", entry);
                request.setAttribute("topic", topic);
                request.getRequestDispatcher("/WEB-INF/views/entry-add-edit.jsp")
                       .forward(request, response);
                return;
            }

            Entry entry = new Entry(title.trim(), text.trim(), topicId);
            entry.setId(entryId);
            entry.setLink(link);
            entry.setImage(image);
            entryDao.updateEntry(entry);
            response.sendRedirect(request.getContextPath() + "/entry?topicid=" + topicId);
        }

        else if ("delete".equals(action)) {
            int entryId = Integer.parseInt(request.getParameter("entryid"));
            entryDao.deleteEntry(entryId);
            response.sendRedirect(request.getContextPath() + "/entry?topicid=" + topicId);
        }
    }
}
