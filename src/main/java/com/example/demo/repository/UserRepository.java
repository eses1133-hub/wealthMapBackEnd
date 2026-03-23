package com.example.demo.repository;

<<<<<<< Updated upstream
import java.util.Optional;

=======
>>>>>>> Stashed changes
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
<<<<<<< Updated upstream
	
	//確認是否有註冊過
    Optional<User> findByEmail(String email);
=======
>>>>>>> Stashed changes

}