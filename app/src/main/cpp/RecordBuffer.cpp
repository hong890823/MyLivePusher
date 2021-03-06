//
// Created by yangw on 2018-9-3.
//

#include <cwchar>
#include "RecordBuffer.h"

RecordBuffer::RecordBuffer(int bufferSize) {
    buffer = new short *[2];
    for(int i = 0; i < 2; i++)
    {
        buffer[i] = new short[bufferSize];
    }
}

short *RecordBuffer::getRecordBuffer() {
    index++;
    if(index > 1)
    {
        index = 1;
    }
    return buffer[index];
}

RecordBuffer::~RecordBuffer() {
    for(int i = 0; i < 2; i++)
    {
        delete  buffer[i];
    }
    delete buffer;
}

short *RecordBuffer::getNowBuffer() {
    return buffer[index];
}
