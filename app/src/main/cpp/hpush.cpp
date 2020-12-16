#include <jni.h>
#include <string>
#include "android/log.h"

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "RecordBuffer.h"

//引擎
SLObjectItf slObjectEngine = NULL;
SLEngineItf  engineItf = NULL;
//录音器
SLObjectItf  recordObj = NULL;
SLRecordItf  recordItf = NULL;

SLAndroidSimpleBufferQueueItf recorderBufferQueue;

RecordBuffer *recordBuffer;

FILE *pcmFile = NULL;

bool finish = false;

void bqRecorderCallback(SLAndroidSimpleBufferQueueItf bq, void *context){
    fwrite(recordBuffer->getNowBuffer(), 1, 4096, pcmFile);
    if(finish){
        __android_log_print(ANDROID_LOG_DEBUG,"Hong","录制完成","%s");
        (*recordItf)->SetRecordState(recordItf, SL_RECORDSTATE_STOPPED);
        (*recordObj)->Destroy(recordObj);
        recordObj = NULL;
        recordItf = NULL;
        (*slObjectEngine)->Destroy(slObjectEngine);
        slObjectEngine = NULL;
        engineItf = NULL;
        delete(recordBuffer);
    } else{
        __android_log_print(ANDROID_LOG_DEBUG,"Hong","正在录制","%s");
        (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recordBuffer->getRecordBuffer(), 4096);
    }
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_hong_mylivepusher_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hong_mylivepusher_MainActivity_startRecord__Ljava_lang_String_2(JNIEnv *env,
                                                                         jobject instance,
                                                                         jstring path_) {
    if(finish)return;
    const char *path = env->GetStringUTFChars(path_, 0);
    finish = false;
    pcmFile = fopen(path, "w");
    recordBuffer = new RecordBuffer(4096);

    //创建引擎
    slCreateEngine(&slObjectEngine, 0, NULL, 0, NULL, NULL);
    (*slObjectEngine)->Realize(slObjectEngine, SL_BOOLEAN_FALSE);
    (*slObjectEngine)->GetInterface(slObjectEngine, SL_IID_ENGINE, &engineItf);


    SLDataLocator_IODevice loc_dev = {SL_DATALOCATOR_IODEVICE,
                                      SL_IODEVICE_AUDIOINPUT,
                                      SL_DEFAULTDEVICEID_AUDIOINPUT,
                                      NULL};
    SLDataSource audioSrc = {&loc_dev, NULL};


    SLDataLocator_AndroidSimpleBufferQueue loc_bq = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
            2
    };
    SLDataFormat_PCM format_pcm = {
            SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, SL_BYTEORDER_LITTLEENDIAN //小端对齐
    };
    SLDataSink audioSnk = {&loc_bq, &format_pcm};

    const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};

    //根据引擎创建录音器
    (*engineItf)->CreateAudioRecorder(engineItf, &recordObj, &audioSrc, &audioSnk, 1, id, req);
    (*recordObj)->Realize(recordObj, SL_BOOLEAN_FALSE);
    (*recordObj)->GetInterface(recordObj, SL_IID_RECORD, &recordItf);
    //设置队列
    (*recordObj)->GetInterface(recordObj, SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &recorderBufferQueue);
    //入队
    (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recordBuffer->getRecordBuffer(), 4096);
    //队列回调
    (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, bqRecorderCallback, NULL);
    //开始录制
    (*recordItf)->SetRecordState(recordItf, SL_RECORDSTATE_RECORDING);

    env->ReleaseStringUTFChars(path_, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hong_mylivepusher_MainActivity_stopRecord(JNIEnv *env, jobject instance) {
    finish = true;
}

#include "RtmpPush.h"

RtmpPush *rtmpPush = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_hong_mylivepusher_push_HPushVideo_initPush(JNIEnv *env, jobject instance, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    rtmpPush = new RtmpPush(url);
    rtmpPush->init();

    env->ReleaseStringUTFChars(url_, url);
}