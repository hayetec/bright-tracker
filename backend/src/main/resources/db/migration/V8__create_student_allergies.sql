CREATE TABLE student_allergies (
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,
    allergen VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    notes VARCHAR(500),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_allergies_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_student_allergies_student_id
    ON student_allergies(student_id);

CREATE UNIQUE INDEX uq_student_allergy
    ON student_allergies(student_id, LOWER(allergen));

CREATE OR REPLACE FUNCTION update_student_allergies_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER student_allergies_updated_at_trigger
BEFORE UPDATE ON student_allergies
FOR EACH ROW
EXECUTE FUNCTION update_student_allergies_updated_at();
