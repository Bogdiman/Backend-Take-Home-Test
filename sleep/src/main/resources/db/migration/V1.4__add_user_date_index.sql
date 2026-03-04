-- Index to optimize queries filtering by user_id and sleep_date range for the 30 day avg scenario
CREATE INDEX idx_sleep_log_user_date ON sleep_log(user_id, sleep_date);
