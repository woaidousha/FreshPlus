/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Wrapper for native library

package com.example.serial.util;

public class SerialLib {

     static {
         System.loadLibrary("serial");
     }

    /**
     * @param key the string of key
     */
     public static native int initSerial();
     public static native int sendData(int fd, String data, int dataLen);
     public static native String recvData(int fd);
     public static native void closeSerial(int fd);
}
