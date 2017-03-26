package com.lmu.pmg.sdiapp;

/**
 * Created by Jonas Mattes on 16.01.2016.
 */
public class Log {


    private enum LogLevel {Debug(0), Warn(1), Error(2), None(3);
        int m_nLevel;
        LogLevel(int i_nLevel){
            m_nLevel = i_nLevel;
        }
        int getLevel(){return m_nLevel;}
    }


    /**
     * *********************************************************************
     * Set LogLevel here.
     * LogLevel.Debug   will print all logs
     * LogLevel.Warn    will print warnings and errors only
     * LogLevel.Error   will print errors only
     * LogLevel.None    will disable any logging
     * *********************************************************************
     */

    private static final LogLevel e_LOG_LEVEL = LogLevel.Debug;


    /**
     * Logs a DEBUG message if LogLevel is Debug
     * LogLevel is currently set to {@link #e_LOG_LEVEL}
     * @param tag According tag. Class name (getSimpleName()) is recommended.
     * @param message Debug message to log.
     */
    public static void d (String tag, String message){
        if(e_LOG_LEVEL.getLevel() <= LogLevel.Debug.getLevel()) {
            android.util.Log.d(tag, message);
        }
    }

    /**
     * Logs a WARN message if LogLevel is Warn at least
     * LogLevel is currently set to {@link #e_LOG_LEVEL}
     * @param tag According tag. Class name (getSimpleName()) is recommended.
     * @param message Warning message to log.
     */
    public static void w (String tag, String message) {
        if (e_LOG_LEVEL.getLevel() <= LogLevel.Warn.getLevel()){
            android.util.Log.w(tag, message);
        }
    }

    /**
     * Logs an ERROR message if LogLevel is Error at least
     * LogLevel is currently set to {@link #e_LOG_LEVEL}
     * @param tag According tag. Class name (getSimpleName()) is recommended.
     * @param message Error message to log
     */
    public static void e (String tag, String message){
        if(e_LOG_LEVEL.getLevel() <= LogLevel.Error.getLevel()) {
            android.util.Log.e(tag, message);
        }
    }

}
