package com.issenur.brighttracker.student

class StudentNotFoundException(id: Long) :
    RuntimeException("Student with id $id was not found")