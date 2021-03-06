package com.vseminar.data;

import com.vseminar.data.model.Question;
import com.vseminar.data.model.Session;
import com.vseminar.data.model.User;
import com.vseminar.data.model.VSeminarData;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SessionData implements VSeminarData<Session> {

    private static volatile SessionData INSTANCE = null;
    private Map<Long, Session> sessions;
    private AtomicLong nextId;

    private SessionData() {
        nextId = new AtomicLong();
        sessions = new LinkedHashMap<>();
    }

    public synchronized static SessionData getInstance() {
        if(INSTANCE == null) {
            synchronized (SessionData.class) {
                if(INSTANCE == null) {
                    INSTANCE = new SessionData();
                }
            }
        }

        return INSTANCE;
    }


    @Override
    public synchronized Session findOne(long id) {

        Session session = sessions.get(id);

        if(session != null) return session;
        return new Session();
    }

    @Override
    public synchronized List<Session> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(sessions.values()));
    }

    @Override
    public int count() {
        return sessions.size();
    }

    @Override
    public synchronized Session save(Session session) {
        List<Session> checkSessions = findByTitle(session.getTitle());

        if(session.getId() == null) {
            if(checkSessions.size() > 0) {
                throw new IllegalArgumentException("Duplicated session name");
            }

            session.setId(nextId.incrementAndGet());
            sessions.put(session.getId(), session);

            return session;
        }

        if(sessions.containsKey(session.getId())) {
            if(session.getId() != checkSessions.get(0).getId() && session.getTitle().equals(checkSessions.get(0).getTitle())) {
                throw new IllegalArgumentException("Duplicated session title");
            }

            sessions.put(session.getId(), session);
            return session;
        }

        throw new IllegalArgumentException("No session with id " + session.getId() + " found");
    }

    @Override
    public synchronized void delete(long id) {
        Session session = findOne(id);
        if(session == null) {
            throw new IllegalArgumentException("Session with id " + id + " not found");
        }

        sessions.remove(session.getId());
    }


    public synchronized List<Session> findByTitle(String title) {
        List<Session> list = findAll();

        List<Session> results = list.stream().filter(session -> session.getTitle().equals(title)).collect(Collectors.toList());
        return results;
    }


    public synchronized List<Session> findByOwner(User owner) {
        List<Session> list = findAll();

        List<Session> results = list.stream().filter(session -> session.getId() == owner.getId()).collect(Collectors.toList());
        return results;
    }

    public synchronized void addMessage(Question question) {
        Session session = sessions.get(question.getSessionId());
        Set<Long> questions = session.getQuestions();

        questions.add(question.getId());
    }
}
