//
// Created by yangw on 2018-4-1.
//

#ifndef MYMUSIC_HPCMBEAN_H
#define MYMUSIC_HPCMBEAN_H

#include <SoundTouch.h>

using namespace soundtouch;

class HPcmBean {

public:
    char *buffer;
    int buffsize;

public:
    HPcmBean(SAMPLETYPE *buffer, int size);
    ~HPcmBean();


};


#endif //MYMUSIC_HPCMBEAN_H
