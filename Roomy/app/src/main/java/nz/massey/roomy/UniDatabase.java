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

    public static MainActivity mActivity;
    private static volatile UniDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static UniDatabase getDatabase(final Context context) {
        mActivity =(MainActivity) context;
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
                long m=dao.insert(new Lecturer("Martin", "43142", "ms3.24"));
                long d=dao.insert(new Lecturer("Daniel", "43136", "ms3.02"));
                long c=dao.insert(new Lecturer("Chris", "43134", "ms2.03"));
                long c101=dao.insert(new Course("159101", "albany"));
                long c102=dao.insert(new Course("159102", "albany"));
                long c201=dao.insert(new Course("159201", "albany"));
                long c272=dao.insert(new Course("159272", "albany"));
                long c235=dao.insert(new Course("159235", "albany"));
                long c261=dao.insert(new Course("159261", "albany"));
                long c234=dao.insert(new Course("159234", "albany"));
                long c236=dao.insert(new Course("159236", "albany"));
                long c336=dao.insert(new Course("159336", "albany"));
                long c302=dao.insert(new Course("159302", "albany"));
                long c341=dao.insert(new Course("159341", "albany"));
                long c731=dao.insert(new Course("159731", "albany"));
                dao.insert(new CourseOffering(c236,m,2020,2));
                dao.insert(new CourseOffering(c336,m,2020,2));
                dao.insert(new CourseOffering(c731,m,2020,1));
                dao.insert(new CourseOffering(c101,m,2020,2));
                dao.insert(new CourseOffering(c102,m,2020,2));
                dao.insert(new CourseOffering(c201,m,2020,1));
                dao.insert(new CourseOffering(c236,m,2019,1));
                dao.insert(new CourseOffering(c336,m,2019,2));
                dao.insert(new CourseOffering(c731,m,2019,1));
                dao.insert(new CourseOffering(c201,d,2020,2));
                dao.insert(new CourseOffering(c272,d,2020,2));
                dao.insert(new CourseOffering(c341,d,2020,1));
                dao.insert(new CourseOffering(c234,d,2020,2));
                dao.insert(new CourseOffering(c235,d,2020,2));
                dao.insert(new CourseOffering(c201,d,2021,1));
                dao.insert(new CourseOffering(c236,d,2021,1));
                dao.insert(new CourseOffering(c336,d,2021,2));
                dao.insert(new CourseOffering(c731,d,2021,1));
                dao.insert(new CourseOffering(c101,c,2020,1));
                dao.insert(new CourseOffering(c102,c,2020,2));
                dao.insert(new CourseOffering(c101,c,2021,1));
                dao.insert(new CourseOffering(c102,c,2021,2));
                dao.insert(new CourseOffering(c101,c,2022,1));
                dao.insert(new CourseOffering(c102,c,2022,2));
                dao.insert(new CourseOffering(c101,c,2023,1));
                dao.insert(new CourseOffering(c102,c,2023,2));
                Log.i("db","Database Populated");
                mActivity.runOnUiThread(() -> {
                    mActivity.updatelects();
                    mActivity.updatecourselist();});
            });
        }
    };
}