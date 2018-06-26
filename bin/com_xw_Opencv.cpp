#include "com_xw_Opencv.h"
#include <opencv2/opencv.hpp>
#include<iostream>
#include <vector>

using namespace cv;
void cutImage(std::vector<char>& img, std::vector<unsigned char>& cimg, int x, int y, int w, int h)
{
	Mat src = imdecode(Mat(img), IMREAD_COLOR);
	Mat target = src(cv::Rect(x, y, w, h));
	imencode(".png", target, cimg);
}
JNIEXPORT jbyteArray JNICALL Java_com_xw_Opencv_cut__J(JNIEnv *env, jclass that, jlong addr) 
{
	//封装
	jbyteArray result = env->NewByteArray(1);
	const jbyte b = 1;
	env->SetByteArrayRegion(result, 0, 1,&b);
	return result;
}

JNIEXPORT jbyteArray JNICALL Java_com_xw_Opencv_cut__JIIII(JNIEnv *env, jclass that, jlong addr, jint x, jint y, jint w, jint h)
{
	//获得源数据
	signed char* data = env->GetByteArrayElements(img, false);
	long data_len = env->GetArrayLength(img);
	
	std::vector<char> data_arr;
	data_arr.reserve(data_len);   //预分配空间
	data_arr.resize(data_len);     //指定元素数目，此处会有其他函数的调用，构造函数，拷贝等。（不够高效）
	memcpy(&data_arr[0], data, data_len * sizeof(signed char)); //内存拷贝

	//处理
	std::vector<unsigned char> cdata_arr;
	cutImage(data_arr, cdata_arr, x, y, w, h);

	//封装
	jbyteArray result = env->NewByteArray(cdata_arr.size());
	env->SetByteArrayRegion(result, 0, cdata_arr.size(), (const jbyte*)cdata_arr.data());

	return result;
}










