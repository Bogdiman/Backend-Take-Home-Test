-- Create sleep_log table to store user sleep records
CREATE TABLE sleep_log (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    sleep_date DATE NOT NULL,
    bed_time TIMESTAMP NOT NULL,
    wake_time TIMESTAMP NOT NULL,
    total_time_in_bed_minutes INT NOT NULL,
    morning_feeling VARCHAR(10) NOT NULL CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_user_sleep_date UNIQUE (user_id, sleep_date)
);

CREATE INDEX idx_sleep_log_user_id ON sleep_log(user_id);
CREATE INDEX idx_sleep_log_sleep_date ON sleep_log(sleep_date);
CREATE INDEX idx_sleep_log_user_date_range ON sleep_log(user_id, sleep_date DESC);
