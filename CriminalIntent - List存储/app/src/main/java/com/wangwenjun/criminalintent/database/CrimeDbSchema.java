package com.wangwenjun.criminalintent.database;

/**
 * Created by Administrator on 2018/1/16.
 */

public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME = "crimes";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOVLED = "sovled";
        }
    }
}
