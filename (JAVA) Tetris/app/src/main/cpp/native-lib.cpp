#include <jni.h>
#include <string>


const int M = 20;
const int N = 10;

int field[M][N] = {0};


struct Point{int x,y;}
        a[4], b[4];

int figures[7][4] =
        {
                1,3,5,7, // I
                2,4,5,7, // Z
                3,5,4,6, // S
                3,5,4,7, // T
                2,3,5,7, // L
                3,5,7,6, // J
                2,3,4,5, // O
        };




extern "C"
JNIEXPORT jint JNICALL
Java_com_gtari_deltatechenologie_tetromino_RunningManAnimation_ax(
        JNIEnv *env,
        jobject, /* this */
         jint j) {

    int n=2;
    for(int i=0;i<4;i++){
        a[i].x=figures[n][i]%2;
    }

    return  a[j].x;
}





extern "C"
JNIEXPORT jint JNICALL
Java_com_gtari_deltatechenologie_tetromino_RunningManAnimation_ay(
        JNIEnv *env,
        jobject, /* this */
        jint j) {

    int n=3;
    for(int i=0;i<4;i++){
        a[i].y=figures[n][i]/2;
    }


    return  a[j].y;
}


// Move puzzel

extern "C"
JNIEXPORT void JNICALL
Java_com_gtari_deltatechenologie_tetromino_RunningManAnimation_Move(
        JNIEnv *env,
        jobject, /* this */
        jint dx) {

    for(int i=0;i<4;i++){
        a[i].x+=dx;
    }
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_gtari_deltatechenologie_tetromino_GameMenuActivity_getPackageAppName(
        JNIEnv *env,
        jobject /* this */) {
    std::string idn = "omi";
    std::string idi = "tar";
    std::string idb = "gi";
    std::string idh = "elt";
    std::string idg = "i.d";
    std::string idm = "tr";
    std::string idc = "ate";
    std::string idj = "chen";
    std::string idk = "co";
    std::string idl = "e.te";
    std::string ido = "no";
    std::string idf = "m.g";
    std::string idd = "olo";
    std::string pck= idk+idf+idi+idg+idh+idc+idj+idd+idb+idl+idm+idn+ido;
    return env->NewStringUTF(pck.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_gtari_deltatechenologie_tetromino_GameSurface_field(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ) {


    return  field[i][j];
}


extern "C"
JNIEXPORT void JNICALL
Java_com_gtari_deltatechenologie_tetromino_GameSurface_setField(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ,
        jint colorNum) {


    field[i][j]=colorNum;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_gtari_deltatechenologie_tetromino_RunningManAnimation_field(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ) {


    return  field[i][j];
}


extern "C"
JNIEXPORT void JNICALL
Java_com_gtari_deltatechenologie_tetromino_RunningManAnimation_setField(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ,
        jint colorNum) {


    field[i][j]=colorNum;
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_gtari_deltatechenologie_tetromino_MainActivity_field(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ) {


    return  field[i][j];
}


extern "C"
JNIEXPORT void JNICALL
Java_com_gtari_deltatechenologie_tetromino_MainActivity_setField(
        JNIEnv *env,
        jobject, /* this */
        jint i,
        jint j ,
        jint colorNum) {


    field[i][j]=colorNum;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_gtari_deltatechenologie_tetromino_GameSurface_emptyField(
        JNIEnv *env,
        jobject /* this */) {

     field[M][N] = {0};
}


