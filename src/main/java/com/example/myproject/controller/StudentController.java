package com.example.myproject.controller;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.myproject.entity.Student;
import com.example.myproject.repository.StudentRepository;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students")
    public String viewStudents(@RequestParam(defaultValue = "0")int page, 
    		@RequestParam(required = false) String keyword, Model model) {
    	int pageSize = 5; // show 5 students per page
     Pageable pageable = PageRequest.of(page, pageSize);
     Page<Student> studentPage;
     if(keyword != null && !keyword.isEmpty()) {
    	 studentPage = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNoContainingIgnoreCase(
    			 keyword, keyword, keyword, keyword, pageable);
    	 model.addAttribute("keyword", keyword);
     } else {
    	 studentPage = studentRepository.findAll(pageable);
     }
     
     model.addAttribute("students", studentPage.getContent());
     model.addAttribute("currentPage", page);
     model.addAttribute("totalPages", studentPage.getTotalPages());
     return "students";

    }
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students";
    }
    @GetMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id).orElse(null);
        model.addAttribute("student", student);
        return "edit-student";
    }
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute Student student, @RequestParam("imageFile") MultipartFile file) throws IOException {
    	String uploadDir = "C:/student-photos/";
    	File dir = new File(uploadDir);
    	if(!dir.exists()) {
    		dir.mkdirs();
    	}
    	
    	if(!file.isEmpty()) {
    		// new photo uploaded -> replace old one
    		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    		File saveFile = new File(uploadDir, fileName);
    		file.transferTo(saveFile);
            student.setPhoto(fileName);
    	} else {
    		// no new photo -> keep existing photo
    		Student existingStudent = studentRepository.findById(student.getId()).orElseThrow();
    		student.setPhoto(existingStudent.getPhoto());
    	}
        studentRepository.save(student);
        return "redirect:/students";
    }
    @GetMapping("/students/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }
    @PostMapping("/students/save")
    public String saveStudent(
            Student student,
            @RequestParam("imageFile") MultipartFile file
    ) throws IOException {

        String uploadDir = "C:/student-photos/";
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File saveFile = new File(uploadDir, fileName);
            file.transferTo(saveFile);
            student.setPhoto(fileName);
        }

        studentRepository.save(student);
        return "redirect:/students";
    }

    
    @GetMapping("/students/download")
    public void downloadCSV(HttpServletResponse response) throws IOException {
    	response.setContentType("text/csv");
    	response.setHeader("Content-Disposition", "attachment; filename=students.csv");
    	
    	List<Student> students = studentRepository.findAll();
    	
    	PrintWriter writer = response.getWriter();
    	
    	// CSV Header
    	writer.println("ID,First Name,Last Name,Email,Phone No");
    	
    	//CSV Data
    	for(Student student : students) {
    		writer.println(
    				student.getId() + "," + 
    		        student.getFirstName() + "," +
    				student.getLastName() + "," +
    		        student.getEmail() + "," +
    				student.getPhoneNo());
    	}
    	writer.flush();
    	writer.close();
    }
    
}
