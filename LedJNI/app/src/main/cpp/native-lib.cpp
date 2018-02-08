#include <jni.h>
#include <string>
#include <fcntl.h>
#include <unistd.h>

#define LED_ON "1"
#define LED_OFF "0"



extern "C" {


    JNIEXPORT jstring JNICALL
    Java_com_wangwenjun_ledjni_MainActivity_stringFromJNI(
            JNIEnv *env,
            jobject /* this */) {
        std::string hello = "Hello from C++";
        return env->NewStringUTF(hello.c_str());


    }

    JNIEXPORT jint JNICALL
    Java_com_wangwenjun_ledjni_MainActivity_ledOn(JNIEnv *env, jclass instance) {

        int led;
        ssize_t ret = 0;

        led = open("/sys/led_sysfs/led_ctl", O_WRONLY);
        //led = open("/sys/class/leds/led-mt6750/brightness", O_WRONLY);
        if (led == -1) {
            printf("open failed\n");
            return led;
        }
        ret = write(led, LED_ON, (int) sizeof(LED_ON));
        if (ret < 0)
            printf("write failed");
        return (jint) ret;

    }

    JNIEXPORT jint JNICALL
    Java_com_wangwenjun_ledjni_MainActivity_ledOff(JNIEnv *env, jobject instance) {

        int led;
        ssize_t ret = 0;

        led = open("/sys/led_sysfs/led_ctl", O_WRONLY);
        //led = open("/sys/class/leds/led-mt6750/brightness", O_WRONLY);

        ret = write(led, LED_OFF, (int) sizeof(LED_OFF));

        return (jint) ret;

    }
};