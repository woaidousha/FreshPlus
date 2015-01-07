/*
 * Copyright (C) 2009 The Android Open Source Project
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
package com.example.serial;

import com.example.serial.util.SerialLib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class HelloJni extends Activity
{
	private Button open;
	private Button close;
	private Button send;
	private Button recv;
	private TextView tv;
	
	private int fd;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        open = (Button) findViewById(R.id.open);
        close = (Button) findViewById(R.id.close);
        send = (Button) findViewById(R.id.send);
        recv = (Button) findViewById(R.id.recv);
        tv = (TextView) findViewById(R.id.message);
        
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	fd = SerialLib.initSerial();
            }        	
        });
        
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	SerialLib.closeSerial(fd);
            }        	
        });
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	String data = "hahaxiao";
            	SerialLib.sendData(fd, data, data.length());
            }        	
        });
        
        recv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	String data;
            	data = SerialLib.recvData(fd);
            	tv.setText(data);
            }        	
        });

    }

}
