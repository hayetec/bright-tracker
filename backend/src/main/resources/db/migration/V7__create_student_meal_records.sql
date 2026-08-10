CREATE TABLE student_meal_records (
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,
    record_date DATE NOT NULL,

    breakfast_eaten BOOLEAN NOT NULL DEFAULT FALSE,
    lunch_eaten BOOLEAN NOT NULL DEFAULT FALSE,
    dinner_eaten BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_meal_records_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_student_meal_record
        UNIQUE (student_id, record_date)
);

CREATE INDEX idx_student_meal_records_student_id
    ON student_meal_records(student_id);

CREATE INDEX idx_student_meal_records_record_date
    ON student_meal_records(record_date);


CREATE OR REPLACE FUNCTION update_student_meal_records_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER student_meal_records_updated_at_trigger
BEFORE UPDATE ON student_meal_records
FOR EACH ROW
EXECUTE FUNCTION update_student_meal_records_updated_at();
