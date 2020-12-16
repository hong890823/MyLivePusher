//
// Created by Hong on 2018/11/27.
//

#ifndef MYLIVEPUSHER_HQUEUE_H
#define MYLIVEPUSHER_HQUEUE_H

#include "queue"
#include "pthread.h"

extern "C" {
#include "librtmp/rtmp.h"
};

class HQueue {
public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;

public:
    HQueue();
    ~HQueue();

    int putRtmpPacket(RTMPPacket *packet);

    RTMPPacket* getRtmpPacket();

    void clearQueue();

    void notifyQueue();
};


#endif //MYLIVEPUSHER_HQUEUE_H
