//
// Created by JinBangzhu on 3/1/16.
//

#include "com_cndroid_imagepicker_NDKBitmapOperations.h"

#include <android/log.h>
#include <stdio.h>
#include <android/bitmap.h>


#define  LOG_TAG    "bitmapOperations"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


class JniBitmap {
public:
    uint32_t *_storedBitmapPixels;
    AndroidBitmapInfo _bitmapInfo;

    JniBitmap() {
        _storedBitmapPixels = NULL;
    }
};


JNIEXPORT void JNICALL Java_com_cndroid_imagepicker_NDKBitmapOperations_jniCropBitmap
        (JNIEnv *env, jobject obj, jobject handle, uint32_t left, uint32_t top, uint32_t right,
         uint32_t bottom) {


    JniBitmap *jniBitmap = (JniBitmap *) env->GetDirectBufferAddress(handle);
    if (jniBitmap->_storedBitmapPixels == NULL)
        return;
    uint32_t *previousData = jniBitmap->_storedBitmapPixels;
    uint32_t oldWidth = jniBitmap->_bitmapInfo.width;
    uint32_t newWidth = right - left, newHeight = bottom - top;
    uint32_t *newBitmapPixels = new uint32_t[newWidth * newHeight];
    uint32_t *whereToGet = previousData + left + top * oldWidth;
    uint32_t *whereToPut = newBitmapPixels;
    for (int y = top; y < bottom; ++y) {
        memcpy(whereToPut, whereToGet, sizeof(uint32_t) * newWidth);
        whereToGet += oldWidth;
        whereToPut += newWidth;
    }
    //done copying , so replace old data with new one
    delete[] previousData;
    jniBitmap->_storedBitmapPixels = newBitmapPixels;
    jniBitmap->_bitmapInfo.width = newWidth;
    jniBitmap->_bitmapInfo.height = newHeight;


}