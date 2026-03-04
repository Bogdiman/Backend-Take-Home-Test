-- Restore unique constraint on user_id and sleep_date
ALTER TABLE sleep_log ADD CONSTRAINT unique_user_sleep_date UNIQUE (user_id, sleep_date);
