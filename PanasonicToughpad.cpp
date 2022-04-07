
#include "PanasonicToughpad.h"

PanasonicToughpad *PanasonicToughpad::m_instance = nullptr;

static void readBarcodeSF(JNIEnv *env, jobject /*thiz*/, jstring device, jstring symbology, jstring data)
{
    PanasonicToughpad::instance()->device(env->GetStringUTFChars(device, nullptr));
    PanasonicToughpad::instance()->symbology(env->GetStringUTFChars(symbology, nullptr));
    PanasonicToughpad::instance()->data(env->GetStringUTFChars(data, nullptr));
    emit PanasonicToughpad::instance()->barcodeReaded();
}

static void buttonPressedSF(JNIEnv *env, jobject /*thiz*/, jstring button, jboolean state)
{
    emit PanasonicToughpad::instance()->buttonPressed(env->GetStringUTFChars(button, nullptr), state);
}

PanasonicToughpad::PanasonicToughpad(QObject *parent) : QObject(parent)
{
    this->m_instance = this;

    JNINativeMethod barcodeMethods[] {{"readBarcode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", reinterpret_cast<void *>(readBarcodeSF)}};
    QAndroidJniObject barcodeClass("org/alde/sample/panasonictoughpad/ToughpadBarcode");

    JNINativeMethod serviceMethods[] {{"buttonPressed", "(Ljava/lang/String;Z)V", reinterpret_cast<void *>(buttonPressedSF)}};
    QAndroidJniObject serviceClass("org/alde/sample/panasonictoughpad/ButtonService");

    jclass objectClass = m_env->GetObjectClass(barcodeClass.object<jobject>());
    this->m_env->RegisterNatives(objectClass,
                         barcodeMethods,
                         sizeof(barcodeMethods) / sizeof(barcodeMethods[0]));
    this->m_env->DeleteLocalRef(objectClass);

    objectClass = this->m_env->GetObjectClass(serviceClass.object<jobject>());
    this->m_env->RegisterNatives(objectClass,
                         serviceMethods,
                         sizeof(serviceMethods) / sizeof(serviceMethods[0]));
    this->m_env->DeleteLocalRef(objectClass);

    auto obj = QAndroidJniObject::callStaticObjectMethod(
            "org/alde/sample/panasonictoughpad/PanasonicToughpad",
            "createToughpadBarcode",
            "(Landroid/content/Context;)Lorg/alde/sample/panasonictoughpad/ToughpadBarcode;",
            QtAndroid::androidContext().object());

    jclass cls = this->m_env->GetObjectClass( obj.object<jobject>() );

    m_pressSoftwareTriggerMethod = this->m_env->GetMethodID( cls, "pressSoftwareTrigger", "(Z)V" );
    m_enableReaderMethod = this->m_env->GetMethodID( cls, "enableReader", "()V" );

    m_objectRef = this->m_env->NewGlobalRef( obj.object<jobject>() );
}

void PanasonicToughpad::enableReader()
{
    this->m_env->CallVoidMethod( m_objectRef, m_enableReaderMethod);
}

void PanasonicToughpad::pressSoftwareTrigger(bool toggle)
{
    this->m_env->CallVoidMethod( m_objectRef, m_pressSoftwareTriggerMethod, toggle);
}
