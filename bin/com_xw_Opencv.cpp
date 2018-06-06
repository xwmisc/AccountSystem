#include "com_xw_Opencv.h"
#include <opencv2/opencv.hpp>
#include<iostream>
#include <vector>

#pragma comment(lib,"opencv_core400d.lib")  
#pragma comment(lib,"opencv_imgcodecs400d.lib")  
#pragma comment(lib,"opencv_imgproc400d.lib")  
using namespace cv;
void cutImage(std::vector<char>& img, std::vector<unsigned char>& cimg, int x, int y, int w, int h)
{
	Mat src = imdecode(Mat(img), IMREAD_COLOR);
	Mat target = src(cv::Rect(x, y, w, h));
	imencode(".png", target, cimg);
}


JNIEXPORT jbyteArray JNICALL Java_com_xw_Opencv_cut(JNIEnv *env, jclass that, jbyteArray img, jint x, jint y, jint w, jint h)
{
	//���Դ����
	signed char* data = env->GetByteArrayElements(img, false);
	long data_len = env->GetArrayLength(img);

	std::vector<char> data_arr;
	data_arr.reserve(data_len);   //Ԥ����ռ�
	data_arr.resize(data_len);     //ָ��Ԫ����Ŀ���˴��������������ĵ��ã����캯���������ȡ���������Ч��
	memcpy(&data_arr[0], data, data_len * sizeof(signed char)); //�ڴ濽��

	//����
	std::vector<unsigned char> cdata_arr;
	cutImage(data_arr, cdata_arr, x, y, w, h);

	//��װ
	jbyteArray result = env->NewByteArray(cdata_arr.size());
	env->SetByteArrayRegion(result, 0, cdata_arr.size(), (const jbyte*)cdata_arr.data());

	return result;
}










