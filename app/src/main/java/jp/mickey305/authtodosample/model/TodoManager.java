package jp.mickey305.authtodosample.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmResults;

public class TodoManager {
    private Realm mRealm;

    public TodoManager(Context context) { mRealm = Realm.getInstance(context); }

    public Todo find(int id) {
        return mRealm.where(Todo.class).equalTo("id", id).findAll().first();
    }

    public RealmResults<Todo> findAll() { return mRealm.where(Todo.class).findAll(); }

    public void insert(String name, boolean completed) {
        mRealm.beginTransaction();
        Todo todo = mRealm.createObject(Todo.class);
        todo.setId((int)mRealm.where(Todo.class).maximumInt("id") + 1);
        todo.setName(name);
        todo.setCompleted(completed);
        mRealm.commitTransaction();
    }

    public void insert(String name, String sentence, boolean completed) {
        mRealm.beginTransaction();
        Todo todo = mRealm.createObject(Todo.class);
        todo.setId((int)mRealm.where(Todo.class).maximumInt("id") + 1);
        todo.setName(name);
        todo.setSentence(sentence);
        todo.setCompleted(completed);
        mRealm.commitTransaction();
    }

    public void update(int id, String name, boolean completed) {
        mRealm.beginTransaction();
        Todo todo = find(id);
        todo.setName(name);
        todo.setCompleted(completed);
        mRealm.commitTransaction();
    }

    public void update(Todo todo, String name) {
        mRealm.beginTransaction();
        todo.setName(name);
        mRealm.commitTransaction();
    }

    public void update(Todo todo, String name, String sentence) {
        mRealm.beginTransaction();
        todo.setName(name);
        todo.setSentence(sentence);
        mRealm.commitTransaction();
    }

    public void update(Todo todo, boolean completed) {
        mRealm.beginTransaction();
        todo.setCompleted(completed);
        mRealm.commitTransaction();
    }

    public void deleteCompleted() {
        mRealm.beginTransaction();
        RealmResults<Todo> results = mRealm.where(Todo.class).equalTo("completed", true).findAll();
        results.clear();
        mRealm.commitTransaction();
    }
}
