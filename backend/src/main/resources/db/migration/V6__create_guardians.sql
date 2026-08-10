CREATE TABLE guardians (
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    email VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE student_guardians (
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,
    guardian_id BIGINT NOT NULL,

    relationship VARCHAR(30) NOT NULL,
    is_primary_contact BOOLEAN NOT NULL DEFAULT FALSE,
    is_emergency_contact BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_guardians_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_guardians_guardian
        FOREIGN KEY (guardian_id)
        REFERENCES guardians(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_student_guardian
        UNIQUE (student_id, guardian_id)
);


CREATE INDEX idx_student_guardians_student_id
    ON student_guardians(student_id);

CREATE INDEX idx_student_guardians_guardian_id
    ON student_guardians(guardian_id);


CREATE OR REPLACE FUNCTION update_guardians_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER guardians_updated_at_trigger
BEFORE UPDATE ON guardians
FOR EACH ROW
EXECUTE FUNCTION update_guardians_updated_at();
