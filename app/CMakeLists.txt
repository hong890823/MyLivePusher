cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

add_library( hpush
             SHARED
             src/main/cpp/hpush.cpp

            src/main/cpp/HQueue.cpp
            src/main/cpp/RtmpPush.cpp

              src/main/cpp/librtmp/amf.c
              src/main/cpp/librtmp/hashswf.c
              src/main/cpp/librtmp/log.c
              src/main/cpp/librtmp/parseurl.c
              src/main/cpp/librtmp/rtmp.c

             src/main/cpp/RecordBuffer.cpp

             )

find_library( log-lib
              log )

target_link_libraries(  hpush
                        OpenSLES
                        ${log-lib} )