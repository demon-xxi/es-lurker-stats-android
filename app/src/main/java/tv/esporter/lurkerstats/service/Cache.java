package tv.esporter.lurkerstats.service;

import android.os.Environment;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;


public class Cache<T> {

    DB db;
    Class<T> persistentClass;

    public Cache(String dir,  Class cls) throws SnappydbException {
        persistentClass = cls;
        db = DBFactory.open(dir, persistentClass.getSimpleName());

    }

    public void put(String name, T value) throws SnappydbException {
        db.put(name, value);
    }

    public T get(String name) throws SnappydbException {
       return db.getObject(name, persistentClass);
    }

    public boolean exists(String name) throws SnappydbException {
        return db.exists(name);
    }


    public void delete(String name) throws SnappydbException {
        db.del(name);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        db.close();
    }
}
