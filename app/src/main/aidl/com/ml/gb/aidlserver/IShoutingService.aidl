// IShoutingService.aidl
package com.ml.gb.aidlserver;

// Declare any non-default types here with import statements

/** Example service interface */
interface IShoutingService {
    const int SHOUT_0 = 0;
    const int SHOUT_1 = 1;
    const int SHOUT_2 = 2;

    const String ACTION = "com.ml.gb.aidlserver.ShoutingService";

    /** Request the process ID of this service, to do evil things with it. */
    int getPid();

    /** Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


    String shout(int shoutType);
}