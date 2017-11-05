package com.example.android.trackme.data;

import android.provider.BaseColumns;

/**
 * Created by tanujanuj on 06/09/17.
 */

public class RegisterContract {
    public static final class  RegisterEntry implements BaseColumns {
        public static final String TABLE_NAME="userdetails";
        public static final String COLUMN_PUI="pid";
        public static final String COLUMN_MEETING_COUNT="meetingcount";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_EMAIL="email";
        public static final String COLUMN_DEPARTMENT="department";
        public static final String COLUMN_DESIGNATION="designation";



    }

}
