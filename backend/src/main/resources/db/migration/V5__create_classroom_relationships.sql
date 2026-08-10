CREATE TABLE student_enrollments (
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,
    classroom_id BIGINT NOT NULL,

    enrolled_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_enrollments_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_enrollments_classroom
        FOREIGN KEY (classroom_id)
        REFERENCES classrooms(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_student_classroom_enrollment
        UNIQUE (student_id, classroom_id)
);


CREATE TABLE classroom_staff_assignments (
    id BIGSERIAL PRIMARY KEY,

    classroom_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,

    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_classroom_staff_classroom
        FOREIGN KEY (classroom_id)
        REFERENCES classrooms(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_classroom_staff_staff
        FOREIGN KEY (staff_id)
        REFERENCES staff(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_classroom_staff_assignment
        UNIQUE (classroom_id, staff_id)
);


CREATE INDEX idx_student_enrollments_student_id
    ON student_enrollments(student_id);

CREATE INDEX idx_student_enrollments_classroom_id
    ON student_enrollments(classroom_id);

CREATE INDEX idx_classroom_staff_assignments_classroom_id
    ON classroom_staff_assignments(classroom_id);

CREATE INDEX idx_classroom_staff_assignments_staff_id
    ON classroom_staff_assignments(staff_id);
