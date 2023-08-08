package nz.massey.roomy;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Lecturer.class, Course.class, CourseOffering.class}, version = 1)
public abstract class UniDatabase extends RoomDatabase {
    public abstract UniDao UniDao();

    private static volatile UniDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static UniDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UniDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UniDatabase.class, "uni_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                UniDao dao = INSTANCE.UniDao();
                dao.deleteAllOfferings();
                Lecturer lect = new Lecturer("Martin", "43142", "ms3.24");
                long m=dao.insert(lect);
                long c101=dao.insert(new Course("159101", "albany"));
                long c102=dao.insert(new Course("159102", "albany"));
                long c202=dao.insert(new Course("159202", "albany"));
                long c236=dao.insert(new Course("159236", "albany"));
                long c336=dao.insert(new Course("159336", "albany"));
                long c736=dao.insert(new Course("159736", "albany"));
                dao.insert(new CourseOffering(c236,m,2020,2));
                dao.insert(new CourseOffering(c336,m,2020,2));
                dao.insert(new CourseOffering(c736,m,2020,1));
                dao.insert(new CourseOffering(c101,m,2020,2));
                dao.insert(new CourseOffering(c102,m,2020,2));
                dao.insert(new CourseOffering(c202,m,2020,1));
                dao.insert(new CourseOffering(c236,m,2019,1));
                dao.insert(new CourseOffering(c336,m,2019,2));
                dao.insert(new CourseOffering(c736,m,2019,1));
                Log.i("db","Database Populated");
            });
        }
    };
}