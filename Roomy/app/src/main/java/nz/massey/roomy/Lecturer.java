package nz.massey.roomy;

import androidx.room.*;

@Entity
class Lecturer {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String phone;
    public String office;
    Lecturer(String name, String phone, String office) {
        this.name=name;
        this.phone=phone;
        this.office=office;
    }
    @Override
    public String toString() {
        return name;
    }
}
