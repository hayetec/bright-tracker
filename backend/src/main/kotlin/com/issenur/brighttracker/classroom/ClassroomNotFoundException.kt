package com.issenur.brighttracker.classroom

class ClassroomNotFoundException(id: Long) :
    RuntimeException("Classroom with id $id was not found")