package tv.esporter.lurkerstats.service;

import android.content.Context;

import com.esotericsoftware.kryo.Kryo;
import com.snappydb.DB;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Date;

import tv.esporter.lurkerstats.api.TwitchApi;
import tv.esporter.lurkerstats.api.TwitchStream;
import tv.esporter.lurkerstats.util.Build;


public class Cache2<T> {

    final DB db;
    final String prefix;
    final Class<T> persistentClass;

    public Cache2(Context ctx, Class cls) throws SnappydbException {
        db = SnappyDB.with(ctx);
        Kryo kryo = db.getKryoInstance();

        kryo.register(StatsItem.class);
        kryo.register(TwitchStream.class);
        kryo.register(TwitchApi.class);
        kryo.register(new ArrayList<StatsItem>().getClass());

        persistentClass = cls;
        prefix = cls.getSimpleName();
    }

    private String id(String name){
        return Build.key(prefix, name);
    }

    private String ttl(String name){
        return Build.key("TTL", id(name));
    }

    /*
     * Puts data into db and records insertion timestamp.
     * @see getFresh(String, Date)
     */
    public void put(String name, T value) throws SnappydbException {
        db.put(id(name), value);
        db.putLong(ttl(name), new Date().getTime());
    }

    public void putArray(String name, T[] value) throws SnappydbException {
        db.put(id(name), value);
        db.putLong(ttl(name), new Date().getTime());
    }


    public T get(String name) throws SnappydbException {
        return db.getObject(id(name), persistentClass);
    }

    public T[] getArray(String name) throws SnappydbException {
        return db.getObjectArray(id(name), persistentClass);
    }


    /*
     * Get data from cache id it was written at or after specified by {@link since} parameter.
     * Otherwise throw exception;
     */
    public T getFresh(String name, long milliseconds) throws SnappydbException {
        String ttl = ttl(name);
        if (!db.exists(ttl) || (new Date().getTime()-db.getLong(ttl)) >= milliseconds ){
            throw new SnappydbException("NotFound");
        }
        return db.getObject(id(name), persistentClass);
    }

    /*
 * Get data from cache id it was written at or after specified by {@link since} parameter.
 * Otherwise throw exception;
 */
    public T[] getArrayFresh(String name, long milliseconds) throws SnappydbException {
        String ttl = ttl(name);
        if (!db.exists(ttl) || (new Date().getTime()-db.getLong(ttl)) >= milliseconds ){
            throw new SnappydbException("NotFound");
        }
        return db.getObjectArray(id(name), persistentClass);
    }

    public boolean exists(String name) throws SnappydbException {
        return db.exists(name);
    }

    public void delete(String name) throws SnappydbException {
        db.del(id(name));
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
