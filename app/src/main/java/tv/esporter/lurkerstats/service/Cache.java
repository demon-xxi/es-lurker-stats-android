package tv.esporter.lurkerstats.service;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.Date;


public class Cache<T> {

    final DB db;
    final String prefix;
    final Class<T> persistentClass;

    public Cache(Context ctx, Class cls) throws SnappydbException {
        db = SnappyDB.with(ctx);
        persistentClass = cls;
        prefix = cls.getSimpleName();
    }

    private String id(String name){
        return String.format("#%s#%s", prefix, name);
    }

    private String ttl(String name){
        return String.format("#TTL#%s", name);
    }

    /*
     * Puts data into db and records insertion timestamp.
     * @see getFresh(String, Date)
     */
    public void put(String name, T value) throws SnappydbException {
        db.put(name, value);
        db.putLong(ttl(name), new Date().getTime());
    }

    public T get(String name) throws SnappydbException {
       return db.getObject(name, persistentClass);
    }

    /*
     * Get data from cache id it was written at or after specified by {@link since} parameter.
     * Otherwise throw exception;
     */
    public T getFresh(String name, Date since) throws SnappydbException {
        String ttl = ttl(name);
        if (!db.exists(ttl) || db.getLong(ttl) < since.getTime()){
            throw new SnappydbException("NotFound");
        }
        return db.getObject(name, persistentClass);
    }

    public boolean exists(String name) throws SnappydbException {
        return db.exists(name);
    }

    public void delete(String name) throws SnappydbException {
        db.del(name);
        if (db.exists(ttl(name))) db.del(ttl(name));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        synchronized (db){
            if (db != null && db.isOpen()){
                db.close();
            }
        }
    }
}
