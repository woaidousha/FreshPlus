#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h> //文件控制
#include <termios.h>//终端控制定义
#include <errno.h>
#include <string.h>
#include <jni.h>
#include <assert.h>
#include <android/log.h>

#define DEBUG 1

#if DEBUG
#define  LOG_TAG    "serial"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGI(...)  
#define  LOGE(...) 
#endif

#define DEVICE "/dev/ttyS1"


//打开串口并初始化设置

int  init_serial(void)
{
	int serial_fd = 0;

	serial_fd = open(DEVICE, O_RDWR | O_NOCTTY | O_NONBLOCK);
	//serial_fd = open(DEVICE, O_RDWR | O_NOCTTY );
	if (serial_fd < 0) {
		perror("open");
		return -1;
	}
	
	//串口主要设置结构体termios <termios.h>
	struct termios options;
	
	/**1. tcgetattr函数用于获取与终端相关的参数。
	*参数fd为终端的文件描述符，返回的结果保存在termios结构体中
	*/
	tcgetattr(serial_fd, &options);
	/**2. 修改所获得的参数*/
/*	options.c_cflag |= (CLOCAL | CREAD);//设置控制模式状态，本地连接，接收使能
	options.c_cflag &= ~CSIZE;//字符长度，设置数据位之前一定要屏掉这个位
	options.c_cflag &= ~CRTSCTS;//无硬件流控
	options.c_cflag |= CS8;//8位数据长度
	options.c_cflag &= ~CSTOPB;//1位停止位
	options.c_iflag |= IGNPAR;//无奇偶检验位
	options.c_oflag = 0; //输出模式
	options.c_lflag = 0; //不激活终端模式 */
 	cfmakeraw(&options);
	cfsetospeed(&options, B115200);//设置波特率
	
	/**3. 设置新属性，TCSANOW：所有改变立即生效*/
	tcflush(serial_fd, TCIFLUSH);//溢出数据可以接收，但不读
	tcsetattr(serial_fd, TCSANOW, &options);

	LOGI("serial fd: %d\n", serial_fd);
	
	return serial_fd;
}

/**
*串口发送数据
*@fd:串口描述符
*@data:待发送数据
*@datalen:数据长度
*/

int uart_send(int fd, const char *data, int datalen)
{
	int len = 0;
	int i;

	LOGI("send data len: %d\n", datalen);
	if (datalen > 0) {
		for (i = 0; i < datalen; i++)
			LOGI("%02x ", data[i]);
	}
	len = write(fd, data, datalen);//实际写入的长度
	LOGI("send real data len %d\n", len);
	if(len == datalen) {
		return len;
	} else {
		tcflush(fd, TCOFLUSH);//TCOFLUSH刷新写入的数据但不传送
		return -1;
	}
	
	return 0;
}

/**
*串口接收数据
*要求启动后，在pc端发送ascii文件
*/
int uart_recv(int fd, char *data, int datalen)
{
	int len=0, ret = 0;
	int i;
	
	LOGI("start recv data\n");
	len = read(fd, data, datalen);
	printf("len = %d\n", len);

	LOGI("recv data len: %d\n", len);
	if (len > 0) {
		for (i = 0; i < len; i++)
			LOGI("%02x ", data[i]);
	}
	return len;
}

void close_serial(int fd)
{
	close(fd);
}

int main(int argc, char **argv)
{
	int serial_fd = 0;
	int len = 0;

	serial_fd = init_serial();

	char buf[]="hello world";
	char buf1[100] = {0};
	uart_send(serial_fd, buf, sizeof(buf));
	printf("\n");

	len = uart_recv(serial_fd, buf1, sizeof(buf));
	
	if (len > 0)
		printf("uart receive %s\n", buf1);

	close(serial_fd);

	return 0;
}

jint Java_com_example_serial_util_SerialLib_initSerial(JNIEnv * env, jobject obj)
{
	return init_serial();
}

jint Java_com_example_serial_util_SerialLib_sendData(JNIEnv * env, jobject obj, jint fd, jbyteArray data)
{
	char* pStr = NULL;

	jsize      strLen    = (*env)->GetArrayLength(env, data);
	jbyte      *jBuf     = (*env)->GetByteArrayElements(env, data, JNI_FALSE);

	if (jBuf > 0) {
		pStr = (char*)malloc(strLen + 1);
		if (!pStr) {
			return -1;
		}
		memcpy(pStr, jBuf, strLen);
		pStr[strLen] = 0;
	}

	(*env)->ReleaseByteArrayElements(env, data, jBuf, 0);
	return uart_send(fd, pStr, strLen);
}

jbyteArray Java_com_example_serial_util_SerialLib_recvData(JNIEnv * env, jobject obj, jint fd)
{
	char buf[512] = {0};
	int len = 0;

	len = uart_recv(fd, buf, sizeof(buf)-1);

	if (len > 0 ) {
		jclass     jstrObj   = (*env)->FindClass(env, "java/lang/String");
		jmethodID  methodId  = (*env)->GetMethodID(env, jstrObj, "<init>", "([BLjava/lang/String;)V");
		jbyteArray byteArray = (*env)->NewByteArray(env, len);
		jstring    encode    = (*env)->NewStringUTF(env, "utf-8");
		(*env)->SetByteArrayRegion(env, byteArray, 0, len, (jbyte*)buf);
		//return (jstring)(*env)->NewObject(env, jstrObj, methodId, byteArray, encode);
		return byteArray;
	}
	else
		return NULL;
}

void Java_com_example_serial_util_SerialLib_closeSerial(JNIEnv * env, jobject obj, jint fd)
{
	close_serial(fd);
}


