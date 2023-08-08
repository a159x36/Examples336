//
// Created by martin on 27/09/17.
//
#include <jni.h>
#include <cmath>

// offsets within the bubblearray
#define X 0
#define Y 1
#define VX 2
#define VY 3
#define R 4
#define CR 5

// native code to update the bubble array
extern "C" JNIEXPORT void JNICALL Java_com_example_martin_touchbubbles_BubbleView_nativeUpdate(JNIEnv *env, jobject obj, jint nb,  jint touched,
                                                                                          jint width, jint height, jfloat dt,
                                                                                          jfloat gx, jfloat gy,
                                                                                          jfloatArray bubblearray,
                                                                                          jfloat rigidity, jfloat dampening,
                                                                                          jboolean compress) {
    jboolean iscopy;
    float *mBubbles = env->GetFloatArrayElements(bubblearray,&iscopy);

    for (int i=0;i<nb*6;i+=6) {
        float *bubble=mBubbles+i;
        bubble[X] += bubble[VX] * dt * 100;
        bubble[Y] += bubble[VY] * dt * 100;

        if (bubble[Y] < bubble[R] || bubble[Y] > height-bubble[R]) {
            bubble[Y]-=bubble[VY]*dt*100;
            bubble[VY]=-bubble[VY];
        }
        if (bubble[X] < bubble[R] || bubble[X] > width-bubble[R]) {
            bubble[X]-=bubble[VX]*dt*100;
            bubble[VX]=-bubble[VX];
        }
        if(i!=touched) {
            bubble[VX] += dt * gx * 10;
            bubble[VY] += dt * gy * 10;
        }
    }
    float x,y,r,x1,y1,r1,d,d1;
    float *bubble,*bubble1;
    for (int i=0;i<nb*6;i+=6) {
        bubble=mBubbles+i;
        x=bubble[X];
        y=bubble[Y];
        r=bubble[R];
        bubble[CR]=r;
        for(int j=i+6;j<nb*6;j+=6) {
            bubble1=mBubbles+j;
            x1=bubble1[X];
            y1=bubble1[Y];
            r1=bubble1[R];
            d=(x1-x);
            d1=(y1-y);
            d=d*d+d1*d1;
            d1=(r1+r);
            d1=d1*d1;
            if(d<d1) {
                d = sqrt(d);
                if (compress) {
                    float sz = d - bubble1[CR];
                    if (sz < 8)
                        sz = 8;
                    if (sz < bubble[CR])
                        bubble[CR] = sz;
                }
                float dx = x1 - x;
                float dy = y1 - y;
                if(d!=0) {
                    dx = dx / d;
                    dy = dy / d;
                }
                float displacement = (r + r1) - d;
                if (i != touched) {
                    bubble[VX] = (bubble[VX] - rigidity * dx * displacement) * dampening;
                    bubble[VY] = (bubble[VY] - rigidity * dy * displacement) * dampening;
                }
                if (j != touched) {
                    bubble1[VX] = (bubble1[VX] + rigidity * dx * displacement) * dampening;
                    bubble1[VY] = (bubble1[VY] + rigidity * dy * displacement) * dampening;
                }
            }
        }

    }
    env->ReleaseFloatArrayElements(bubblearray,mBubbles,0);

}