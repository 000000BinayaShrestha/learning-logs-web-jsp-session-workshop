package com.learninglogs.dao;

import com.learninglogs.entity.Entry;
import java.util.ArrayList;

/**
 * Entry DAO Interface — defines database operations for entries.
 * Complete from Week 2: insertEntry, fetchAllEntries, fetchEntriesByTopicId.
 * Week 4 adds: findEntryById, updateEntry, deleteEntry, searchEntries.
 */
public interface EntryDao {
    boolean insertEntry(Entry entry);
    ArrayList<Entry> fetchAllEntries();
    ArrayList<Entry> fetchEntriesByTopicId(int topicId);

    Entry findEntryById(int id);
    boolean updateEntry(Entry entry);
    boolean deleteEntry(int id);
    ArrayList<Entry> searchEntries(int topicId, String keyword);
}
