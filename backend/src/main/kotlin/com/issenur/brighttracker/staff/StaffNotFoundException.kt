package com.issenur.brighttracker.staff

class StaffNotFoundException(id: Long) :
    RuntimeException("Staff member with id $id was not found")