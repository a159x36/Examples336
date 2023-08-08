package nz.massey.roomy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface UniDao {
    @Insert
    long insert(Course course);
    @Query("SELECT * FROM course")
    List<Course> getAllCourses();
    @Insert
    long insert(Lecturer lect);
    @Query("DELETE FROM courseoffering")
    void deleteAllOfferings();
    @Transaction
    @Query("SELECT course.* FROM course,courseoffering,lecturer WHERE lecturer.name=:lect AND lecturer_id=lecturer.id AND course_id=course.id AND year=:year")
    List<Course> getCourses(String lect, int year);
    @Query("SELECT * FROM courseoffering")
    List<CourseOffering> getAllOfferings();
    @Insert
    long insert(CourseOffering co);
}
