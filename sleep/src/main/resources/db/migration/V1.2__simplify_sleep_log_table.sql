-- Drop indexes
DROP INDEX IF EXISTS idx_sleep_log_user_id;
DROP INDEX IF EXISTS idx_sleep_log_sleep_date;
DROP INDEX IF EXISTS idx_sleep_log_user_date_range;

-- Drop unique constraint on user_id and sleep_date since a user can have multiple naps throughout the day
ALTER TABLE sleep_log DROP CONSTRAINT IF EXISTS unique_user_sleep_date;

-- Drop total_time_in_bed_minutes column (can be calculated from bed_time and wake_time)
ALTER TABLE sleep_log DROP COLUMN IF EXISTS total_time_in_bed_minutes;
