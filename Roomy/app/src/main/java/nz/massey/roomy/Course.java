package nz.massey.roomy;

import androidx.room.*;

@Entity
class Course {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String campus;
    Course (String name, String campus) {
        this.name=name;
        this.campus=campus;
    }
}
