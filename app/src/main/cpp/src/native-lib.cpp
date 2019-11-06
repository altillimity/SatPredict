#include <jni.h>
#include <string>
#include <predict/predict.h>
#include <chrono>
#include <math.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_altillimity_satpredict_activities_Satellite_getCurrentSatPos(
    JNIEnv *env,
    jobject /* this */,
    jstring tle1_j,
    jstring tle2_j)
{
    std::string result;

    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(tle1_j, &isCopy);
    std::string stringtle1 = std::string(convertedValue);

    convertedValue = (env)->GetStringUTFChars(tle2_j, &isCopy);
    std::string stringtle2 = std::string(convertedValue);

    predict_orbital_elements_t *sat = predict_parse_tle(stringtle1.c_str(), stringtle2.c_str());
    if (!sat)
    {
        exit(1);
    }

    predict_observer_t *obs = predict_create_observer("Me", 48.7778 * M_PI / 180.0, 1.81 * M_PI / 180.0, 0);
    if (!obs)
    {
        exit(1);
    }

    predict_julian_date_t curr_time = predict_to_julian(time(NULL));

    struct predict_position sat_orbit;
    predict_orbit(sat, &sat_orbit, curr_time);

    struct predict_observation observer;
    predict_observe_orbit(obs, &sat_orbit, &observer);

    result = std::to_string((sat_orbit.latitude * 180.0 / M_PI) >= 180 ? (sat_orbit.latitude * 180.0 / M_PI) - 360 : (sat_orbit.latitude * 180.0 / M_PI)) + ":" + std::to_string((sat_orbit.longitude * 180.0 / M_PI) >= 180 ? (sat_orbit.longitude * 180.0 / M_PI) - 360 : (sat_orbit.longitude * 180.0 / M_PI)) + ":" + std::to_string(observer.elevation * 180.0 / M_PI);

    predict_destroy_orbital_elements(sat);
    //predict_destroy_observer(obs);

    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_altillimity_satpredict_activities_Satellite_getSatPosAtTime(
        JNIEnv *env,
        jobject /* this */,
        jstring tle1_j,
        jstring tle2_j,
        jlong time_j)
{
    std::string result;

    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(tle1_j, &isCopy);
    std::string stringtle1 = std::string(convertedValue);

    convertedValue = (env)->GetStringUTFChars(tle2_j, &isCopy);
    std::string stringtle2 = std::string(convertedValue);

    predict_orbital_elements_t *sat = predict_parse_tle(stringtle1.c_str(), stringtle2.c_str());
    if (!sat)
    {
        exit(1);
    }

    predict_observer_t *obs = predict_create_observer("Me", 48.7778 * M_PI / 180.0, 1.81 * M_PI / 180.0, 0);
    if (!obs)
    {
        exit(1);
    }

    predict_julian_date_t curr_time = predict_to_julian(time_j);

    struct predict_position sat_orbit;
    predict_orbit(sat, &sat_orbit, curr_time);

    struct predict_observation observer;
    predict_observe_orbit(obs, &sat_orbit, &observer);

    result = std::to_string((sat_orbit.latitude * 180.0 / M_PI) >= 180 ? (sat_orbit.latitude * 180.0 / M_PI) - 360 : (sat_orbit.latitude * 180.0 / M_PI)) + ":" + std::to_string((sat_orbit.longitude * 180.0 / M_PI) >= 180 ? (sat_orbit.longitude * 180.0 / M_PI) - 360 : (sat_orbit.longitude * 180.0 / M_PI));

    predict_destroy_orbital_elements(sat);
    //predict_destroy_observer(obs);

    return env->NewStringUTF(result.c_str());
}
