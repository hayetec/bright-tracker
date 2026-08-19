ALTER TABLE student_meal_records
    RENAME COLUMN breakfast_eaten TO am_snack_eaten;

ALTER TABLE student_meal_records
    RENAME COLUMN dinner_eaten TO pm_snack_eaten;