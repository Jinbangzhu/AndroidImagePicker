LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog
LOCAL_LDFLAGS += -ljnigraphics

LOCAL_MODULE := JniBitmapOperationsLibrary
LOCAL_SRC_FILES := JniBitmapOperationsLibrary.cpp
include $(BUILD_SHARED_LIBRARY)
