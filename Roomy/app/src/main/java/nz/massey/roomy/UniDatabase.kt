package nz.massey.roomy

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Lecturer::class, Course::class, CourseOffering::class], version = 1)
abstract class UniDatabase : RoomDatabase() {
    abstract fun UniDao(): UniDao
    companion object {
        @Volatile
        private var Instance: UniDatabase? = null
        fun getDatabase(context: Context): UniDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, UniDatabase::class.java, "uni_database")
                    .addCallback(roomDatabaseCallback)
                    .build()
                    .also { Instance = it }
            }
        }
        private val roomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    val dao: UniDao = Instance!!.UniDao()
                    dao.deleteAllOfferings()
                    val m = dao.insert(Lecturer(name="Martin", phone="43142", office="ms3.24"))
                    val d = dao.insert(Lecturer(name="Daniel", phone="43136", office="ms3.02"))
                    val c = dao.insert(Lecturer(name="Chris", phone="43134", office="ms2.03"))
                    val c101 = dao.insert(Course(name="159101", location="albany"))
                    val c102 = dao.insert(Course(name="159102", location="albany"))
                    val c201 = dao.insert(Course(name="159201", location="albany"))
                    val c272 = dao.insert(Course(name="159272", location="albany"))
                    val c235 = dao.insert(Course(name="159235", location="albany"))
                    val c261 = dao.insert(Course(name="159261", location="albany"))
                    val c234 = dao.insert(Course(name="159234", location="albany"))
                    val c236 = dao.insert(Course(name="159236", location="albany"))
                    val c336 = dao.insert(Course(name="159336", location="albany"))
                    val c302 = dao.insert(Course(name="159302", location="albany"))
                    val c341 = dao.insert(Course(name="159341", location="albany"))
                    val c731 = dao.insert(Course(name="159731", location="albany"))
                    dao.insert(CourseOffering(courseId=c236, lecturerId=m, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c336, lecturerId=m, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c731, lecturerId=m, year=2020, semester=1))
                    dao.insert(CourseOffering(courseId=c101, lecturerId=m, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c102, lecturerId=m, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c336, lecturerId=m, year=2019, semester=2))
                    dao.insert(CourseOffering(courseId=c731, lecturerId=m, year=2019, semester=1))
                    dao.insert(CourseOffering(courseId=c201, lecturerId=d, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c272, lecturerId=d, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c341, lecturerId=d, year=2020, semester=1))
                    dao.insert(CourseOffering(courseId=c234, lecturerId=d, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c235, lecturerId=d, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c201, lecturerId=d, year=2021, semester=1))
                    dao.insert(CourseOffering(courseId=c236, lecturerId=d, year=2021, semester=1))
                    dao.insert(CourseOffering(courseId=c336, lecturerId=d, year=2021, semester=2))
                    dao.insert(CourseOffering(courseId=c731, lecturerId=d, year=2021, semester=1))
                    dao.insert(CourseOffering(courseId=c101, lecturerId=c, year=2020, semester=1))
                    dao.insert(CourseOffering(courseId=c102, lecturerId=c, year=2020, semester=2))
                    dao.insert(CourseOffering(courseId=c101, lecturerId=c, year=2021, semester=1))
                    dao.insert(CourseOffering(courseId=c102, lecturerId=c, year=2021, semester=2))
                    dao.insert(CourseOffering(courseId=c101, lecturerId=c, year=2022, semester=1))
                    dao.insert(CourseOffering(courseId=c102, lecturerId=c, year=2022, semester=2))
                    dao.insert(CourseOffering(courseId=c101, lecturerId=c, year=2023, semester=1))
                    dao.insert(CourseOffering(courseId=c102, lecturerId=c, year=2023, semester=2))
                    dao.insert(CourseOffering(courseId=c261, lecturerId=d, year=2025, semester=1))
                    dao.insert(CourseOffering(courseId=c302, lecturerId=d, year=2025, semester=1))
                    dao.insert(CourseOffering(courseId=c341, lecturerId=d, year=2025, semester=2))
                    Log.i("db", "Database Populated")
                }
            }
        }
    }
}