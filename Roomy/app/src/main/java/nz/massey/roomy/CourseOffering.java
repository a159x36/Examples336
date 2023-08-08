package nz.massey.roomy;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;
@Entity(foreignKeys = {
        @ForeignKey(entity = Course.class,
                parentColumns = "id",
                childColumns = "course_id"),
        @ForeignKey(entity = Lecturer.class,
                parentColumns = "id",
                childColumns = "lecturer_id")})
public class CourseOffering {
  @PrimaryKey(autoGenerate = true)
  public long id;
    public long course_id;
    public long lecturer_id;
    public int year;
    public int semester;
    CourseOffering(long course_id,long lecturer_id, int year, int semester) {
        this.course_id=course_id;
        this.lecturer_id=lecturer_id;
        this.year=year;
        this.semester=semester;
    }
}
