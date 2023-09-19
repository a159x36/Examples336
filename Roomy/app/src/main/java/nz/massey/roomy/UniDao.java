package nz.massey.roomy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface UniDao {
    @Insert
    long insert(Course course);
    @Query("SELECT * FROM course")
    List<Course> getAllCourses();
    @Query("SELECT * FROM lecturer")
    List<Lecturer> getLecturers();

    @Insert
    long insert(Lecturer lect);
    @Query("DELETE FROM courseoffering")
    void deleteAllOfferings();
    @Query("DELETE FROM courseoffering WHERE id=:id")
    void deleteOffering(long id);
    //@Transaction
    //@Query("SELECT course.* FROM course,courseoffering,lecturer WHERE lecturer.name=:lect AND lecturer_id=lecturer.id AND course_id=course.id AND year=:year")
    //List<Course> getCourses(String lect, int year);
    //@Query("SELECT * FROM courseoffering")
    //List<CourseOffering> getAllOfferings();
    @Query("SELECT * FROM courseoffering where id=:id")
    CourseOffering getOffering(long id);
    @Insert
    long insert(CourseOffering co);
    @Query("SELECT courseoffering.id as id, course.name AS coursename, lecturer.name AS lecturername, courseoffering.year, courseoffering.semester FROM course,courseoffering,lecturer WHERE lecturer.name=:lect AND lecturer_id=lecturer.id AND course_id=course.id")
    List<CourseInfo> getCourseInfo(String lect);
    @Query("UPDATE courseoffering SET lecturer_id=:lecturer_id, course_id=:course_id, year=:year, semester=:semester where id=:id")
    void update(long id,long lecturer_id,long course_id,int year,int semester);
}
