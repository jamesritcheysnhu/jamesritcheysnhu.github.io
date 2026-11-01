package com.zybooks.eventtracker.repo;
import com.zybooks.eventtracker.model.User;
import androidx.room.*;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User ORDER BY id ASC")
    List<User> getUsers();

    @Query("SELECT * FROM User WHERE id = :userId")
    User getUser(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}