//
// Created by Hong on 2018/11/27.
//

#ifndef MYLIVEPUSHER_RTMPPUSH_H
#define MYLIVEPUSHER_RTMPPUSH_H

#include <malloc.h>
#include <string>
#include <android/log.h>
#include "HQueue.h"
extern  "C" {
#include "librtmp/rtmp.h"
};
class RtmpPush {
public:
    RTMP *rtmp = NULL;
    char *url = NULL;
    HQueue *queue = NULL;
    pthread_t push_thread;
public:
    RtmpPush(const char *url);
    ~RtmpPush();

    void init();
};


#endif //MYLIVEPUSHER_RTMPPUSH_H
