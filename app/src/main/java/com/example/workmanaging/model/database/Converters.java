package com.example.workmanaging.model.database;

import androidx.room.TypeConverter;
import java.util.Date;
import com.example.workmanaging.model.entity.JobStatus;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static JobStatus toStatus(String value) {
        return value == null ? null : JobStatus.valueOf(value);
    }
    @TypeConverter
    public static String fromStatus(JobStatus status) {
        return status == null ? null : status.name();
    }
}
