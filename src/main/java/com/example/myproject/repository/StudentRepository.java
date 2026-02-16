package com.example.myproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.myproject.entity.Student;


public interface StudentRepository extends JpaRepository<Student, Long> {
	// search method
	 Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNoContainingIgnoreCase(
            String firstName, 
            String lastName,
            String email,
            String phoneNo,
            Pageable pageable);
	
	// pagination method
	Page<Student> findAll(Pageable pageable);

	
}



